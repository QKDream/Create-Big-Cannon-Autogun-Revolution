package com.cbcaddon.addon.item;

import com.cbcaddon.addon.entity.APFSDSAutocannonProjectile;
import com.cbcaddon.addon.entity.APHEAutocannonProjectile;
import com.cbcaddon.addon.entity.MultiPurposeAutocannonProjectile;
import com.cbcaddon.addon.entity.SAPAutocannonProjectile;
import com.cbcaddon.addon.entity.ShrapnelAutocannonProjectile;
import com.cbcaddon.addon.entity.ThermiteAutocannonProjectile;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonCartridgeItem;

import java.util.List;

public class HighVelocityAutocannonCartridgeItem extends AutocannonCartridgeItem {
    public HighVelocityAutocannonCartridgeItem(Properties properties) { super(properties); }

    @Override
    public AbstractAutocannonProjectile getAutocannonProjectile(ItemStack stack, Level level) {
        AbstractAutocannonProjectile projectile = super.getAutocannonProjectile(stack, level);
        if (projectile == null) return null;

        // Skip high velocity for frag grenade (slow ammo)
        String projClassName = projectile.getClass().getSimpleName();
        boolean isSlowAmmo = projClassName.contains("FragGrenade");

        // Apply high velocity (2x speed)
        if (!isSlowAmmo) {
            if (projectile instanceof APFSDSAutocannonProjectile apfsds) apfsds.setHighVelocity(true);
            else if (projectile instanceof APHEAutocannonProjectile aphe) aphe.setHighVelocity(true);
            else if (projectile instanceof SAPAutocannonProjectile sap) sap.setHighVelocity(true);
            else if (projectile instanceof ShrapnelAutocannonProjectile shr) shr.setHighVelocity(true);
            else if (projectile instanceof ThermiteAutocannonProjectile thm) thm.setHighVelocity(true);
            else if (projectile instanceof MultiPurposeAutocannonProjectile mp) mp.setHighVelocity(true);
        }

        // Soul fire - mutually exclusive with incendiary tip
        CustomData cd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        boolean hasSoulFire = cd.contains("soul_fire");

        // Check if projectile already has incendiary effect from CBC
        boolean hasIncendiary = false;
        if (AutocannonCartridgeItem.hasProjectile(stack)) {
            ItemStack round = AutocannonCartridgeItem.getProjectileStack(stack);
            CustomData rd = round.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            hasIncendiary = rd.contains("incendiary");
        }

        if (hasSoulFire && !hasIncendiary) {
            if (projectile instanceof APHEAutocannonProjectile aphe) aphe.setSoulFire(true);
            else if (projectile instanceof SAPAutocannonProjectile sap) sap.setSoulFire(true);
            else if (projectile instanceof ShrapnelAutocannonProjectile shr) shr.setSoulFire(true);
            else if (projectile instanceof ThermiteAutocannonProjectile thm) thm.setSoulFire(true);
            else if (projectile instanceof MultiPurposeAutocannonProjectile mp) mp.setSoulFire(true);
        }

        return projectile;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.cbcaddon.high_velocity"));
        CustomData cd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (cd.contains("soul_fire")) {
            tooltip.add(Component.translatable("tooltip.cbcaddon.soul_fire_applied"));
        }
    }
}