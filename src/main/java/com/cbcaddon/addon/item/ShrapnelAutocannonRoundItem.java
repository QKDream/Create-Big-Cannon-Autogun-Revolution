package com.cbcaddon.addon.item;

import com.cbcaddon.addon.entity.ShrapnelAutocannonProjectile;
import com.cbcaddon.addon.init.ModEntities;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.index.CBCDataComponents;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonRoundItem;

import java.util.List;

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
            projectile.setFuze(stack.getOrDefault(CBCDataComponents.FUZE,
                    net.minecraft.world.item.component.ItemContainerContents.EMPTY).copyOne());
        }
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (customData.contains("soul_fire")) {
            projectile.setSoulFire(true);
        }
        return projectile;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.cbcaddon.shrapnel_round"));
        tooltip.add(Component.translatable("tooltip.cbcaddon.shrapnel_info"));
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (customData.contains("soul_fire")) {
            tooltip.add(Component.translatable("tooltip.cbcaddon.soul_fire_applied"));
        }
    }
}