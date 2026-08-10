package com.cbcaddon.addon.item;

import com.cbcaddon.addon.entity.MultiPurposeAutocannonProjectile;
import com.cbcaddon.addon.init.ModEntities;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.index.CBCDataComponents;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonRoundItem;

import java.util.List;

public class MultiPurposeAutocannonRoundItem extends FlakAutocannonRoundItem {
    public MultiPurposeAutocannonRoundItem(Properties properties) { super(properties); }

    @Override
    public EntityType<?> getEntityType(ItemStack stack) { return ModEntities.MULTIPURPOSE_AUTOCANNON.get(); }

    @Override
    public AbstractAutocannonProjectile getAutocannonProjectile(ItemStack stack, Level level) {
        MultiPurposeAutocannonProjectile projectile = ModEntities.MULTIPURPOSE_AUTOCANNON.get().create(level);
        if (stack.has(CBCDataComponents.FUZE)) {
            ItemStack fuzeStack = stack.getOrDefault(CBCDataComponents.FUZE, ItemContainerContents.EMPTY).copyOne();
            projectile.setFuze(fuzeStack);
            if (fuzeStack.getItem() instanceof SmartFuzeItem) {
                projectile.setSmartFuzeMode(SmartFuzeItem.getMode(fuzeStack));
                projectile.setSmartFuzeDist(SmartFuzeItem.getProximityDistance(fuzeStack));
                projectile.setSmartFuzeTimer(60);
            }
        }
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (customData.contains("soul_fire")) projectile.setSoulFire(true);
        return projectile;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.cbcaddon.multipurpose_round"));
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (customData.contains("soul_fire")) tooltip.add(Component.translatable("tooltip.cbcaddon.soul_fire_applied"));
        if (stack.has(CBCDataComponents.FUZE)) {
            ItemStack fuze = stack.getOrDefault(CBCDataComponents.FUZE, ItemContainerContents.EMPTY).copyOne();
            if (fuze.getItem() instanceof SmartFuzeItem) {
                SmartFuzeItem.Mode mode = SmartFuzeItem.getMode(fuze);
                tooltip.add(Component.translatable(mode.translationKey));
                if (mode == SmartFuzeItem.Mode.PROXIMITY)
                    tooltip.add(Component.translatable("tooltip.cbcaddon.smart_fuze.distance", SmartFuzeItem.getProximityDistance(fuze)));
            }
        }
    }
}