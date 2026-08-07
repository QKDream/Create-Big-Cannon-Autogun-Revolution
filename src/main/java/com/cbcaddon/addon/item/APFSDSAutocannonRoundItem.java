package com.cbcaddon.addon.item;

import com.cbcaddon.addon.init.ModEntities;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonAmmoItem;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonAmmoType;
import rbasamoyai.createbigcannons.munitions.autocannon.ap_round.APAutocannonRoundItem;

public class APFSDSAutocannonRoundItem extends APAutocannonRoundItem implements AutocannonAmmoItem {
    public APFSDSAutocannonRoundItem(Properties properties) {
        super(properties);
    }

    @Override
    public EntityType<?> getEntityType(ItemStack stack) {
        return ModEntities.APFSDS_AUTOCANNON.get();
    }

    @Override
    public boolean isTracer(ItemStack stack) {
        return false;
    }

    @Override
    public void setTracer(ItemStack stack, boolean tracer) {
    }

    @Override
    public ItemStack getSpentItem(ItemStack stack) {
        return ItemStack.EMPTY;
    }

    @Override
    public AutocannonAmmoType getType() {
        return AutocannonAmmoType.AUTOCANNON;
    }
}