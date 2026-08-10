package com.cbcaddon.addon.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SmartFuzeHelper {
    public static boolean checkProximityDetonation(Projectile projectile, float distance) {
        Vec3 pos = projectile.position();
        AABB area = new AABB(pos.x - distance, pos.y - distance, pos.z - distance,
                             pos.x + distance, pos.y + distance, pos.z + distance);
        for (LivingEntity e : projectile.level().getEntitiesOfClass(LivingEntity.class, area)) {
            if (e == projectile.getOwner()) continue;
            if (!e.isAlive()) continue;
            double dist = e.position().distanceTo(pos);
            if (dist <= distance) return true;
        }
        return false;
    }
}