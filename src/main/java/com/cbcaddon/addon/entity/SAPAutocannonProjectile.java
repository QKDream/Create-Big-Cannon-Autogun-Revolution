package com.cbcaddon.addon.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.munitions.ProjectileContext;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;

public class SAPAutocannonProjectile extends FlakAutocannonProjectile {
    public SAPAutocannonProjectile(EntityType<? extends SAPAutocannonProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    protected boolean onClip(ProjectileContext ctx, Vec3 start, Vec3 end) {
        detonate(end);
        return true;
    }
}