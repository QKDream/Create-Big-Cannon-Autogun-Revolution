package com.cbcaddon.addon.init;

import com.cbcaddon.addon.CBCAddon;
import com.cbcaddon.addon.block.FuzeControllerBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CBCAddon.MOD_ID);

    public static final Supplier<BlockEntityType<FuzeControllerBlockEntity>> FUZE_CONTROLLER =
            BLOCK_ENTITIES.register("fuze_controller",
                    () -> BlockEntityType.Builder.of(
                            FuzeControllerBlockEntity::new,
                            ModBlocks.FUZE_CONTROLLER.get()
                    ).build(null));
}