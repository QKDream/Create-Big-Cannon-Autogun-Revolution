package com.cbcaddon.addon.entity;

import com.cbcaddon.addon.item.SmartFuzeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;

public class SAPAutocannonProjectile extends FlakAutocannonProjectile {
    private boolean highVelocity = false;
    private boolean soulFire = false;
    private SmartFuzeItem.Mode smartFuzeMode = null;
    private float smartFuzeDist = 3.0f;
    private int smartFuzeTimer = 0;

    public SAPAutocannonProjectile(EntityType<? extends SAPAutocannonProjectile> type, Level level) { super(type, level); }

    public void setHighVelocity(boolean hv) { this.highVelocity = hv; }
    public void setSoulFire(boolean sf) { this.soulFire = sf; }
    public void setSmartFuzeMode(SmartFuzeItem.Mode mode) { this.smartFuzeMode = mode; }
    public void setSmartFuzeDist(float dist) { this.smartFuzeDist = dist; }
    public void setSmartFuzeTimer(int timer) { this.smartFuzeTimer = timer; }

    @Override
    public void tick() {
        if (this.highVelocity && this.tickCount == 1) {
            this.setDeltaMovement(this.getDeltaMovement().scale(2.0));
            this.highVelocity = false;
        }
        if (smartFuzeMode != null && !this.level().isClientSide) {
            if (smartFuzeMode == SmartFuzeItem.Mode.PROXIMITY) {
                if (SmartFuzeHelper.checkProximityDetonation(this, smartFuzeDist)) { this.detonate(this.position()); return; }
            } else if (smartFuzeMode == SmartFuzeItem.Mode.TIMED) {
                smartFuzeTimer--;
                if (smartFuzeTimer <= 0) { this.detonate(this.position()); return; }
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