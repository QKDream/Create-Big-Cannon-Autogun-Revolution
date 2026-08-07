package com.cbcaddon.addon.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;

public class ShrapnelAutocannonProjectile extends FlakAutocannonProjectile {
    public ShrapnelAutocannonProjectile(EntityType<? extends ShrapnelAutocannonProjectile> type, Level level) {
        super(type, level);
    }
}