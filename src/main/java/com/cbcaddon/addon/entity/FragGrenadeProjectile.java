package com.cbcaddon.addon.entity;

import com.cbcaddon.addon.init.ModEntities;
import net.minecraft.core.Position;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;

public class FragGrenadeProjectile extends FlakAutocannonProjectile {
    private boolean hasDetonated = false;

    public FragGrenadeProjectile(EntityType<? extends FragGrenadeProjectile> type, Level level) {
        super(type, level);
    }
    @Override
    public void tick() {
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

        if (!this.level().isClientSide) {
            for (int i = 0; i < 8; i++) {
                FragSubProjectile sub = ModEntities.FRAG_SUB.get().create(this.level());
                if (sub != null) {
                    double theta = this.random.nextDouble() * Math.PI * 2;
                    double phi = this.random.nextDouble() * Math.PI * 2;
                    double vx = Math.sin(theta) * Math.cos(phi) * 0.5;
                    double vy = Math.sin(theta) * Math.sin(phi) * 0.5 + 0.2;
                    double vz = Math.cos(theta) * 0.5;
                    sub.setPos(position.x(), position.y() + 0.5, position.z());
                    sub.setDeltaMovement(vx, vy, vz);
                    this.level().addFreshEntity(sub);
                }
            }
        }
    }
}