package com.cbcaddon.addon.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.munitions.autocannon.ap_round.APAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;

public class APFSDSAutocannonProjectile extends APAutocannonProjectile {
    private boolean highVelocity = false;

    private static final BallisticPropertiesComponent BALLISTIC = new BallisticPropertiesComponent(
        -0.005, 0.001, false, 10.0f, 250.0f, 120.0f, 0.03f
    );

    private static final EntityDamagePropertiesComponent DAMAGE = new EntityDamagePropertiesComponent(
        40.0f, false, true, false, 2.0f
    );

    public APFSDSAutocannonProjectile(EntityType<? extends APFSDSAutocannonProjectile> type, Level level) {
        super(type, level);
    }

    public void setHighVelocity(boolean hv) {
        this.highVelocity = hv;
    }

    @Override
    protected BallisticPropertiesComponent getBallisticProperties() {
        return BALLISTIC;
    }

    @Override
    public EntityDamagePropertiesComponent getDamageProperties() {
        return DAMAGE;
    }

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
}