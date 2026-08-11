package com.cbcaddon.addon.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.munitions.autocannon.ap_round.APAutocannonProjectile;

public class APFSDSAutocannonProjectile extends APAutocannonProjectile {
    private boolean highVelocity = false;

    public APFSDSAutocannonProjectile(EntityType<? extends APFSDSAutocannonProjectile> type, Level level) {
        super(type, level);
    }

    public void setHighVelocity(boolean hv) {
        this.highVelocity = hv;
    }

    @Override
    public void setDeltaMovement(Vec3 velocity) {
        if (this.highVelocity && velocity.lengthSqr() > 0.001) {
            this.highVelocity = false;
            super.setDeltaMovement(velocity.scale(2.0));
            return;
        }
        super.setDeltaMovement(velocity);
    }
}