package com.cbcaddon.addon.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;

public class FragSubProjectile extends FlakAutocannonProjectile {
    private int fuseTimer = 5;
    private boolean hasDetonated = false;

    public FragSubProjectile(EntityType<? extends FragSubProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        if (this.hasDetonated) {
            this.discard();
            return;
        }
        super.tick();
        if (!this.isInGround() && this.getDeltaMovement().lengthSqr() > 0.001) {
            this.setOrientation(this.getDeltaMovement());
        }

        if (!this.level().isClientSide) {
            this.fuseTimer--;
            if (this.fuseTimer <= 0) {
                this.triggerDetonation();
            }
        }

        if (!this.level().isClientSide && this.tickCount > 2) {
            BlockPos pos = this.blockPosition();
            if (this.level().getBlockState(pos).isSolid()) {
                this.triggerDetonation();
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide) {
            this.triggerDetonation();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide) {
            this.triggerDetonation();
        }
    }

    private void triggerDetonation() {
        if (this.hasDetonated) return;
        this.hasDetonated = true;
        this.detonate(this.position());
        this.discard();
    }

    @Override
    protected void detonate(Position position) {
        super.detonate(position);
    }
}