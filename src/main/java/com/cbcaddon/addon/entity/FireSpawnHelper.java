package com.cbcaddon.addon.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/**
 * Global fire spawn cooldown to prevent lag from overlapping detonations.
 */
public class FireSpawnHelper {
    private static long lastFireSpawnTick = -100;
    private static final int FIRE_SPAWN_COOLDOWN = 10; // ticks between fire spawns

    public static boolean canSpawnFire(Level level) {
        long currentTick = level.getGameTime();
        if (currentTick - lastFireSpawnTick < FIRE_SPAWN_COOLDOWN) {
            return false;
        }
        lastFireSpawnTick = currentTick;
        return true;
    }

    public static void spawnFireField(Level level, BlockPos center, RandomSource random) {
        if (!canSpawnFire(level)) return;
        for (int x = -3; x <= 3; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    if (level.isEmptyBlock(pos) && random.nextFloat() < 0.4f) {
                        level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
                    }
                }
            }
        }
    }
}