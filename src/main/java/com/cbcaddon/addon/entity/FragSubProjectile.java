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
    private boolean isFragment = false;

    public FragSubProjectile(EntityType<? extends FragSubProjectile> type, Level level) {
        super(type, level);
        this.setNoGravity(false);
    }

    public void setOwner(Entity owner) { super.setOwner(owner); }
    public void setFragment(boolean f) { this.isFragment = f; }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        Vec3 movement = this.getDeltaMovement();
        double nx = this.getX() + movement.x;
        double ny = this.getY() + movement.y;
        double nz = this.getZ() + movement.z;
        this.setPos(nx, ny, nz);

        if (!this.isNoGravity()) {
            this.setDeltaMovement(movement.add(0.0, -0.05, 0.0));
        }

        super.tick();
        life++;

        if (!this.level().isClientSide) {
            if (life >= 5) {
                if (this.isFragment) {
                    this.fragmentDamage(this.position());
                } else {
                    this.subDetonate(this.position());
                }
                return;
            }
            AABB box = this.getBoundingBox().inflate(0.5);
            for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class, box)) {
                if (e == this.getOwner()) continue;
                if (!e.isAlive()) continue;
                if (this.isFragment) {
                    this.fragmentDamage(this.position());
                } else {
                    this.subDetonate(this.position());
                }
                return;
            }
        }
    }

    private void subDetonate(Position position) {
        if (!this.level().isClientSide) {
            // Spawn fragment shrapnel wave
            for (int i = 0; i < 10; i++) {
                FragSubProjectile frag = (FragSubProjectile) this.getType().create(this.level());
                if (frag == null) continue;
                frag.setPos(position.x(), position.y(), position.z());
                double theta = this.random.nextDouble() * Math.PI * 2;
                double phi = Math.acos(2 * this.random.nextDouble() - 1);
                double speed = 0.4 + this.random.nextDouble() * 0.6;
                Vec3 vel = new Vec3(
                    Math.sin(phi) * Math.cos(theta) * speed,
                    Math.sin(phi) * Math.sin(theta) * speed,
                    Math.cos(phi) * speed
                );
                frag.setDeltaMovement(vel);
                if (this.getOwner() != null) frag.setOwner(this.getOwner());
                frag.setFragment(true);
                this.level().addFreshEntity(frag);
            }
            // Direct damage
            fragmentDamage(position);
        }
        this.discard();
    }

    private void fragmentDamage(Position position) {
        AABB area = new AABB(position.x() - 2, position.y() - 2, position.z() - 2,
                             position.x() + 2, position.y() + 2, position.z() + 2);
        for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class, area)) {
            e.hurt(this.damageSources().explosion(this, this.getOwner()), e.getMaxHealth() * 0.1f);
        }
        this.discard();
    }
}