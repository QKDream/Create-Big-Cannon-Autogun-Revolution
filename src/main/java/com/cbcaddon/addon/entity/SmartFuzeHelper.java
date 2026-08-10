package com.cbcaddon.addon.entity;

import com.cbcaddon.addon.block.FuzeControllerBlockEntity;
import com.cbcaddon.addon.item.SmartFuzeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;

public class SmartFuzeHelper {
    private static boolean sableChecked = false;
    private static boolean sableAvailable = false;

    public static boolean isSableAvailable() {
        if (!sableChecked) {
            sableAvailable = ModList.get().isLoaded("sable");
            sableChecked = true;
        }
        return sableAvailable;
    }

    public static boolean checkProximityDetonation(Projectile projectile, float distance) {
        Level level = projectile.level();
        Vec3 pos = projectile.position();
        Vec3 motion = projectile.getDeltaMovement();

        AABB area = new AABB(
            pos.x - distance, pos.y - distance, pos.z - distance,
            pos.x + distance, pos.y + distance, pos.z + distance
        );

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area)) {
            if (entity == projectile.getOwner()) continue;
            if (entity.is(projectile.getOwner())) continue;
            if (!entity.isAlive()) continue;

            double dist = entity.position().distanceTo(pos);
            if (dist <= distance) {
                Vec3 toEntity = entity.position().subtract(pos).normalize();
                double dot = motion.normalize().dot(toEntity);
                if (dot > 0.3 || dist < 1.5) {
                    return true;
                }
            }
        }

        if (isSableAvailable()) {
            try { return sableRaycastCheck(projectile, distance); }
            catch (Exception ignored) {}
        }

        return false;
    }

    private static boolean sableRaycastCheck(Projectile projectile, float distance) throws Exception {
        Class.forName("dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.create.raycasts.SableRaycastHelper");
        return false;
    }

    /**
     * Resolve the effective fuze mode by checking the bound controller's live settings.
     */
    public static SmartFuzeItem.Mode resolveMode(ItemStack fuzeStack, Level level) {
        if (!(fuzeStack.getItem() instanceof SmartFuzeItem)) {
            return SmartFuzeItem.Mode.CONTACT;
        }

        // Check bound controller for live settings
        BlockPos controllerPos = SmartFuzeItem.getControllerPos(fuzeStack);
        if (controllerPos != null && level != null && level.isLoaded(controllerPos)) {
            BlockEntity be = level.getBlockEntity(controllerPos);
            if (be instanceof FuzeControllerBlockEntity controller) {
                return SmartFuzeItem.Mode.fromId(controller.getFuzeMode());
            }
        }

        return SmartFuzeItem.getMode(fuzeStack);
    }

    public static float resolveProximityDistance(ItemStack fuzeStack, Level level) {
        if (!(fuzeStack.getItem() instanceof SmartFuzeItem)) {
            return 3.0f;
        }

        BlockPos controllerPos = SmartFuzeItem.getControllerPos(fuzeStack);
        if (controllerPos != null && level != null && level.isLoaded(controllerPos)) {
            BlockEntity be = level.getBlockEntity(controllerPos);
            if (be instanceof FuzeControllerBlockEntity controller) {
                return controller.getProximityDistance();
            }
        }

        return SmartFuzeItem.getProximityDistance(fuzeStack);
    }

    public static int resolveTimer(ItemStack fuzeStack, Level level) {
        if (!(fuzeStack.getItem() instanceof SmartFuzeItem)) {
            return 60;
        }

        BlockPos controllerPos = SmartFuzeItem.getControllerPos(fuzeStack);
        if (controllerPos != null && level != null && level.isLoaded(controllerPos)) {
            BlockEntity be = level.getBlockEntity(controllerPos);
            if (be instanceof FuzeControllerBlockEntity controller) {
                return controller.getFuzeTimer();
            }
        }

        return 60;
    }
}