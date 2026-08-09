package com.cbcaddon.addon.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;

public class ThermiteAutocannonProjectile extends FlakAutocannonProjectile {
    private boolean highVelocity = false;

    public ThermiteAutocannonProjectile(EntityType<? extends ThermiteAutocannonProjectile> type, Level level) {
        super(type, level);
    }

    public void setHighVelocity(boolean hv) {
        this.highVelocity = hv;
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
        if (!this.level().isClientSide) {
            BlockPos center = BlockPos.containing(position);
            for (int x = -2; x <= 2; x++) {
                for (int y = -2; y <= 2; y++) {
                    for (int z = -2; z <= 2; z++) {
                        BlockPos pos = center.offset(x, y, z);
                        BlockState state = this.level().getBlockState(pos);
                        if (!state.isAir() && state.getDestroySpeed(this.level(), pos) >= 0
                                && this.random.nextInt(3) == 0) {
                            this.level().destroyBlock(pos, false);
                        }
                        if (this.level().isEmptyBlock(pos.above())
                                && state.isFlammable(this.level(), pos, null)) {
                            this.level().setBlock(pos.above(), Blocks.FIRE.defaultBlockState(), Block.UPDATE_ALL);
                        }
                    }
                }
            }
        }
    }
}