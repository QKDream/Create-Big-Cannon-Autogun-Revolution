package com.cbcaddon.addon.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/**
 * Minimal fire spawner - single block only, zero lag.
 */
public class FireSpawnHelper {
    public static void spawnFireField(Level level, BlockPos center) {
        if (level.isEmptyBlock(center)) {
            level.setBlock(center, Blocks.FIRE.defaultBlockState(), 2);
        }
    }
}