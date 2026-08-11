package com.cbcaddon.addon.recipe;

import com.cbcaddon.addon.init.ModItems;
import com.cbcaddon.addon.init.ModRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class SmokePotionRecipe extends CustomRecipe {

    public SmokePotionRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        boolean hasSmoke = false;
        boolean hasPotion = false;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() == ModItems.SMOKE_AUTOCANNON_ROUND.get()) {
                if (hasSmoke) return false;
                // Don't allow already-potioned rounds
                PotionContents existing = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
                if (existing != PotionContents.EMPTY) return false;
                hasSmoke = true;
            } else if (stack.getItem() == Items.LINGERING_POTION) {
                if (hasPotion) return false;
                hasPotion = true;
            } else {
                return false;
            }
        }
        return hasSmoke && hasPotion;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack smokeStack = ItemStack.EMPTY;
        ItemStack potionStack = ItemStack.EMPTY;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && stack.getItem() == ModItems.SMOKE_AUTOCANNON_ROUND.get()) {
                smokeStack = stack;
            } else if (!stack.isEmpty() && stack.getItem() == Items.LINGERING_POTION) {
                potionStack = stack;
            }
        }
        if (smokeStack.isEmpty() || potionStack.isEmpty()) return ItemStack.EMPTY;

        ItemStack result = smokeStack.copyWithCount(1);
        PotionContents potionContents = potionStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        if (potionContents != PotionContents.EMPTY) {
            result.set(DataComponents.POTION_CONTENTS, potionContents);
        }
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SMOKE_POTION.get();
    }
}