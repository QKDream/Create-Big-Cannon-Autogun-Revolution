package com.cbcaddon.addon.item;

import com.cbcaddon.addon.entity.SmokeAutocannonProjectile;
import com.cbcaddon.addon.init.ModEntities;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.index.CBCDataComponents;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonRoundItem;

import java.util.List;

public class SmokeAutocannonRoundItem extends FlakAutocannonRoundItem {
    public SmokeAutocannonRoundItem(Properties properties) { super(properties); }
    @Override public EntityType<?> getEntityType(ItemStack stack) { return ModEntities.SMOKE_AUTOCANNON.get(); }
    @Override
    public AbstractAutocannonProjectile getAutocannonProjectile(ItemStack stack, Level level) {
        SmokeAutocannonProjectile projectile = ModEntities.SMOKE_AUTOCANNON.get().create(level);
        if (stack.has(CBCDataComponents.FUZE)) {
            ItemStack fuzeStack = stack.getOrDefault(CBCDataComponents.FUZE, ItemContainerContents.EMPTY).copyOne();
            projectile.setFuze(fuzeStack);
        }
        // Check for stored potion contents
        PotionContents potion = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        if (potion != PotionContents.EMPTY) {
            projectile.setPotionContents(potion);
        }
        return projectile;
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        PotionContents potion = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        if (potion != PotionContents.EMPTY) {
            tooltip.add(Component.translatable("tooltip.cbcaddon.smoke_potion_loaded"));
            potion.addPotionTooltip(tooltip::add, 1.0f, context.tickRate());
        } else {
            tooltip.add(Component.translatable("tooltip.cbcaddon.smoke_round"));
        }
    }
}