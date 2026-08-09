package com.cbcaddon.addon.item;

import com.cbcaddon.addon.entity.APFSDSAutocannonProjectile;
import com.cbcaddon.addon.entity.APHEAutocannonProjectile;
import com.cbcaddon.addon.entity.SAPAutocannonProjectile;
import com.cbcaddon.addon.entity.ShrapnelAutocannonProjectile;
import com.cbcaddon.addon.entity.ThermiteAutocannonProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonCartridgeItem;

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
        }
        return projectile;
    }
}