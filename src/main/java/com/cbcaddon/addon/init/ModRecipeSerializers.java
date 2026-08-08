package com.cbcaddon.addon.init;

import com.cbcaddon.addon.CBCAddon;
import com.cbcaddon.addon.recipe.HighVelocityCartridgeAssemblyRecipe;
import com.cbcaddon.addon.recipe.HighVelocityCartridgeUpgradeRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, CBCAddon.MOD_ID);

    public static final Supplier<RecipeSerializer<HighVelocityCartridgeAssemblyRecipe>> HIGH_VELOCITY_ASSEMBLY =
            RECIPE_SERIALIZERS.register("high_velocity_cartridge_assembly",
                    () -> new SimpleCraftingRecipeSerializer<>(HighVelocityCartridgeAssemblyRecipe::new));

    public static final Supplier<RecipeSerializer<HighVelocityCartridgeUpgradeRecipe>> HIGH_VELOCITY_UPGRADE =
            RECIPE_SERIALIZERS.register("high_velocity_cartridge_upgrade",
                    () -> new SimpleCraftingRecipeSerializer<>(HighVelocityCartridgeUpgradeRecipe::new));
}