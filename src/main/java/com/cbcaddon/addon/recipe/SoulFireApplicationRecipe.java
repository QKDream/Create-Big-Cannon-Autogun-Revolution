package com.cbcaddon.addon.recipe;

import com.cbcaddon.addon.init.ModItems;
import com.cbcaddon.addon.init.ModRecipeSerializers;
import com.cbcaddon.addon.item.HighVelocityAutocannonCartridgeItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonCartridgeItem;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonRoundItem;

public class SoulFireApplicationRecipe extends CustomRecipe {

    public SoulFireApplicationRecipe(CraftingBookCategory category) {
        super(category);
    }

    private static boolean hasSoulFire(ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return data.contains("soul_fire");
    }

    private static void setSoulFire(ItemStack stack) {
        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY,
                data -> data.update(tag -> tag.putBoolean("soul_fire", true)));
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        boolean hasTarget = false;
        boolean hasDevice = false;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            boolean isRound = stack.getItem() instanceof AutocannonRoundItem;
            boolean isCartridge = stack.getItem() instanceof AutocannonCartridgeItem
                    && !(stack.getItem() instanceof HighVelocityAutocannonCartridgeItem);
            if ((isRound || isCartridge) && !hasSoulFire(stack)) {
                if (hasTarget) return false;
                hasTarget = true;
            } else if (stack.getItem() == ModItems.SOUL_FIRE_DEVICE.get()) {
                if (hasDevice) return false;
                hasDevice = true;
            } else {
                return false;
            }
        }
        return hasTarget && hasDevice;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack target = ItemStack.EMPTY;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && ((stack.getItem() instanceof AutocannonRoundItem)
                    || (stack.getItem() instanceof AutocannonCartridgeItem
                        && !(stack.getItem() instanceof HighVelocityAutocannonCartridgeItem)))) {
                target = stack;
                break;
            }
        }
        if (target.isEmpty()) return ItemStack.EMPTY;
        ItemStack result = target.copyWithCount(1);
        setSoulFire(result);

        if (result.getItem() instanceof AutocannonCartridgeItem && AutocannonCartridgeItem.hasProjectile(result)) {
            ItemStack projectile = AutocannonCartridgeItem.getProjectileStack(result);
            if (!projectile.isEmpty()) {
                ItemStack updatedProjectile = projectile.copy();
                setSoulFire(updatedProjectile);
                AutocannonCartridgeItem.writeProjectile(updatedProjectile, result);
            }
        }

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