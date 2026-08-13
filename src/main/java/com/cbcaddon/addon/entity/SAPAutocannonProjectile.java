package com.cbcaddon.addon.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.config.CBCConfigs;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile.ImpactResult;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile.ImpactResult.KinematicOutcome;
import rbasamoyai.createbigcannons.munitions.ProjectileContext;
import rbasamoyai.createbigcannons.munitions.ShellExplosion;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectileProperties;

public class SAPAutocannonProjectile extends FlakAutocannonProjectile {
    private boolean highVelocity;
    private boolean soulFire;
    private boolean hasDetonated;

    public SAPAutocannonProjectile(EntityType<? extends SAPAutocannonProjectile> type, Level level) { super(type, level); }
    public void setHighVelocity(boolean hv) { this.highVelocity = hv; }
    public void setSoulFire(boolean sf) { this.soulFire = sf; }

    @Override
    public void tick() {
        if (this.highVelocity && this.tickCount == 0) {
            this.setDeltaMovement(this.getDeltaMovement().scale(2.0));
            this.highVelocity = false;
        }
        super.tick();
    }

    @Override
    protected void expireProjectile() {
        if (this.hasDetonated) return;
        super.expireProjectile();
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
        if (this.soulFire && !this.level().isClientSide) {
            BlockPos center = BlockPos.containing(position);
            FireSpawnHelper.spawnFireField(this.level(), center);
            AABB area = new AABB(center).inflate(1.5);
            for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class, area)) {
                e.hurt(this.damageSources().explosion(this, this.getOwner()), e.getMaxHealth() * 0.25f);
            }
        }
        this.removeNextTick = true;
    }
}