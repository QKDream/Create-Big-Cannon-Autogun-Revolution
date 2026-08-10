package com.cbcaddon.addon.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/**
 * Lightweight fire spawner — small area, low density, no cooldown needed.
 */
public class FireSpawnHelper {
    /**
     * Spawns a small patch of fire around the impact point.
     * Kept intentionally small (3x3 footprint) to avoid lag.
     */
    public static void spawnFireField(Level level, BlockPos center, RandomSource random) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos pos = center.offset(x, 0, z);
                if (level.isEmptyBlock(pos) && random.nextFloat() < 0.35f) {
                    level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 2);
                }
                // Also try one block above
                BlockPos posUp = pos.above();
                if (level.isEmptyBlock(posUp) && random.nextFloat() < 0.25f) {
                    level.setBlock(posUp, Blocks.FIRE.defaultBlockState(), 2);
                }
            }
        }
    }
}