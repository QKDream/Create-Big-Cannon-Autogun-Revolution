package com.cbcaddon.addon.item;

import com.cbcaddon.addon.entity.SAPAutocannonProjectile;
import com.cbcaddon.addon.init.ModEntities;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonRoundItem;

import java.util.List;

public class SAPAutocannonRoundItem extends FlakAutocannonRoundItem {
    public SAPAutocannonRoundItem(Properties properties) { super(properties); }
    @Override public EntityType<?> getEntityType(ItemStack stack) { return ModEntities.SAP_AUTOCANNON.get(); }
    @Override
    public AbstractAutocannonProjectile getAutocannonProjectile(ItemStack stack, Level level) {
        AbstractAutocannonProjectile projectile = super.getAutocannonProjectile(stack, level);
        if (projectile instanceof SAPAutocannonProjectile p) {
            CustomData d = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            if (d.contains("soul_fire")) p.setSoulFire(true);
        }
        return projectile;
    }
}