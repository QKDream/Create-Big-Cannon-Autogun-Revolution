package com.cbcaddon.addon.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;

public class ThermiteAutocannonProjectile extends FlakAutocannonProjectile {
    private boolean highVelocity = false;
    private boolean soulFire = false;

    public ThermiteAutocannonProjectile(EntityType<? extends ThermiteAutocannonProjectile> type, Level level) {
        super(type, level);
    }

    public void setHighVelocity(boolean hv) {
        this.highVelocity = hv;
    }

    public void setSoulFire(boolean sf) {
        this.soulFire = sf;
    }

    @Override
    public void tick() {
        if (this.highVelocity && this.tickCount == 1) {
            this.setDeltaMovement(this.getDeltaMovement().scale(2.0));
            this.highVelocity = false;
        }
        super.tick();
    }

    @Override
    protected void detonate(Position position) {
        super.detonate(position);
        if (!this.level().isClientSide) {
            BlockPos center = BlockPos.containing(position);
            // Nerf: only mine the impact point block
            BlockState state = this.level().getBlockState(center);
            if (!state.isAir() && state.getDestroySpeed(this.level(), center) >= 0) {
                this.level().destroyBlock(center, false);
            }
            // Ignite above the impact point
            if (this.level().isEmptyBlock(center.above())) {
                this.level().setBlock(center.above(), this.soulFire ? Blocks.FIRE.defaultBlockState() : Blocks.FIRE.defaultBlockState(), 3);
            }
            // Soul fire device behavior: explosion + fire + %HP damage
            if (this.soulFire) {
                this.level().explode(this, position.x(), position.y(), position.z(), 2.0f, Level.ExplosionInteraction.NONE);
                for (int x = -3; x <= 3; x++) {
                    for (int y = -2; y <= 2; y++) {
                        for (int z = -3; z <= 3; z++) {
                            BlockPos pos = center.offset(x, y, z);
                            if (this.level().isEmptyBlock(pos) && this.random.nextFloat() < 0.4f) {
                                this.level().setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
                            }
                        }
                    }
                }
                AABB area = new AABB(center).inflate(1.0);
                for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, area)) {
                    float damage = entity.getMaxHealth() * 0.15f;
                    entity.hurt(this.damageSources().explosion(this, this.getOwner()), damage);
                }
            }
        }
    }
}