package com.cbcaddon.addon.item;

import com.cbcaddon.addon.entity.FragGrenadeProjectile;
import com.cbcaddon.addon.init.ModEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonRoundItem;

import java.util.List;

public class FragGrenadeRoundItem extends FlakAutocannonRoundItem {
    public FragGrenadeRoundItem(Properties properties) { super(properties); }
    @Override public EntityType<?> getEntityType(ItemStack stack) { return ModEntities.FRAG_GRENADE_AUTOCANNON.get(); }
    @Override
    public AbstractAutocannonProjectile getAutocannonProjectile(ItemStack stack, Level level) {
        return super.getAutocannonProjectile(stack, level);
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.cbcaddon.frag_grenade_round"));
        tooltip.add(Component.translatable("tooltip.cbcaddon.frag_grenade_info"));
    }
}