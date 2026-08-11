package com.cbcaddon.addon.item;

import com.cbcaddon.addon.entity.SmokeAutocannonProjectile;
import com.cbcaddon.addon.init.ModEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonRoundItem;

import java.util.List;

public class SmokeAutocannonRoundItem extends FlakAutocannonRoundItem {
    public SmokeAutocannonRoundItem(Properties properties) { super(properties); }
    @Override public EntityType<?> getEntityType(ItemStack stack) { return ModEntities.SMOKE_AUTOCANNON.get(); }
    @Override
    public AbstractAutocannonProjectile getAutocannonProjectile(ItemStack stack, Level level) {
        AbstractAutocannonProjectile projectile = super.getAutocannonProjectile(stack, level);
        if (projectile instanceof SmokeAutocannonProjectile p) {
            PotionContents pc = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            if (pc.hasEffects()) {
                ItemStack potionHolder = new ItemStack(net.minecraft.world.item.Items.LINGERING_POTION);
                potionHolder.set(DataComponents.POTION_CONTENTS, pc);
                p.setPotion(potionHolder);
            }
        }
        return projectile;
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.cbcaddon.smoke_round"));
        PotionContents pc = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        if (pc.hasEffects()) {
            pc.addPotionTooltip(tooltip::add, 1.0f, context.tickRate());
        } else {
            tooltip.add(Component.translatable("tooltip.cbcaddon.smoke_no_potion"));
        }
    }
}