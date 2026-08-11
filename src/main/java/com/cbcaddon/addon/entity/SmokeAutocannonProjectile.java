package com.cbcaddon.addon.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;

public class SmokeAutocannonProjectile extends FlakAutocannonProjectile {
    private boolean hasPotion;
    private PotionContents potionContents;
    private boolean highVelocity;

    public SmokeAutocannonProjectile(EntityType<? extends SmokeAutocannonProjectile> type, Level level) {
        super(type, level);
        this.hasPotion = false;
        this.potionContents = PotionContents.EMPTY;
    }

    public void setPotionContents(PotionContents contents) {
        this.hasPotion = true;
        this.potionContents = contents;
    }

    public void setHighVelocity(boolean hv) { this.highVelocity = hv; }

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
            ServerLevel serverLevel = (ServerLevel) this.level();
            Entity owner = this.getOwner();

            if (this.hasPotion && this.potionContents != PotionContents.EMPTY) {
                AreaEffectCloud cloud = new AreaEffectCloud(this.level(), position.x(), position.y(), position.z());
                if (owner instanceof LivingEntity livingOwner) {
                    cloud.setOwner(livingOwner);
                }
                cloud.setRadius(2.5f);
                cloud.setRadiusOnUse(-0.5f);
                cloud.setWaitTime(10);
                cloud.setDuration(160);
                cloud.setRadiusPerTick(-0.01f);
                for (MobEffectInstance effect : this.potionContents.getAllEffects()) {
                    cloud.addEffect(new MobEffectInstance(effect));
                }
                this.level().addFreshEntity(cloud);
            } else {
                BlockPos center = BlockPos.containing(position);
                for (int i = 0; i < 30; i++) {
                    double dx = (this.random.nextDouble() - 0.5) * 3.0;
                    double dy = this.random.nextDouble() * 2.5;
                    double dz = (this.random.nextDouble() - 0.5) * 3.0;
                    serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            position.x() + dx, position.y() + dy, position.z() + dz,
                            1, 0.1, 0.1, 0.1, 0.02);
                }
                AreaEffectCloud smokeCloud = new AreaEffectCloud(this.level(), position.x(), position.y(), position.z());
                if (owner instanceof LivingEntity livingOwner) {
                    smokeCloud.setOwner(livingOwner);
                }
                smokeCloud.setRadius(2.5f);
                smokeCloud.setRadiusOnUse(-0.3f);
                smokeCloud.setWaitTime(0);
                smokeCloud.setDuration(120);
                smokeCloud.setRadiusPerTick(-0.005f);
                smokeCloud.setParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE);
                this.level().addFreshEntity(smokeCloud);
            }
        }
    }
}