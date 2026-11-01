package com.cbcaddon.addon.item;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Locale;
import java.util.WeakHashMap;
import java.util.function.BooleanSupplier;

import com.cbcaddon.addon.client.ClientTooltipUtils;
import net.minecraft.core.Position;
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
    /** 记录本 tick 已经由 onProjectileClip 扫过的弹体，避免 tick 兜底重复判定/重复起爆。 */
    private static final Map<AbstractCannonProjectile, Integer> CLIP_SWEPT_TICK =
            Collections.synchronizedMap(new WeakHashMap<>());
    private static final double MIN_SENSING_RADIUS = 1.0;
    /** CBC 默认覆盖半宽：proximityFuzeScale(5) × proximityFuzeSpacing(1.5)。配置读不到时就用它。 */
    private static final double FALLBACK_SENSING_RADIUS = 7.5;
    /** CBC 默认 proximityFuzeArmingTime。配置读不到时就用它。 */
    private static final int FALLBACK_ARMING_TICKS = 5;

    /** 弹体首次被判定时记录的位置≈炮口：落在这个位置里的 SABLE 子关卡就是"自己的船"，永远不判。 */
    private static final Map<AbstractCannonProjectile, Vec3> LAUNCH_POS =
            Collections.synchronizedMap(new WeakHashMap<>());

    /** SABLE 子关卡盒在这个轴上超过这么多格就认为不可信（多为未刷新的异常盒），不参与判定。 */
    private static final double SABLE_MAX_BOX = 1024.0;
    private static int shaolibDebugCounter = 0;
    private static int sableDebugCounter = 0;

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
        if (airTime > armingTicks()) {
            stack.set(CBCDataComponents.ARMED, true);
        }
        stack.set(CBCDataComponents.AIR_TIME, ++airTime);

        // 兜底扫描：CBC 只在 clipAndDamage() 的穿透循环里回调 onProjectileClip，
        // 任何改写这条链路的 mixin / 子类都会让引信静默失效。这里用本 tick 的位移线段再扫一次，
        // 保证只要弹体还在服务端 tick，引信就始终有机会起爆。
        if (!stack.has(CBCDataComponents.ARMED)) return false;
        if (proj.isInGround()) return false;
        Integer sweptTick = CLIP_SWEPT_TICK.get(proj);
        if (sweptTick != null && sweptTick.intValue() == proj.tickCount) return false;
        Vec3 tickEnd = proj.position();
        Vec3 tickStart = tickEnd.subtract(proj.getDeltaMovement());
        return findTarget(stack, proj, tickStart, tickEnd) != null;
    }

    @Override
    public boolean onProjectileClip(ItemStack stack, AbstractCannonProjectile proj, Vec3 v0, Vec3 v1,
                                    ProjectileContext context, boolean flag) {
        if (flag) return false;
        CLIP_SWEPT_TICK.put(proj, proj.tickCount);
        if (!stack.has(CBCDataComponents.ARMED)) return false;
        Vec3 detonationPosition = findTarget(stack, proj, v0, v1);
        if (detonationPosition == null) return false;
        context.setDetonationPositionForClip(detonationPosition);
        return true;
    }

    /**
     * 本引信实际使用的感应半径。
     * <p>
     * CBC 自带的近炸引信并不把 {@code DETONATION_DISTANCE} 当作感应半径：它固定用
     * {@code proximityFuzeScale * proximityFuzeSpacing} 铺一张 (2*scale+1)^2 的射线扇面
     * （默认 5 * 1.5 = 7.5 格半宽），{@code DETONATION_DISTANCE} 只额外增加前向"牵引距离"。
     * 本引信只有球形判定，所以把同样的扇面半宽当作感应半径下限；否则默认值 1 会让
    /** 起爆前的装订时间；CBC 配置读不到时退回默认值，绝不让异常打断整条 tick。 */
    private static int armingTicks() {
        try {
            return CBCConfigs.server().munitions.proximityFuzeArmingTime.get();
        } catch (Throwable ignored) {
            return FALLBACK_ARMING_TICKS;
        }
    }
    /**
     * 本引信实际使用的感应半径。
     * <p>
     * CBC 自带的近炸引信并不把 {@code DETONATION_DISTANCE} 当作感应半径：它固定用
     * {@code proximityFuzeScale * proximityFuzeSpacing} 铺一张 (2*scale+1)^2 的射线扇面
     * （默认 5 * 1.5 = 7.5 格半宽），{@code DETONATION_DISTANCE} 只额外增加前向"牵引距离"。
     * 本引信只有球形判定，所以把同样的扇面半宽当作感应半径下限；否则默认值 1 会让
     * 引信退化成"只有直接命中才起爆"，也就是完全没有空爆。
     */
    private static double sensingRadius(ItemStack stack) {
        // 读不到 CBC 配置时也要保持与 CBC 默认一致的 7.5 格，绝不能退化成 1 格（1 格＝只有碰炸）
        double coverage = FALLBACK_SENSING_RADIUS;
        try {
            int scale = CBCConfigs.server().munitions.proximityFuzeScale.get();
            double spacing = CBCConfigs.server().munitions.proximityFuzeSpacing.get();
            coverage = Math.max(MIN_SENSING_RADIUS, scale * spacing);
        } catch (Throwable ignored) {
        }
        int userDistance = stack.getOrDefault(CBCDataComponents.DETONATION_DISTANCE, 1);
        return Math.max(coverage, userDistance);
    }

    private static Vec3 findTarget(ItemStack stack, AbstractCannonProjectile proj, Vec3 start, Vec3 end) {
        double radius = sensingRadius(stack);
        // 顺序很重要：先用纯原版类判导弹，SABLE / Shaolib 这两条反射分支放在后面。
        // 这样任何一条反射链路出问题（返回假命中或抛异常）都不会再屏蔽导弹判定。
        Vec3 detonationPosition = findMissileTarget(proj.level(), proj, start, end, radius);
        if (detonationPosition != null) {
            return detonationPosition;
        }
        detonationPosition = findSableTarget(proj.level(), proj, start, end, radius);
        if (detonationPosition != null) {
            return detonationPosition;
        }
        detonationPosition = findShaolibTarget(proj.level(), proj, start, end, radius);
        if (detonationPosition != null) {
            return detonationPosition;
        }
        return null;
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
            tooltip.add(Component.translatable("tooltip.cbcaddon.universal_proximity_fuze.radius",
                    String.format(Locale.ROOT, "%.1f", sensingRadius(stack))));
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
        tooltip.add(Component.translatable("tooltip.cbcaddon.universal_proximity_fuze.radius",
                String.format(Locale.ROOT, "%.1f", sensingRadius(stack))));
        tooltip.add(Component.translatable("tooltip.cbcaddon.universal_proximity_fuze.targets"));
    }

    /**
     * SABLE 结构近炸。
     * <p>
     * 优先走 {@code Sable.HELPER}：这是 CBC 自己的 SABLE 兼容层用的静态入口，
     * {@code getAllIntersecting(Level, BoundingBox3dc)} 直接做扫掠查询，不需要
     * {@code ServerSubLevelContainer}，于是"容器为空 / 无子关卡"这两类早退整体消失。
     * 拿不到 HELPER 时才退回容器路径。
     * <p>
     * 三条"绝不判"规则（都是踩过的坑）：
     * <ul>
     *   <li>炮口在这条船的盒子里 → 自己的船（甲板机炮距离恒 0）。</li>
     *   <li>本 tick 起点已经在盒内 → 炮口在船体里/炮弹已入船，不算"接近"。</li>
     *   <li>盒子退化或尺寸不像一条船 → 不可信，跳过。</li>
     * </ul>
     */
    private static Vec3 findSableTarget(Level level, AbstractCannonProjectile proj, Vec3 segStart, Vec3 segEnd, double radius) {
        if (!(level instanceof ServerLevel serverLevel)) return null;
        try {
            SableApi api = SableApi.resolve();
            if (api == null) {
                if (sableDebugCounter++ < 5) {
                    LOGGER.warn("[CBCAddon] UniversalProximityFuze: SABLE API unavailable, skipping structure detection");
                }
                return null;
            }
            Vec3 launchPos = LAUNCH_POS.computeIfAbsent(proj, k -> proj.position());
            Iterable<?> ships;
            Object ownShip = null;
            if (api.helperMode) {
                Object queryBox = api.boxFromAabb.newInstance(new AABB(segStart, segEnd).inflate(radius));
                ships = (Iterable<?>) api.getAllIntersecting.invoke(api.helper, level, queryBox);
                ownShip = api.getContaining.invoke(api.helper, level, launchPos);
            } else {
                Object container = api.getContainer.invoke(null, serverLevel);
                if (container == null) {
                    if (sableDebugCounter++ < 5) {
                        LOGGER.warn("[CBCAddon] UniversalProximityFuze: SABLE container null for {}", level.dimension().location());
                    }
                    return null;
                }
                List<?> list = (List<?>) api.getAllSubLevels.invoke(container);
                if (list == null || list.isEmpty()) {
                    return null;
                }
                ships = list;
            }
            if (ships == null) return null;

            double radiusSqr = radius * radius;
            double bestDistSqr = Double.MAX_VALUE;
            Vec3 bestPoint = null;
            for (Object subLevel : ships) {
                if (subLevel == null) continue;
                if (ownShip != null && subLevel == ownShip) {
                    continue;
                }
                if ((Boolean) api.isRemoved.invoke(subLevel)) continue;
                Object boundingBox = api.subLevelBoundingBox.invoke(subLevel);
                if (boundingBox == null) continue;
                AABB box = new AABB(
                        (Double) api.minX.invoke(boundingBox),
                        (Double) api.minY.invoke(boundingBox),
                        (Double) api.minZ.invoke(boundingBox),
                        (Double) api.maxX.invoke(boundingBox),
                        (Double) api.maxY.invoke(boundingBox),
                        (Double) api.maxZ.invoke(boundingBox));
                if (box.getXsize() <= 0.05 || box.getYsize() <= 0.05 || box.getZsize() <= 0.05
                        || box.getXsize() > SABLE_MAX_BOX || box.getZsize() > SABLE_MAX_BOX) {
                    continue;
                }
                if (box.contains(launchPos)) {
                    continue;
                }
                if (box.contains(segStart)) {
                    continue;
                }
                Vec3 shellPoint = closestSegmentPointToBox(segStart, segEnd, box);
                Vec3 hullPoint = closestPointOnAABB(box, shellPoint);
                double distSqr = shellPoint.distanceToSqr(hullPoint);
                if (distSqr <= radiusSqr && distSqr < bestDistSqr) {
                    bestDistSqr = distSqr;
                    bestPoint = hullPoint;
                }
            }
            return bestPoint;
        } catch (Throwable t) {
            if (sableDebugCounter++ < 5) {
                LOGGER.warn("[CBCAddon] UniversalProximityFuze: SABLE detection failed", t);
            }
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
                    // 起爆点取目标身上最近的点（而不是弹体航迹上的点）：
                    // CBC 自带的近炸引信同样是"在目标处起爆"，否则空爆会落在离目标 radius 格的半空中，
                    // 战斗部威力白扔。
                    bestPosition = closestOnMissile;
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
                        // 同 findMissileTarget：起爆点取 Shaolib 弹本身的最近点，让空爆落在目标上。
                        bestPosition = closestOnMissile;
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
    /** 弹道线段上离 box 最近的那个点（用来算距离，也用来反推命中点）。 */
    private static Vec3 closestSegmentPointToBox(Vec3 segStart, Vec3 segEnd, AABB box) {
        Vec3 p = closestPointOnSegment(segStart, segEnd, box.getCenter());
        for (int i = 0; i < 3; i++) {
            Vec3 q = closestPointOnAABB(box, p);
            p = closestPointOnSegment(segStart, segEnd, q);
        }
        return p;
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
        // 首选路径：Sable.HELPER（CBC 自己的 SABLE 兼容层入口）
        boolean helperMode;
        Object helper;
        Method getAllIntersecting;
        Method getContaining;
        Constructor<?> boxFromAabb;
        // 兜底路径：ServerSubLevelContainer
        Method getContainer;
        Method getAllSubLevels;
        // 两条路都要用
        Method subLevelBoundingBox;
        Method isRemoved;
        Method minX;
        Method minY;
        Method minZ;
        Method maxX;
        Method maxY;
        Method maxZ;

        private static volatile SableApi instance;
        private static volatile boolean resolved;

        String modeName() {
            return this.helperMode ? "helper" : "container";
        }

        static SableApi resolve() {
            if (!resolved) {
                synchronized (SableApi.class) {
                    if (!resolved) {
                        instance = build();
                        resolved = true;
                    }
                }
            }
            return instance;
        }

        private static SableApi build() {
            SableApi api = new SableApi();
            Class<?> bboxClass;
            Class<?> subLevelClass;
            try {
                bboxClass = forName("dev.ryanhcode.sable.companion.math.BoundingBox3dc");
                subLevelClass = forName("dev.ryanhcode.sable.sublevel.SubLevel");
                api.subLevelBoundingBox = subLevelClass.getMethod("boundingBox");
                api.isRemoved = subLevelClass.getMethod("isRemoved");
                api.minX = bboxClass.getMethod("minX");
                api.minY = bboxClass.getMethod("minY");
                api.minZ = bboxClass.getMethod("minZ");
                api.maxX = bboxClass.getMethod("maxX");
                api.maxY = bboxClass.getMethod("maxY");
                api.maxZ = bboxClass.getMethod("maxZ");
            } catch (Throwable t) {
                if (sableDebugCounter++ < 5) {
                    LOGGER.warn("[CBCAddon] UniversalProximityFuze: SABLEApi.resolve() failed - SABLE/companion missing or incompatible", t);
                }
                return null;
            }
            // 首选：Sable.HELPER
            try {
                Class<?> sableClass = forName("dev.ryanhcode.sable.Sable");
                Object helper = sableClass.getField("HELPER").get(null);
                if (helper != null) {
                    Class<?> helperClass = helper.getClass();
                    api.getAllIntersecting = helperClass.getMethod("getAllIntersecting", Level.class, bboxClass);
                    api.getContaining = helperClass.getMethod("getContaining", Level.class, Position.class);
                    api.boxFromAabb = forName("dev.ryanhcode.sable.companion.math.BoundingBox3d").getConstructor(AABB.class);
                    api.helper = helper;
                    api.helperMode = true;
                    if (sableDebugCounter++ < 3) {
                        LOGGER.info("[CBCAddon] UniversalProximityFuze: SABLE via Sable.HELPER ({})", helperClass.getName());
                    }
                    return api;
                }
            } catch (Throwable helperFailure) {
                if (sableDebugCounter++ < 5) {
                    LOGGER.warn("[CBCAddon] UniversalProximityFuze: Sable.HELPER path unavailable, trying container", helperFailure);
                }
            }
            // 兜底：容器
            try {
                Class<?> containerClass = forName("dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer");
                api.getContainer = containerClass.getMethod("getContainer", ServerLevel.class);
                api.getAllSubLevels = containerClass.getMethod("getAllSubLevels");
                api.helperMode = false;
                if (sableDebugCounter++ < 3) {
                    LOGGER.info("[CBCAddon] UniversalProximityFuze: SABLE via container");
                }
                return api;
            } catch (Throwable containerFailure) {
                if (sableDebugCounter++ < 5) {
                    LOGGER.warn("[CBCAddon] UniversalProximityFuze: SABLE container path unavailable", containerFailure);
                }
            }
            return null;
        }

        /**
         * 类探测用的加载器候选，按可能性排序：
         * <ol>
         *   <li>线程上下文（多数情况够用）</li>
         *   <li>本模组自己的加载器（ModLauncher 的 TransformingClassLoader）</li>
         *   <li><b>SABLE 自己的加载器</b>——先拿到只依赖 sable 本体的类，再用它的加载器去摸 jarjar 里的 companion 库</li>
         *   <li>CBC 的加载器（CBC 的 SABLE 兼容层能看见 companion）</li>
         *   <li>系统加载器</li>
         * </ol>
         * 注意：{@code SubLevel} 实现了 {@code companion.SubLevelAccess}，companion 库不可见时
         * 连 {@code SubLevel} 都加载不了 —— 这不是可选项。
         */
        private static Class<?> forName(String name) throws ClassNotFoundException {
            for (ClassLoader loader : loaderChain()) {
                if (loader == null) continue;
                try {
                    return Class.forName(name, false, loader);
                } catch (Throwable ignored) {
                }
            }
            return Class.forName(name);
        }

        private static ClassLoader[] loaderChain() {
            ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
            ClassLoader ownLoader = SableApi.class.getClassLoader();
            ClassLoader sableLoader = null;
            ClassLoader cbcLoader = null;
            for (ClassLoader loader : new ClassLoader[]{contextLoader, ownLoader, ClassLoader.getSystemClassLoader()}) {
                if (loader == null) continue;
                if (sableLoader == null) {
                    try {
                        sableLoader = Class.forName("dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer", false, loader).getClassLoader();
                    } catch (Throwable ignored) {
                    }
                }
                if (cbcLoader == null) {
                    try {
                        cbcLoader = Class.forName("rbasamoyai.createbigcannons.compat.sable.SableCannonProjectileCompat", false, loader).getClassLoader();
                    } catch (Throwable ignored) {
                    }
                }
            }
            return new ClassLoader[]{contextLoader, ownLoader, sableLoader, cbcLoader, ClassLoader.getSystemClassLoader()};
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
