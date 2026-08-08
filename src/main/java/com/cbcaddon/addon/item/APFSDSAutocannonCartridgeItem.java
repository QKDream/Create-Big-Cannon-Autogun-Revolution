package com.cbcaddon.addon.item;

import com.cbcaddon.addon.init.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonCartridgeItem;

public class APFSDSAutocannonCartridgeItem extends AutocannonCartridgeItem {
    public APFSDSAutocannonCartridgeItem(Properties properties) {
        super(properties);
    }

    @Override
    public EntityType<?> getEntityType(ItemStack stack) {
        return ModEntities.APFSDS_AUTOCANNON.get();
    }
}