package com.cbcaddon.addon.recipe;

import com.cbcaddon.addon.init.ModDataComponents;
import com.cbcaddon.addon.init.ModItems;
import com.cbcaddon.addon.init.ModRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonRoundItem;

public class SoulFireApplicationRecipe extends CustomRecipe {

    public SoulFireApplicationRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        boolean hasRound = false;
        boolean hasDevice = false;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof AutocannonRoundItem && !stack.has(ModDataComponents.SOUL_FIRE.get())) {
                if (hasRound) return false;
                hasRound = true;
            } else if (stack.getItem() == ModItems.SOUL_FIRE_DEVICE.get()) {
                if (hasDevice) return false;
                hasDevice = true;
            } else {
                return false;
            }
        }
        return hasRound && hasDevice;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack round = ItemStack.EMPTY;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof AutocannonRoundItem) {
                round = stack;
                break;
            }
        }
        if (round.isEmpty()) return ItemStack.EMPTY;
        ItemStack result = round.copyWithCount(1);
        result.set(ModDataComponents.SOUL_FIRE.get(), true);
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SOUL_FIRE_APPLICATION.get();
    }
}