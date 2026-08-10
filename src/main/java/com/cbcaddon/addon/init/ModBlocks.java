package com.cbcaddon.addon.init;

import com.cbcaddon.addon.CBCAddon;
import com.cbcaddon.addon.block.FuzeControllerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(net.minecraft.core.registries.Registries.BLOCK, CBCAddon.MOD_ID);

    public static final Supplier<FuzeControllerBlock> FUZE_CONTROLLER =
            BLOCKS.register("fuze_controller",
                    () -> new FuzeControllerBlock(BlockBehaviour.Properties.of()
                            .strength(3.5f, 6.0f)
                            .requiresCorrectToolForDrops()));
}