package com.cbcaddon.addon.item;

import com.cbcaddon.addon.entity.APHEAutocannonProjectile;
import com.cbcaddon.addon.entity.SmartFuzeHelper;
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

public class APHEAutocannonRoundItem extends FlakAutocannonRoundItem {
    public APHEAutocannonRoundItem(Properties properties) { super(properties); }
    @Override
    public EntityType<?> getEntityType(ItemStack stack) { return ModEntities.APHE_AUTOCANNON.get(); }
    @Override
    public AbstractAutocannonProjectile getAutocannonProjectile(ItemStack stack, Level level) {
        APHEAutocannonProjectile projectile = ModEntities.APHE_AUTOCANNON.get().create(level);
        boolean hasSmartFuze = false;
        if (stack.has(CBCDataComponents.FUZE)) {
            ItemStack fuzeStack = stack.getOrDefault(CBCDataComponents.FUZE, ItemContainerContents.EMPTY).copyOne();
            if (fuzeStack.getItem() instanceof SmartFuzeItem) {
                projectile.setSmartFuzeMode(SmartFuzeHelper.resolveMode(fuzeStack, level));
                projectile.setSmartFuzeDist(SmartFuzeHelper.resolveProximityDistance(fuzeStack, level));
                projectile.setSmartFuzeTimer(SmartFuzeHelper.resolveTimer(fuzeStack, level));
                hasSmartFuze = true;
            }
            if (!hasSmartFuze) {
                projectile.setFuze(fuzeStack);
            }
        }
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (customData.contains("soul_fire")) projectile.setSoulFire(true);
        return projectile;
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.cbcaddon.aphe_round"));
        tooltip.add(Component.translatable("tooltip.cbcaddon.penetration", 14));
        CustomData d = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (d.contains("soul_fire")) tooltip.add(Component.translatable("tooltip.cbcaddon.soul_fire_applied"));
        if (stack.has(CBCDataComponents.FUZE)) {
            ItemStack fuze = stack.getOrDefault(CBCDataComponents.FUZE, ItemContainerContents.EMPTY).copyOne();
            if (fuze.getItem() instanceof SmartFuzeItem) {
                SmartFuzeItem.Mode mode = SmartFuzeItem.getMode(fuze);
                tooltip.add(Component.translatable(mode.translationKey));
                if (mode == SmartFuzeItem.Mode.PROXIMITY)
                    tooltip.add(Component.translatable("tooltip.cbcaddon.smart_fuze.distance", SmartFuzeItem.getProximityDistance(fuze)));
                if (mode == SmartFuzeItem.Mode.TIMED)
                    tooltip.add(Component.translatable("tooltip.cbcaddon.smart_fuze.timer_info", 60));
            }
        }
    }
}