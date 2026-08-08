package com.cbcaddon.addon.item;

import com.cbcaddon.addon.entity.APFSDSAutocannonProjectile;
import com.cbcaddon.addon.init.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.ap_round.APAutocannonRoundItem;

public class APFSDSAutocannonRoundItem extends APAutocannonRoundItem {
    public APFSDSAutocannonRoundItem(Properties properties) {
        super(properties);
    }

    @Override
    public EntityType<?> getEntityType(ItemStack stack) {
        return ModEntities.APFSDS_AUTOCANNON.get();
    }

    @Override
    public AbstractAutocannonProjectile getAutocannonProjectile(ItemStack stack, Level level) {
        return ModEntities.APFSDS_AUTOCANNON.get().create(level);
    }
}