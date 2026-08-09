package com.cbcaddon.addon.init;

import com.cbcaddon.addon.CBCAddon;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, CBCAddon.MOD_ID);

    public static final Supplier<DataComponentType<Boolean>> SOUL_FIRE =
            DATA_COMPONENTS.register("soul_fire",
                    () -> DataComponentType.<Boolean>builder()
                            .persistent(Codec.BOOL)
                            .build());
}