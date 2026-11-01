package com.cbcaddon.addon.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;

public class ShrapnelAutocannonProjectile extends FlakAutocannonProjectile {
    private boolean highVelocity;
    private boolean soulFire;
    /** 一次性闸：deflection 0.30 意味着掠射命中会弹跳，弹一下就会再爆一次满破片。
     *  一颗弹只允许爆一次，否则同一发弹药能连开数次破片云，客户端直接被方块粒子刷屏。 */
    private boolean hasDetonated;

    public ShrapnelAutocannonProjectile(EntityType<? extends ShrapnelAutocannonProjectile> type, Level level) { super(type, level); }
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
        if (this.hasDetonated) return;
        this.hasDetonated = true;
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