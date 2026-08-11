package com.cbcaddon.addon.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;

public class SAPAutocannonProjectile extends FlakAutocannonProjectile {
    private boolean highVelocity;
    private boolean soulFire;

    public SAPAutocannonProjectile(EntityType<? extends SAPAutocannonProjectile> type, Level level) { super(type, level); }
    public void setHighVelocity(boolean hv) { this.highVelocity = hv; }
    public void setSoulFire(boolean sf) { this.soulFire = sf; }

    @Override
    public void tick() {
        if (this.highVelocity && this.tickCount == 0 && !this.level().isClientSide) {
            this.setDeltaMovement(this.getDeltaMovement().scale(2.0));
            this.highVelocity = false;
        }
        super.tick();
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        if (this.highVelocity) {
            Vec3 origVel = this.getDeltaMovement();
            this.setDeltaMovement(origVel.scale(2.0));
            super.writeSpawnData(buffer);
            this.setDeltaMovement(origVel);
        } else {
            super.writeSpawnData(buffer);
        }
    }

    @Override
    protected void detonate(Position position) {
        super.detonate(position);
        if (this.soulFire && !this.level().isClientSide) {
            BlockPos center = BlockPos.containing(position);
            FireSpawnHelper.spawnFireField(this.level(), center);
            AABB area = new AABB(center).inflate(1.5);
            for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class, area)) {
                e.hurt(this.damageSources().explosion(this, this.getOwner()), e.getMaxHealth() * 0.25f);
            }
        }
    }
}
