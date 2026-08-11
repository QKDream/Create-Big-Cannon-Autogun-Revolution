package com.cbcaddon.addon.item;

import com.cbcaddon.addon.CBCAddon;
import com.cbcaddon.addon.entity.APFSDSAutocannonProjectile;
import com.cbcaddon.addon.entity.APHEAutocannonProjectile;
import com.cbcaddon.addon.entity.MultiPurposeAutocannonProjectile;
import com.cbcaddon.addon.entity.SAPAutocannonProjectile;
import com.cbcaddon.addon.entity.ShrapnelAutocannonProjectile;
import com.cbcaddon.addon.entity.SmokeAutocannonProjectile;
import com.cbcaddon.addon.entity.ThermiteAutocannonProjectile;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonCartridgeItem;

import java.util.List;

public class HighVelocityAutocannonCartridgeItem extends AutocannonCartridgeItem {
    public HighVelocityAutocannonCartridgeItem(Properties properties) {
        super(properties);
    }

    @Override
    public AbstractAutocannonProjectile getAutocannonProjectile(ItemStack stack, Level level) {
        AbstractAutocannonProjectile projectile = super.getAutocannonProjectile(stack, level);
        if (projectile instanceof APFSDSAutocannonProjectile apfsds) {
            apfsds.setHighVelocity(true);
        } else if (projectile instanceof APHEAutocannonProjectile aphe) {
            aphe.setHighVelocity(true);
        } else if (projectile instanceof SAPAutocannonProjectile sap) {
            sap.setHighVelocity(true);
        } else if (projectile instanceof ShrapnelAutocannonProjectile shrapnel) {
            shrapnel.setHighVelocity(true);
        } else if (projectile instanceof ThermiteAutocannonProjectile thermite) {
            thermite.setHighVelocity(true);
        } else if (projectile instanceof MultiPurposeAutocannonProjectile mp) {
            mp.setHighVelocity(true);
        } else if (projectile instanceof SmokeAutocannonProjectile smoke) {
            smoke.setHighVelocity(true);
        }

        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (customData.contains("soul_fire")) {
            if (projectile instanceof APHEAutocannonProjectile aphe) {
                aphe.setSoulFire(true);
            } else if (projectile instanceof SAPAutocannonProjectile sap) {
                sap.setSoulFire(true);
            } else if (projectile instanceof ShrapnelAutocannonProjectile shrapnel) {
                shrapnel.setSoulFire(true);
            } else if (projectile instanceof ThermiteAutocannonProjectile thermite) {
                thermite.setSoulFire(true);
            }
        }

        return projectile;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.cbcaddon.high_velocity"));
    }
}