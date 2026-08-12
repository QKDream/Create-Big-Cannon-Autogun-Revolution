package com.cbcaddon.addon.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile.ImpactResult;
import rbasamoyai.createbigcannons.munitions.ProjectileContext;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;

public class HeavyExplosiveAutocannonProjectile extends FlakAutocannonProjectile {
    private static final double MAX_SPEED = 5.0;
    private static final double MAX_SPEED_SQ = MAX_SPEED * MAX_SPEED;
    private static final double ACCELERATION = 1.04;
    private boolean hasDetonated;

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
        if (!this.isRemoved() && !this.level().isClientSide && !this.isInGround()) {
            Vec3 pos = this.position().subtract(this.getDeltaMovement().scale(0.4));
            ((ServerLevel) this.level()).sendParticles(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.01);
            ((ServerLevel) this.level()).sendParticles(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.005);
        }
    }

    @Override
    protected boolean onImpact(HitResult hitResult, ImpactResult impactResult, ProjectileContext projectileContext) {
        super.onImpact(hitResult, impactResult, projectileContext);
        if (!this.level().isClientSide && !this.hasDetonated) {
            this.hasDetonated = true;
            this.detonate(hitResult.getLocation());
            this.removeNextTick = true;
        }
        return true;
    }
}