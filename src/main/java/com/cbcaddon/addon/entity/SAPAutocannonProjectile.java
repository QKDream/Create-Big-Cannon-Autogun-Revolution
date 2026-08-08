package com.cbcaddon.addon.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;

public class SAPAutocannonProjectile extends FlakAutocannonProjectile {
    public SAPAutocannonProjectile(EntityType<? extends SAPAutocannonProjectile> type, Level level) {
        super(type, level);
    }
}