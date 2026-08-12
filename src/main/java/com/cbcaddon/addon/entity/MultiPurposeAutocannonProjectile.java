package com.cbcaddon.addon.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;

public class MultiPurposeAutocannonProjectile extends FlakAutocannonProjectile {
    private boolean highVelocity;
    private boolean soulFire;

    private static final BallisticPropertiesComponent BALLISTIC = new BallisticPropertiesComponent(
        -0.025, 0.01, false, 2.0f, 8.0f, 5.0f, 0.30f
    );
    private static final EntityDamagePropertiesComponent DAMAGE = new EntityDamagePropertiesComponent(
        12.0f, false, true, false, 0.4f
    );

    public MultiPurposeAutocannonProjectile(EntityType<? extends MultiPurposeAutocannonProjectile> type, Level level) { super(type, level); }
    public void setHighVelocity(boolean hv) { this.highVelocity = hv; }
    public void setSoulFire(boolean sf) { this.soulFire = sf; }

    @Override protected BallisticPropertiesComponent getBallisticProperties() { return BALLISTIC; }
    @Override public EntityDamagePropertiesComponent getDamageProperties() { return DAMAGE; }
    @Override public float getProjectileMass() { return 2.0f; }

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