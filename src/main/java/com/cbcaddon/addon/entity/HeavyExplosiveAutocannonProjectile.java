package com.cbcaddon.addon.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;

public class HeavyExplosiveAutocannonProjectile extends FlakAutocannonProjectile {
    private static final double MAX_SPEED = 5.0;
    private static final double MAX_SPEED_SQ = MAX_SPEED * MAX_SPEED;
    private static final double ACCELERATION = 1.04;

    public HeavyExplosiveAutocannonProjectile(EntityType<? extends HeavyExplosiveAutocannonProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        Vec3 velocity = this.getDeltaMovement();
        double speedSq = velocity.lengthSqr();
        if (speedSq > 0.0001 && speedSq < MAX_SPEED_SQ) {
            Vec3 accelerated = velocity.scale(ACCELERATION);
            if (accelerated.lengthSqr() > MAX_SPEED_SQ) {
                accelerated = velocity.normalize().scale(MAX_SPEED);
            }
            this.setDeltaMovement(accelerated);
        }
        super.tick();
    }
}