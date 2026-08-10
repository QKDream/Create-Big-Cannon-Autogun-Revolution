package com.cbcaddon.addon.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class FireSpawnHelper {
    public static void spawnFireField(Level level, BlockPos center) {
        for (BlockPos p : BlockPos.betweenClosed(center.offset(-1, -1, -1), center.offset(1, 1, 1))) {
            if (level.isEmptyBlock(p)) {
                level.setBlock(p, Blocks.FIRE.defaultBlockState(), 2);
            }
        }
    }
}