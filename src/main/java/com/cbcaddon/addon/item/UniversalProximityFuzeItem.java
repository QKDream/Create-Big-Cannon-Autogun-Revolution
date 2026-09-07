package com.cbcaddon.addon.item;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Locale;
import java.util.function.BooleanSupplier;

import com.cbcaddon.addon.client.ClientTooltipUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;
import rbasamoyai.createbigcannons.config.CBCConfigs;
import rbasamoyai.createbigcannons.index.CBCDataComponents;
import rbasamoyai.createbigcannons.index.CBCMenuTypes;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile;
import rbasamoyai.createbigcannons.munitions.ProjectileContext;
import rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem;
import rbasamoyai.createbigcannons.munitions.fuzes.ProximityFuzeContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UniversalProximityFuzeItem extends FuzeItem implements MenuProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger("cbcaddon:UniversalProximityFuze");
    private static int shaolibDebugCounter = 0;

    public UniversalProximityFuzeItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onProjectileImpact(ItemStack stack, AbstractCannonProjectile proj, HitResult result,
                                      AbstractCannonProjectile.ImpactResult impactResult, boolean flag) {
        return !flag;
    }

    @Override
    public boolean onProjectileExpiry(ItemStack stack, AbstractCannonProjectile proj) {
        return true;
    }

    @Override
    public boolean onProjectileTick(ItemStack stack, AbstractCannonProjectile proj) {
        int airTime = stack.getOrDefault(CBCDataComponents.AIR_TIME, 0);
        if (airTime > CBCConfigs.server().munitions.proximityFuzeArmingTime.get()) {
            stack.set(CBCDataComponents.ARMED, true);
        }
        stack.set(CBCDataComponents.AIR_TIME, ++airTime);
        return false;
    }

    @Override
    public boolean onProjectileClip(ItemStack stack, AbstractCannonProjectile proj, Vec3 v0, Vec3 v1,
                                    ProjectileContext context, boolean flag) {
        if (flag) return false;
        if (!stack.has(CBCDataComponents.ARMED)) return false;
        double radius = Math.max(1.0, stack.getOrDefault(CBCDataComponents.DETONATION_DISTANCE, 1));
        Vec3 detonationPosition = findSableTarget(proj.level(), proj, v0, v1, radius);
        if (detonationPosition == null) {
            detonationPosition = findMissileTarget(proj.level(), proj, v0, v1, radius);
        }
        if (detonationPosition == null) {
            detonationPosition = findShaolibTarget(proj.level(), proj, v0, v1, radius);
        }
        if (detonationPosition != null) {
            context.setDetonationPositionForClip(detonationPosition);
            return true;
        }
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer && player.mayBuild()) {
            ItemStack stack = player.getItemInHand(hand);
            if (!stack.has(CBCDataComponents.DETONATION_DISTANCE)) {
                stack.set(CBCDataComponents.DETONATION_DISTANCE, 1);
            }
            int distance = stack.getOrDefault(CBCDataComponents.DETONATION_DISTANCE, 1);
            CBCMenuTypes.SET_PROXIMITY_FUZE.open(serverPlayer, getDisplayName(), this, buffer -> {
                buffer.writeVarInt(distance);
                ItemStack.STREAM_CODEC.encode(buffer, new ItemStack(this));
            });
        }
        return super.use(level, player, hand);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return ProximityFuzeContainer.getServerMenu(containerId, inventory, player.getMainHandItem());
    }

    @Override
    public Component getDisplayName() {
        return getDescription();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        int distance = Math.max(1, stack.getOrDefault(CBCDataComponents.DETONATION_DISTANCE, 1));
        tooltip.add(Component.translatable("tooltip.cbcaddon.universal_proximity_fuze.desc"));
        boolean sneaking = false;
        try {
            if (FMLEnvironment.dist.isClient()) {
                sneaking = ((BooleanSupplier) (ClientTooltipUtils::isPlayerSneaking)).getAsBoolean();
            }
        } catch (Throwable ignored) {
        }
        if (sneaking) {
            tooltip.add(Component.translatable("tooltip.cbcaddon.universal_proximity_fuze.distance", distance));
            tooltip.add(Component.translatable("tooltip.cbcaddon.universal_proximity_fuze.targets"));
        } else {
            tooltip.add(Component.translatable("tooltip.cbcaddon.universal_proximity_fuze.hint"));
        }
    }

    @Override
    public void addExtraInfo(List<Component> tooltip, boolean flag, ItemStack stack) {
        super.addExtraInfo(tooltip, flag, stack);
        int distance = Math.max(1, stack.getOrDefault(CBCDataComponents.DETONATION_DISTANCE, 1));
        tooltip.add(Component.translatable("tooltip.cbcaddon.universal_proximity_fuze.distance", distance));
        tooltip.add(Component.translatable("tooltip.cbcaddon.universal_proximity_fuze.targets"));
    }

    private static Vec3 findSableTarget(Level level, AbstractCannonProjectile proj, Vec3 segStart, Vec3 segEnd, double radius) {
        if (!(level instanceof ServerLevel serverLevel)) return null;
        try {
            SableApi api = SableApi.resolve();
            if (api == null) return null;
            Object container = api.getContainer.invoke(null, serverLevel);
            if (container == null) return null;
            List<?> subLevels = (List<?>) api.getAllSubLevels.invoke(container);
            if (subLevels == null || subLevels.isEmpty()) return null;
            for (Object subLevel : subLevels) {
                if (subLevel == null) continue;
                if ((Boolean) api.isRemoved.invoke(subLevel)) continue;
                Object boundingBox = api.boundingBox.invoke(subLevel);
                if (boundingBox == null) continue;
                AABB structureBox = new AABB(
                        (Double) api.minX.invoke(boundingBox),
                        (Double) api.minY.invoke(boundingBox),
                        (Double) api.minZ.invoke(boundingBox),
                        (Double) api.maxX.invoke(boundingBox),
                        (Double) api.maxY.invoke(boundingBox),
                        (Double) api.maxZ.invoke(boundingBox));
                if (segmentDistanceSqrToBox(segStart, segEnd, structureBox) <= radius * radius) {
                    return closestPointOnAABB(structureBox, proj.position());
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static Vec3 findMissileTarget(Level level, AbstractCannonProjectile proj, Vec3 segStart, Vec3 segEnd, double radius) {
        AABB searchBox = new AABB(segStart, segEnd).inflate(Math.max(radius, 4.0));
        double bestAlongSqr = Double.MAX_VALUE;
        Vec3 bestPosition = null;
        for (Entity entity : level.getEntities(proj, searchBox, e -> e != proj && !e.isRemoved())) {
            if (!isMissileEntity(entity)) continue;
            Vec3 missileEnd = entity.position();
            Vec3 missileStart = missileEnd.subtract(entity.getDeltaMovement());
            Vec3 closestOnShell = closestPointBetweenSegments(segStart, segEnd, missileStart, missileEnd);
            Vec3 closestOnMissile = closestPointOnSegment(missileStart, missileEnd, closestOnShell);
            if (closestOnShell.distanceToSqr(closestOnMissile) <= radius * radius) {
                double alongSqr = closestOnShell.distanceToSqr(segStart);
                if (alongSqr < bestAlongSqr) {
                    bestAlongSqr = alongSqr;
                    bestPosition = closestOnShell;
                }
            }
        }
        return bestPosition;
    }

    private static Vec3 findShaolibTarget(Level level, AbstractCannonProjectile proj, Vec3 segStart, Vec3 segEnd, double radius) {
        if (!(level instanceof ServerLevel)) return null;
        try {
            ShaolibApi api = ShaolibApi.resolve();
            if (api == null) {
                if (shaolibDebugCounter++ < 5) LOGGER.warn("[CBCAddon] UniversalProximityFuze: Shaolib API unavailable, skipping TAOV detection");
                return null;
            }
            Collection<?> projectiles = (Collection<?>) api.activeProjectiles.invoke(null);
            if (projectiles == null || projectiles.isEmpty()) return null;
            String dimensionId = level.dimension().location().toString();
            double bestAlongSqr = Double.MAX_VALUE;
            Vec3 bestPosition = null;
            boolean sawTaovMissile = false;
            for (Object projectile : projectiles) {
                if (projectile == null) continue;
                if (!(Boolean) api.instanceIsAlive.invoke(projectile)) continue;
                if ((Boolean) api.instanceIsDiscarded.invoke(projectile)) continue;
                Object instanceDimension = api.instanceDimensionId.invoke(projectile);
                if (!dimensionId.equals(instanceDimension)) {
                    if (shaolibDebugCounter++ < 10) LOGGER.warn("[CBCAddon] UniversalProximityFuze: dimension mismatch shell={} projectile={}", dimensionId, instanceDimension);
                    continue;
                }
                Object typeId = api.typeId.invoke(api.instanceType.invoke(projectile));
                if (!(typeId instanceof ResourceLocation resourceLocation)) continue;
                if (!"taov_weapons".equals(resourceLocation.getNamespace())) continue;
                String path = resourceLocation.getPath();
                if (!"tau_missile".equals(path) && !"hellfire_missile".equals(path)) continue;
                sawTaovMissile = true;
                Vec3 missileEnd = (Vec3) api.instancePosition.invoke(projectile);
                Vec3 missileStart = (Vec3) api.instancePreviousPosition.invoke(projectile);
                if ((Boolean) api.instanceIsEmbedded.invoke(projectile)) {
                    if (api.bodyQueries == null) continue;
                    Object optional = api.instanceEmbeddedWorldPosition.invoke(projectile, level, api.bodyQueries);
                    if (optional instanceof Optional<?> worldPosition && worldPosition.isPresent()) {
                        missileEnd = (Vec3) worldPosition.get();
                        missileStart = missileEnd;
                    } else {
                        continue;
                    }
                }
                if (shaolibDebugCounter++ < 10) LOGGER.info("[CBCAddon] UniversalProximityFuze: found flying {} at {} (shell segment {} -> {})", path, missileEnd, segStart, segEnd);
                Vec3 closestOnShell = closestPointBetweenSegments(segStart, segEnd, missileStart, missileEnd);
                Vec3 closestOnMissile = closestPointOnSegment(missileStart, missileEnd, closestOnShell);
                if (closestOnShell.distanceToSqr(closestOnMissile) <= radius * radius) {
                    double alongSqr = closestOnShell.distanceToSqr(segStart);
                    if (alongSqr < bestAlongSqr) {
                        bestAlongSqr = alongSqr;
                        bestPosition = closestOnShell;
                    }
                }
            }
            if (!sawTaovMissile && projectiles.size() > 0 && shaolibDebugCounter++ < 10) {
                StringBuilder sample = new StringBuilder();
                int shown = 0;
                for (Object projectile : projectiles) {
                    if (shown >= 3 || projectile == null) break;
                    try {
                        if (!(Boolean) api.instanceIsAlive.invoke(projectile)) continue;
                        sample.append(api.typeId.invoke(api.instanceType.invoke(projectile))).append(' ');
                        shown++;
                    } catch (Throwable ignored) {
                    }
                }
                LOGGER.warn("[CBCAddon] UniversalProximityFuze: {} active Shaolib projectiles, none is taov missile; sample: {}", projectiles.size(), sample.toString().trim());
            }
            return bestPosition;
        } catch (Throwable t) {
            if (shaolibDebugCounter++ < 5) LOGGER.warn("[CBCAddon] UniversalProximityFuze: Shaolib detection failed", t);
            return null;
        }
    }

    private static boolean isMissileEntity(Entity entity) {
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        String namespace = id == null ? "" : id.getNamespace();
        String key = id == null ? "" : id.getPath();
        if ("vestalihy".equals(namespace)) {
            return "ptur".equals(key) || "tow".equals(key) || "ptur_jet".equals(key) || "malytka".equals(key);
        }
        if ("firecontrolcompat".equals(namespace)) {
            return key.endsWith("_tanshe") || key.contains("missile");
        }
        String lower = (namespace + ":" + key).toLowerCase(Locale.ROOT);
        boolean mianbao = lower.startsWith("mianbaos_modernwarfare:") || lower.contains("tanshe");
        if (mianbao) {
            return key.contains("missile") || key.contains("rocket") || key.startsWith("agm_") || key.startsWith("jdam");
        }
        if ("cbcmoreshells".equals(namespace)) {
            return key.contains("torpedo") || key.contains("rocket") || key.contains("depth_charge")
                    || key.contains("depthcharge") || key.contains("bomb") || key.contains("missile");
        }
        return key.contains("missile") || key.contains("rocket");
    }

    private static Vec3 closestPointBetweenSegments(Vec3 a1, Vec3 a2, Vec3 b1, Vec3 b2) {
        Vec3 p = closestPointOnSegment(a1, a2, b1);
        for (int i = 0; i < 4; i++) {
            Vec3 q = closestPointOnSegment(b1, b2, p);
            p = closestPointOnSegment(a1, a2, q);
        }
        return p;
    }
    private static double segmentDistanceSqrToBox(Vec3 segStart, Vec3 segEnd, AABB box) {
        Vec3 p = closestPointOnSegment(segStart, segEnd, box.getCenter());
        for (int i = 0; i < 3; i++) {
            Vec3 q = closestPointOnAABB(box, p);
            p = closestPointOnSegment(segStart, segEnd, q);
        }
        Vec3 q = closestPointOnAABB(box, p);
        return p.distanceToSqr(q);
    }

    private static Vec3 closestPointOnSegment(Vec3 a, Vec3 b, Vec3 p) {
        Vec3 ab = b.subtract(a);
        double lengthSqr = ab.lengthSqr();
        double t = lengthSqr < 1.0E-8 ? 0.0 : Mth.clamp(p.subtract(a).dot(ab) / lengthSqr, 0.0, 1.0);
        return a.add(ab.scale(t));
    }

    private static Vec3 closestPointOnAABB(AABB box, Vec3 p) {
        return new Vec3(
                Mth.clamp(p.x, box.minX, box.maxX),
                Mth.clamp(p.y, box.minY, box.maxY),
                Mth.clamp(p.z, box.minZ, box.maxZ));
    }

    private static final class SableApi {
        final Method getContainer;
        final Method getAllSubLevels;
        final Method boundingBox;
        final Method isRemoved;
        final Method minX;
        final Method minY;
        final Method minZ;
        final Method maxX;
        final Method maxY;
        final Method maxZ;

        private SableApi(Method getContainer, Method getAllSubLevels, Method boundingBox, Method isRemoved,
                         Method minX, Method minY, Method minZ, Method maxX, Method maxY, Method maxZ) {
            this.getContainer = getContainer;
            this.getAllSubLevels = getAllSubLevels;
            this.boundingBox = boundingBox;
            this.isRemoved = isRemoved;
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
        }

        private static volatile SableApi instance;
        private static volatile boolean resolved;

        static SableApi resolve() {
            if (!resolved) {
                synchronized (SableApi.class) {
                    if (!resolved) {
                        try {
                            ClassLoader loader = Thread.currentThread().getContextClassLoader();
                            Class<?> containerClass = Class.forName("dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer", false, loader);
                            Class<?> subLevelClass = Class.forName("dev.ryanhcode.sable.sublevel.SubLevel", false, loader);
                            Class<?> boundingBoxClass = Class.forName("dev.ryanhcode.sable.companion.math.BoundingBox3dc", false, loader);
                            instance = new SableApi(
                                    containerClass.getMethod("getContainer", ServerLevel.class),
                                    containerClass.getMethod("getAllSubLevels"),
                                    subLevelClass.getMethod("boundingBox"),
                                    subLevelClass.getMethod("isRemoved"),
                                    boundingBoxClass.getMethod("minX"),
                                    boundingBoxClass.getMethod("minY"),
                                    boundingBoxClass.getMethod("minZ"),
                                    boundingBoxClass.getMethod("maxX"),
                                    boundingBoxClass.getMethod("maxY"),
                                    boundingBoxClass.getMethod("maxZ"));
                        } catch (Throwable t) {
                            instance = null;
                        }
                        resolved = true;
                    }
                }
            }
            return instance;
        }
    }

    private static final class ShaolibApi {
        final Method activeProjectiles;
        final Method instanceIsAlive;
        final Method instanceIsDiscarded;
        final Method instanceDimensionId;
        final Method instanceType;
        final Method typeId;
        final Method instancePosition;
        final Method instancePreviousPosition;
        final Method instanceIsEmbedded;
        final Method instanceEmbeddedWorldPosition;
        final Object bodyQueries;

        private ShaolibApi(Method activeProjectiles, Method instanceIsAlive, Method instanceIsDiscarded,
                           Method instanceDimensionId, Method instanceType, Method typeId,
                           Method instancePosition, Method instancePreviousPosition, Method instanceIsEmbedded,
                           Method instanceEmbeddedWorldPosition, Object bodyQueries) {
            this.activeProjectiles = activeProjectiles;
            this.instanceIsAlive = instanceIsAlive;
            this.instanceIsDiscarded = instanceIsDiscarded;
            this.instanceDimensionId = instanceDimensionId;
            this.instanceType = instanceType;
            this.typeId = typeId;
            this.instancePosition = instancePosition;
            this.instancePreviousPosition = instancePreviousPosition;
            this.instanceIsEmbedded = instanceIsEmbedded;
            this.instanceEmbeddedWorldPosition = instanceEmbeddedWorldPosition;
            this.bodyQueries = bodyQueries;
        }

        private static volatile ShaolibApi instance;
        private static volatile boolean resolved;

        static ShaolibApi resolve() {
            if (!resolved) {
                synchronized (ShaolibApi.class) {
                    if (!resolved) {
                        try {
                            Class<?> projectileInstanceClass = forName("com.verr1.shaolib.api.projectile.ProjectileInstance");
                            Class<?> projectileTypeClass = forName("com.verr1.shaolib.api.projectile.ProjectileType");
                            Class<?> bodyQueriesClass = forName("com.verr1.shaolib.api.projectile.body.ProjectileBodyQueries");
                            Class<?> shaolibProjectilesClass = forName("com.verr1.shaolib.api.projectile.ShaolibProjectiles");
                            Object sableBodyQueries = null;
                            try {
                                Class<?> sableBodyQueriesClass = forName("com.verr1.shaolib.compat.sable.SableBodyQueries");
                                sableBodyQueries = sableBodyQueriesClass.getField("INSTANCE").get(null);
                            } catch (Throwable ignored) {
                            }
                            instance = new ShaolibApi(
                                    shaolibProjectilesClass.getMethod("activeProjectiles"),
                                    projectileInstanceClass.getMethod("isAlive"),
                                    projectileInstanceClass.getMethod("isDiscarded"),
                                    projectileInstanceClass.getMethod("dimensionId"),
                                    projectileInstanceClass.getMethod("type"),
                                    projectileTypeClass.getMethod("id"),
                                    projectileInstanceClass.getMethod("position"),
                                    projectileInstanceClass.getMethod("previousPosition"),
                                    projectileInstanceClass.getMethod("isEmbedded"),
                                    projectileInstanceClass.getMethod("embeddedWorldPosition", Level.class, bodyQueriesClass),
                                    sableBodyQueries);
                        } catch (Throwable t) {
                            instance = null;
                            if (shaolibDebugCounter++ < 5) {
                                LOGGER.warn("[CBCAddon] UniversalProximityFuze: ShaolibApi.resolve() failed - Shaolib missing or incompatible", t);
                            }
                        }
                        resolved = true;
                    }
                }
            }
            return instance;
        }

        private static Class<?> forName(String name) throws ClassNotFoundException {
            ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
            ClassLoader ownLoader = ShaolibApi.class.getClassLoader();
            ClassLoader systemLoader = ClassLoader.getSystemClassLoader();
            for (ClassLoader loader : new ClassLoader[]{contextLoader, ownLoader, systemLoader}) {
                if (loader == null) continue;
                try {
                    return Class.forName(name, false, loader);
                } catch (Throwable ignored) {
                }
            }
            return Class.forName(name);
        }
    }
}
