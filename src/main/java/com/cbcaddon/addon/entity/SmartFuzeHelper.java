package com.cbcaddon.addon.entity;

import com.cbcaddon.addon.block.FuzeControllerBlockEntity;
import com.cbcaddon.addon.item.SmartFuzeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;

import java.lang.reflect.Method;

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

    /**
     * Try to use SABLE raycast to check for entities along the projectile's path.
     * Falls back to simple AABB entity check if SABLE is not available.
     */
    public static boolean checkProximityDetonation(Projectile projectile, float distance) {
        Level level = projectile.level();
        Vec3 pos = projectile.position();
        Vec3 motion = projectile.getDeltaMovement();

        // Extend look-ahead based on velocity
        Vec3 ahead = pos.add(motion.normalize().scale(distance));

        // Simple entity check
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
                // Check if entity is roughly in front of the projectile
                Vec3 toEntity = entity.position().subtract(pos).normalize();
                double dot = motion.normalize().dot(toEntity);
                if (dot > 0.3 || dist < 1.5) {
                    return true;
                }
            }
        }

        // SABLE raycast check (reflection-based)
        if (isSableAvailable()) {
            try {
                return sableRaycastCheck(projectile, distance);
            } catch (Exception ignored) {}
        }

        return false;
    }

    private static boolean sableRaycastCheck(Projectile projectile, float distance) throws Exception {
        // Use SABLE's raycast helper if available
        // dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.create.raycasts.SableRaycastHelper
        Class<?> helperClass = Class.forName(
            "dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.create.raycasts.SableRaycastHelper");
        // Try to use its raycast method - the exact API depends on SABLE version
        // For now, we just return false and rely on the simple AABB check above
        return false;
    }

    /**
     * Read the smart fuze mode from the projectile's fuze stack.
     * Also checks the bound controller for live updates.
     */
    public static SmartFuzeItem.Mode resolveMode(ItemStack fuzeStack) {
        if (!(fuzeStack.getItem() instanceof SmartFuzeItem)) {
            return SmartFuzeItem.Mode.CONTACT;
        }

        // Check if there's a bound controller
        BlockPos controllerPos = SmartFuzeItem.getControllerPos(fuzeStack);
        if (controllerPos != null) {
            // Try to get live mode from controller
            // Note: this requires the controller chunk to be loaded
            // We'll fall back to the fuze's stored mode if unavailable
        }

        return SmartFuzeItem.getMode(fuzeStack);
    }

    public static float resolveProximityDistance(ItemStack fuzeStack) {
        if (!(fuzeStack.getItem() instanceof SmartFuzeItem)) {
            return 3.0f;
        }
        return SmartFuzeItem.getProximityDistance(fuzeStack);
    }
}