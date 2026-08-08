package com.cbcaddon.addon.recipe;

import com.cbcaddon.addon.init.ModItems;
import com.cbcaddon.addon.init.ModRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonCartridgeItem;

public class HighVelocityCartridgeUpgradeRecipe extends CustomRecipe {

    public HighVelocityCartridgeUpgradeRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        boolean hasCartridge = false;
        boolean hasGunpowder = false;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof AutocannonCartridgeItem && AutocannonCartridgeItem.hasProjectile(stack)) {
                if (hasCartridge) return false;
                hasCartridge = true;
            } else if (stack.is(Items.GUNPOWDER)) {
                if (hasGunpowder) return false;
                hasGunpowder = true;
            } else {
                return false;
            }
        }
        return hasCartridge && hasGunpowder;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack cartridge = ItemStack.EMPTY;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof AutocannonCartridgeItem) {
                cartridge = stack;
                break;
            }
        }
        if (cartridge.isEmpty()) return ItemStack.EMPTY;
        ItemStack projectile = AutocannonCartridgeItem.getProjectileStack(cartridge);
        ItemStack result = new ItemStack(ModItems.HIGH_VELOCITY_CARTRIDGE.get());
        AutocannonCartridgeItem.writeProjectile(projectile, result);
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.HIGH_VELOCITY_UPGRADE.get();
    }
}