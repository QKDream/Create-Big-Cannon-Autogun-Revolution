package com.cbcaddon.addon.item;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
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
import rbasamoyai.createbigcannons.config.CBCConfigs;
import rbasamoyai.createbigcannons.index.CBCDataComponents;
import rbasamoyai.createbigcannons.index.CBCMenuTypes;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile;
import rbasamoyai.createbigcannons.munitions.ProjectileContext;
import rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem;
import rbasamoyai.createbigcannons.munitions.fuzes.ProximityFuzeContainer;

public class UniversalProximityFuzeItem extends FuzeItem implements MenuProvider {

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
            Player player = Minecraft.getInstance().player;
            sneaking = player != null && player.isShiftKeyDown();
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

    private static boolean isMissileEntity(Entity entity) {
        String typeName = entity.getType().toShortString();
        if (typeName.startsWith("vestalihy:")) {
            String key = typeName.substring("vestalihy:".length());
            return "ptur".equals(key) || "tow".equals(key) || "ptur_jet".equals(key) || "malytka".equals(key);
        }
        String lower = typeName.toLowerCase(Locale.ROOT);
        boolean mianbao = lower.startsWith("mianbaos_modernwarfare:") || lower.contains("tanshe");
        if (mianbao) {
            String key = lower.substring(lower.lastIndexOf(':') + 1);
            return key.contains("missile") || key.contains("rocket") || key.startsWith("agm_") || key.startsWith("jdam");
        }
        return lower.contains("missile") || lower.contains("rocket");
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
}
