package com.cbcaddon.addon.entity;

import com.cbcaddon.addon.item.SmartFuzeItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;

public class SmartFuzeHelper {
    private static boolean sableAvailable = false;
    private static boolean sableChecked = false;

    public static boolean isSableAvailable() {
        if (!sableChecked) { sableAvailable = ModList.get().isLoaded("sable"); sableChecked = true; }
        return sableAvailable;
    }

    public static boolean checkProximityDetonation(Projectile projectile, float distance) {
        Vec3 pos = projectile.position();
        Vec3 motion = projectile.getDeltaMovement();
        AABB area = new AABB(pos.x - distance, pos.y - distance, pos.z - distance,
                             pos.x + distance, pos.y + distance, pos.z + distance);
        for (LivingEntity e : projectile.level().getEntitiesOfClass(LivingEntity.class, area)) {
            if (e == projectile.getOwner()) continue;
            if (e.is(projectile.getOwner())) continue;
            if (!e.isAlive()) continue;
            double dist = e.position().distanceTo(pos);
            if (dist <= distance) {
                Vec3 toE = e.position().subtract(pos).normalize();
                double dot = motion.normalize().dot(toE);
                if (dot > 0.3 || dist < 1.5) return true;
            }
        }
        return false;
    }
}