package com.cbcaddon.addon.item;

import com.cbcaddon.addon.entity.ShrapnelAutocannonProjectile;
import com.cbcaddon.addon.init.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.munitions.FuzedItemMunition;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonCartridgeItem;

public class ShrapnelAutocannonCartridgeItem extends AutocannonCartridgeItem implements FuzedItemMunition {
    public ShrapnelAutocannonCartridgeItem(Properties properties) {
        super(properties);
    }

    @Override
    public EntityType<?> getEntityType(ItemStack stack) {
        return ModEntities.SHRAPNEL_AUTOCANNON.get();
    }

    @Override
    public AbstractAutocannonProjectile getAutocannonProjectile(ItemStack stack, Level level) {
        return new ShrapnelAutocannonProjectile(ModEntities.SHRAPNEL_AUTOCANNON.get(), level);
    }
}