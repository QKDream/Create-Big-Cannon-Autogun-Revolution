package com.cbcaddon.addon.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile.ImpactResult;
import rbasamoyai.createbigcannons.munitions.ProjectileContext;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;

public class ThermiteAutocannonProjectile extends FlakAutocannonProjectile {
    private boolean highVelocity;
    private boolean soulFire;
    private boolean hasDetonated;
    private BlockPos targetBlock;

    public ThermiteAutocannonProjectile(EntityType<? extends ThermiteAutocannonProjectile> type, Level level) { super(type, level); }
    public void setHighVelocity(boolean hv) { this.highVelocity = hv; }
    public void setSoulFire(boolean sf) { this.soulFire = sf; }

    @Override
    public void tick() {
        if (this.highVelocity && this.tickCount == 0) {
            this.setDeltaMovement(this.getDeltaMovement().scale(2.0));
            this.highVelocity = false;
        }
        super.tick();
    }
    @Override
    protected boolean onImpact(HitResult hitResult, ImpactResult impactResult, ProjectileContext projectileContext) {
        if (hitResult instanceof BlockHitResult blockHit) {
            this.targetBlock = blockHit.getBlockPos().immutable();
        }
        boolean handled = super.onImpact(hitResult, impactResult, projectileContext);
        if (handled || this.level().isClientSide || this.hasDetonated) return handled;
        this.hasDetonated = true;
        if (this.targetBlock != null) {
            this.detonate(Vec3.atCenterOf(this.targetBlock));
        } else {
            this.detonate(hitResult.getLocation());
        }
        this.removeNextTick = true;
        return true;
    }
    @Override
    protected void detonate(Position position) {
        super.detonate(position);
        BlockPos center = this.targetBlock != null ? this.targetBlock : BlockPos.containing(position);
        if (!this.level().isClientSide) {
            if (this.random.nextFloat() < 0.33f) {
                this.level().destroyBlock(center, false, this.getOwner());
            }
            BlockPos above = center.above();
            if (this.level().isEmptyBlock(above)) {
                this.level().setBlock(above, this.soulFire ? Blocks.SOUL_FIRE.defaultBlockState() : Blocks.FIRE.defaultBlockState(), 3);
            }
            if (this.soulFire) {
                AABB area = new AABB(center).inflate(1.5);
                for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class, area)) {
                    e.hurt(this.damageSources().explosion(this, this.getOwner()), e.getMaxHealth() * 0.25f);
                }
            }
        }
    }
}