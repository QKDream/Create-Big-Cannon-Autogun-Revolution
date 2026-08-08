package com.cbcaddon.addon.recipe;

import com.cbcaddon.addon.init.ModItems;
import com.cbcaddon.addon.init.ModRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonCartridgeItem;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonRoundItem;

public class HighVelocityCartridgeAssemblyRecipe extends CustomRecipe {

    public HighVelocityCartridgeAssemblyRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int roundIdx = -1;
        int cartridgeIdx = -1;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof AutocannonRoundItem) {
                if (roundIdx != -1) return false;
                roundIdx = i;
            } else if (stack.getItem() == ModItems.HIGH_VELOCITY_CARTRIDGE.get() && !AutocannonCartridgeItem.hasProjectile(stack)) {
                if (cartridgeIdx != -1) return false;
                cartridgeIdx = i;
            } else {
                return false;
            }
        }
        return roundIdx != -1 && cartridgeIdx != -1;
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
        ItemStack result = new ItemStack(ModItems.HIGH_VELOCITY_CARTRIDGE.get());
        AutocannonCartridgeItem.writeProjectile(round, result);
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.HIGH_VELOCITY_ASSEMBLY.get();
    }
}