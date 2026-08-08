package com.cbcaddon.addon.item;

import com.cbcaddon.addon.entity.ShrapnelAutocannonProjectile;
import com.cbcaddon.addon.init.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.index.CBCDataComponents;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonRoundItem;

public class ShrapnelAutocannonRoundItem extends FlakAutocannonRoundItem {
    public ShrapnelAutocannonRoundItem(Properties properties) {
        super(properties);
    }

    @Override
    public EntityType<?> getEntityType(ItemStack stack) {
        return ModEntities.SHRAPNEL_AUTOCANNON.get();
    }

    @Override
    public AbstractAutocannonProjectile getAutocannonProjectile(ItemStack stack, Level level) {
        ShrapnelAutocannonProjectile projectile = ModEntities.SHRAPNEL_AUTOCANNON.get().create(level);
        if (stack.has(CBCDataComponents.FUZE)) {
            projectile.setFuze(stack.getOrDefault(CBCDataComponents.FUZE, net.minecraft.world.item.component.ItemContainerContents.EMPTY).copyOne());
        }
        return projectile;
    }
}