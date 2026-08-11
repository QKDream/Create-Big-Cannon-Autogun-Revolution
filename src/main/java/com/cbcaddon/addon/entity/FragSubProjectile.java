package com.cbcaddon.addon.entity;

import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FragSubProjectile extends Projectile {
    private int life = 0;

    public FragSubProjectile(EntityType<? extends FragSubProjectile> type, Level level) {
        super(type, level);
        this.setNoGravity(false);
    }

    public void setOwner(Entity owner) { super.setOwner(owner); }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        // Manual movement before super.tick
        Vec3 movement = this.getDeltaMovement();
        double nx = this.getX() + movement.x;
        double ny = this.getY() + movement.y;
        double nz = this.getZ() + movement.z;
        this.setPos(nx, ny, nz);

        // Apply gravity
        if (!this.isNoGravity()) {
            this.setDeltaMovement(movement.add(0.0, -0.05, 0.0));
        }

        super.tick();
        life++;

        if (!this.level().isClientSide) {
            if (life >= 5) {
                this.subDetonate(this.position());
                return;
            }
            AABB box = this.getBoundingBox().inflate(0.5);
            for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class, box)) {
                if (e == this.getOwner()) continue;
                if (!e.isAlive()) continue;
                this.subDetonate(this.position());
                return;
            }
        }
    }

    private void subDetonate(Position position) {
        if (!this.level().isClientSide) {
            AABB area = new AABB(position.x() - 2, position.y() - 2, position.z() - 2,
                                 position.x() + 2, position.y() + 2, position.z() + 2);
            for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class, area)) {
                e.hurt(this.damageSources().explosion(this, this.getOwner()), e.getMaxHealth() * 0.1f);
            }
        }
        this.discard();
    }
}