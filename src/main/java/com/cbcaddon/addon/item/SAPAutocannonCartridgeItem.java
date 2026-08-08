package com.cbcaddon.addon.item;

import com.cbcaddon.addon.entity.SAPAutocannonProjectile;
import com.cbcaddon.addon.init.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.munitions.FuzedItemMunition;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonAmmoItem;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonAmmoType;
import rbasamoyai.createbigcannons.munitions.autocannon.config.AutocannonProjectilePropertiesComponent;

public class SAPAutocannonCartridgeItem extends Item implements AutocannonAmmoItem, FuzedItemMunition {
    public SAPAutocannonCartridgeItem(Properties properties) {
        super(properties);
    }

    @Override
    public EntityType<?> getEntityType(ItemStack stack) {
        return ModEntities.SAP_AUTOCANNON.get();
    }

    @Override
    public AbstractAutocannonProjectile getAutocannonProjectile(ItemStack stack, Level level) {
        return new SAPAutocannonProjectile(ModEntities.SAP_AUTOCANNON.get(), level);
    }

    @Override
    public AutocannonProjectilePropertiesComponent getAutocannonProperties(ItemStack stack) {
        return AutocannonProjectilePropertiesComponent.DEFAULT;
    }

    @Override
    public ItemStack getSpentItem(ItemStack stack) {
        return ItemStack.EMPTY;
    }

    @Override
    public AutocannonAmmoType getType() {
        return AutocannonAmmoType.AUTOCANNON;
    }

    @Override
    public boolean isTracer(ItemStack stack) {
        return false;
    }

    @Override
    public void setTracer(ItemStack stack, boolean tracer) {
    }
}