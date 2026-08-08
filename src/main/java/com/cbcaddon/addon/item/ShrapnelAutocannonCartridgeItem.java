package com.cbcaddon.addon.item;

import com.cbcaddon.addon.init.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonCartridgeItem;

public class ShrapnelAutocannonCartridgeItem extends AutocannonCartridgeItem {
    public ShrapnelAutocannonCartridgeItem(Properties properties) {
        super(properties);
    }

    @Override
    public EntityType<?> getEntityType(ItemStack stack) {
        return ModEntities.SHRAPNEL_AUTOCANNON.get();
    }
}