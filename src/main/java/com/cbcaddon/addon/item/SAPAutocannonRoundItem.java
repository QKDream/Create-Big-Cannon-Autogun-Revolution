package com.cbcaddon.addon.item;

import com.cbcaddon.addon.entity.SAPAutocannonProjectile;
import com.cbcaddon.addon.init.ModEntities;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.index.CBCDataComponents;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonRoundItem;

public class SAPAutocannonRoundItem extends FlakAutocannonRoundItem {
    public SAPAutocannonRoundItem(Properties properties) {
        super(properties);
    }

    @Override
    public EntityType<?> getEntityType(ItemStack stack) {
        return ModEntities.SAP_AUTOCANNON.get();
    }

    @Override
    public AbstractAutocannonProjectile getAutocannonProjectile(ItemStack stack, Level level) {
        SAPAutocannonProjectile projectile = ModEntities.SAP_AUTOCANNON.get().create(level);
        if (stack.has(CBCDataComponents.FUZE)) {
            projectile.setFuze(stack.getOrDefault(CBCDataComponents.FUZE,
                    net.minecraft.world.item.component.ItemContainerContents.EMPTY).copyOne());
        }
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (customData.contains("soul_fire")) {
            projectile.setSoulFire(true);
        }
        return projectile;
    }
}