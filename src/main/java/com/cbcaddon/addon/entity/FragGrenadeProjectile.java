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
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.12, 0));
        }
        super.tick();
    }

    @Override
    protected void detonate(Position position) {
        super.detonate(position);
        if (this.level().isClientSide) return;
        BlockPos center = BlockPos.containing(position);

        for (int i = 0; i < 24; i++) {
            FragSubProjectile sub = ModEntities.FRAG_SUB_PROJECTILE.get().create(this.level());
            if (sub == null) continue;
            sub.setPos(position.x(), position.y(), position.z());
            Vec3 vel = new Vec3(
                (this.random.nextDouble() - 0.5) * 1.0,
                this.random.nextDouble() * 0.6 + 0.3,
                (this.random.nextDouble() - 0.5) * 1.0
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