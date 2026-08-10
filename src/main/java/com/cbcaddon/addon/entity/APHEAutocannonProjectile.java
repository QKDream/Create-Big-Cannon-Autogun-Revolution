package com.cbcaddon.addon.entity;

import com.cbcaddon.addon.block.FuzeControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;

public class APHEAutocannonProjectile extends FlakAutocannonProjectile {
    private boolean highVelocity;
    private boolean soulFire;
    private String controllerMode;
    private float controllerDist = 3.0f;
    private int controllerTimer;

    public APHEAutocannonProjectile(EntityType<? extends APHEAutocannonProjectile> type, Level level) {
        super(type, level);
    }

    public void setHighVelocity(boolean hv) { this.highVelocity = hv; }
    public void setSoulFire(boolean sf) { this.soulFire = sf; }

    @Override
    public void tick() {
        if (this.highVelocity && this.tickCount == 1) {
            this.setDeltaMovement(this.getDeltaMovement().scale(2.0));
            this.highVelocity = false;
        }
        if (this.tickCount == 1 && !this.level().isClientSide) {
            BlockPos c = this.blockPosition();
            for (BlockPos p : BlockPos.betweenClosed(c.offset(-3, -3, -3), c.offset(3, 3, 3))) {
                BlockEntity be = this.level().getBlockEntity(p);
                if (be instanceof FuzeControllerBlockEntity ctrl) {
                    this.controllerMode = ctrl.getFuzeMode();
                    this.controllerDist = ctrl.getProximityDistance();
                    this.controllerTimer = ctrl.getFuzeTimer();
                    break;
                }
            }
        }
        if (this.controllerMode != null && !this.level().isClientSide && this.tickCount > 1) {
            if ("proximity".equals(this.controllerMode)) {
                if (SmartFuzeHelper.checkProximityDetonation(this, this.controllerDist)) {
                    this.detonate(this.position());
                    return;
                }
            } else if ("timed".equals(this.controllerMode)) {
                this.controllerTimer--;
                if (this.controllerTimer <= 0) {
                    this.detonate(this.position());
                    return;
                }
            }
        }
        super.tick();
    }

    @Override
    protected void detonate(Position position) {
        super.detonate(position);
        if (this.soulFire && !this.level().isClientSide) {
            BlockPos center = BlockPos.containing(position);
            FireSpawnHelper.spawnFireField(this.level(), center);
            AABB area = new AABB(center).inflate(1.0);
            for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, area)) {
                entity.hurt(this.damageSources().explosion(this, this.getOwner()), entity.getMaxHealth() * 0.15f);
            }
        }
    }
}