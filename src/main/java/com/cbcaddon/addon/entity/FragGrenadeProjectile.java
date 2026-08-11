package com.cbcaddon.addon.entity;

import com.cbcaddon.addon.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;

public class FragGrenadeProjectile extends FlakAutocannonProjectile {

    public FragGrenadeProjectile(EntityType<? extends FragGrenadeProjectile> type, Level level) { super(type, level); }

    @Override
    public void tick() {
        if (!this.isInGround()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.24, 0));
        }
        super.tick();
    }

    @Override
    protected void detonate(Position position) {
        super.detonate(position);
        if (this.level().isClientSide) return;
        BlockPos center = BlockPos.containing(position);

        for (int i = 0; i < 48; i++) {
            FragSubProjectile sub = ModEntities.FRAG_SUB_PROJECTILE.get().create(this.level());
            if (sub == null) continue;
            sub.setPos(position.x(), position.y(), position.z());
            // Omnidirectional (sphere)
            double theta = this.random.nextDouble() * Math.PI * 2;
            double phi = Math.acos(2 * this.random.nextDouble() - 1);
            double speed = 0.6 + this.random.nextDouble() * 0.8;
            Vec3 vel = new Vec3(
                Math.sin(phi) * Math.cos(theta) * speed,
                Math.sin(phi) * Math.sin(theta) * speed,
                Math.cos(phi) * speed
            );
            sub.setDeltaMovement(vel);
            sub.setOwner(this.getOwner());
            this.level().addFreshEntity(sub);
        }

        AABB area = new AABB(center).inflate(1.5);
        for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class, area)) {
            e.hurt(this.damageSources().explosion(this, this.getOwner()), e.getMaxHealth() * 0.3f);
        }
    }
}