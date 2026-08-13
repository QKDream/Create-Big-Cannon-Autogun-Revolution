package com.cbcaddon.addon.entity;

import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.config.CBCConfigs;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile.ImpactResult;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile.ImpactResult.KinematicOutcome;
import rbasamoyai.createbigcannons.munitions.ProjectileContext;
import rbasamoyai.createbigcannons.munitions.ShellExplosion;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectileProperties;
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;

public class HeavyExplosiveAutocannonProjectile extends FlakAutocannonProjectile {
    private static final double MAX_SPEED = 5.0;
    private static final double MAX_SPEED_SQ = MAX_SPEED * MAX_SPEED;
    private static final double ACCELERATION = 1.04;
    private static final double PENETRATION_PER_BLOCK = 0.25;
    private static final double PENETRATION_BONUS_CAP = 10.0;
    private static final double TOUGHNESS_PER_BLOCK = 0.125;
    private static final double TOUGHNESS_BONUS_CAP = 5.0;
    private boolean hasDetonated;

    public HeavyExplosiveAutocannonProjectile(EntityType<? extends HeavyExplosiveAutocannonProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        Vec3 before = this.position();
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
            Vec3 after = this.position();
            Vec3 step = after.subtract(before);
            double distance = step.length();
            if (distance > 0.05) {
                int segments = Math.min(8, Math.max(4, (int) Math.ceil(distance * 1.5)));
                ServerLevel level = (ServerLevel) this.level();
                for (int i = 1; i <= segments; ++i) {
                    double t = (double) i / (double) segments;
                    Vec3 point = before.add(step.x * t, step.y * t, step.z * t);
                    level.sendParticles(ParticleTypes.FLAME, point.x, point.y, point.z, 1, 0.02, 0.02, 0.02, 0.0);
                    if (i % 2 == 0) {
                        level.sendParticles(ParticleTypes.SMOKE, point.x, point.y, point.z, 1, 0.04, 0.04, 0.04, 0.01);
                    }
                }
            }
        }
    }

    @Override
    protected void expireProjectile() {
        if (this.hasDetonated) return;
        super.expireProjectile();
    }

    @Override
    protected BallisticPropertiesComponent getBallisticProperties() {
        BallisticPropertiesComponent base = super.getBallisticProperties();
        double distance = this.getTotalDisplacement();
        double penetrationBonus = Math.min(distance * PENETRATION_PER_BLOCK, PENETRATION_BONUS_CAP);
        double toughnessBonus = Math.min(distance * TOUGHNESS_PER_BLOCK, TOUGHNESS_BONUS_CAP);
        return new BallisticPropertiesComponent(
            base.gravity(), base.drag(), base.isQuadraticDrag(),
            base.durabilityMass(),
            base.penetration() + (float) penetrationBonus,
            base.toughness() + (float) toughnessBonus,
            base.deflection());
    }

    @Override
    protected boolean onImpact(HitResult hitResult, ImpactResult impactResult, ProjectileContext projectileContext) {
        boolean handled = super.onImpact(hitResult, impactResult, projectileContext);
        if (handled || this.hasDetonated) return handled;
        boolean detonateHere = hitResult instanceof EntityHitResult
            || impactResult.shouldRemove()
            || impactResult.kinematics() == KinematicOutcome.STOP;
        if (detonateHere) {
            this.detonate(hitResult.getLocation());
            this.removeNextTick = true;
            return true;
        }
        return false;
    }

    @Override
    protected void detonate(Position position) {
        if (this.hasDetonated) return;
        this.hasDetonated = true;
        FlakAutocannonProjectileProperties props = this.getAllProperties();
        ShellExplosion explosion = new ShellExplosion(
            this.level(), this, this.indirectArtilleryFire(false),
            position.x(), position.y(), position.z(),
            props.explosion().blockDamagePower(),
            props.explosion().entityDamagePower(),
            false,
            CBCConfigs.server().munitions.damageRestriction.get().explosiveInteraction());
        CreateBigCannons.handleCustomExplosion(this.level(), explosion);
        this.removeNextTick = true;
    }
}