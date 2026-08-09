package com.cbcaddon.addon.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;

public class SAPAutocannonProjectile extends FlakAutocannonProjectile {
    private boolean highVelocity = false;
    private boolean soulFire = false;

    public SAPAutocannonProjectile(EntityType<? extends SAPAutocannonProjectile> type, Level level) {
        super(type, level);
    }

    public void setHighVelocity(boolean hv) {
        this.highVelocity = hv;
    }

    public void setSoulFire(boolean sf) {
        this.soulFire = sf;
    }

    @Override
    public void tick() {
        if (this.highVelocity && this.tickCount == 1) {
            this.setDeltaMovement(this.getDeltaMovement().scale(2.0));
            this.highVelocity = false;
        }
        super.tick();
    }

    @Override
    protected void detonate(Position position) {
        super.detonate(position);
        if (this.soulFire && !this.level().isClientSide) {
            spawnSoulFire(position);
        }
    }

    private void spawnSoulFire(Position position) {
        BlockPos center = BlockPos.containing(position);
        for (int x = -3; x <= 3; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    if (this.level().isEmptyBlock(pos) && this.random.nextFloat() < 0.4f) {
                        this.level().setBlock(pos, Blocks.SOUL_FIRE.defaultBlockState(), 3);
                    }
                }
            }
        }
    }
}