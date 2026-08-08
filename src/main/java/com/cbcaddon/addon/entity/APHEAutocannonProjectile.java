package com.cbcaddon.addon.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;

public class APHEAutocannonProjectile extends FlakAutocannonProjectile {
    public APHEAutocannonProjectile(EntityType<? extends APHEAutocannonProjectile> type, Level level) {
        super(type, level);
    }
}