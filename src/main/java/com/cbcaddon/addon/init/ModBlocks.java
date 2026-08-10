package com.cbcaddon.addon.init;

import com.cbcaddon.addon.CBCAddon;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, CBCAddon.MOD_ID);
}