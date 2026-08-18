package com.cbcaddon.addon.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.config.CBCConfigs;
import rbasamoyai.createbigcannons.munitions.fragment_burst.CBCProjectileBurst;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareEntityTypes;
import riftyboi.cbcmodernwarfare.munitions.big_cannon.heap_shell.HEAPExplosion;

public class MultiPurposeAutocannonProjectile extends FlakAutocannonProjectile {
    private boolean highVelocity;
    private boolean soulFire;

    public MultiPurposeAutocannonProjectile(EntityType<? extends MultiPurposeAutocannonProjectile> type, Level level) { super(type, level); }
    public void setHighVelocity(boolean hv) { this.highVelocity = hv; }
    public void setSoulFire(boolean sf) { this.soulFire = sf; }

    @Override
    public void tick() {
        if (this.highVelocity && this.tickCount == 0) {
            this.setDeltaMovement(this.getDeltaMovement().scale(2.0));
            this.highVelocity = false;
        }
        super.tick();
        if (!this.isInGround() && this.getDeltaMovement().lengthSqr() > 0.001) {
            this.setOrientation(this.getDeltaMovement());
        }
    }

    @Override
    protected void detonate(Position position) {
        if (!this.level().isClientSide) {
            Vec3 jetVelocity = this.getDeltaMovement().normalize().scale(2.0);
            HEAPExplosion explosion = new HEAPExplosion(
                    this.level(),
                    this,
                    this.indirectArtilleryFire(false),
                    position.x(), position.y(), position.z(),
                    2.5f,
                    12.0f,
                    true,
                    CBCConfigs.server().munitions.damageRestriction.get().explosiveInteraction());
            CreateBigCannons.handleCustomExplosion(this.level(), explosion);
            CBCProjectileBurst.spawnConeBurst(
                    this.level(),
                    CBCModernWarfareEntityTypes.HEAP_BURST.get(),
                    new Vec3(position.x(), position.y(), position.z()),
                    jetVelocity,
                    10,
                    0.35);
            if (this.soulFire) {
                BlockPos center = BlockPos.containing(position);
                FireSpawnHelper.spawnFireField(this.level(), center);
                AABB area = new AABB(center).inflate(1.5);
                for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class, area)) {
                    e.hurt(this.damageSources().explosion(this, this.getOwner()), e.getMaxHealth() * 0.25f);
                }
            }
        }
        this.discard();
    }
}