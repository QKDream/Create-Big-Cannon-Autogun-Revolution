package com.cbcaddon.addon.entity;

import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.big_cannon.smoke_shell.SmokeEmitterEntity;

public class SmokeAutocannonProjectile extends FlakAutocannonProjectile {
    private static final ResourceLocation SMOKE_EMITTER_ID =
            ResourceLocation.fromNamespaceAndPath("createbigcannons", "smoke_emitter");

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
        if (this.highVelocity && this.tickCount == 0) {
            this.setDeltaMovement(this.getDeltaMovement().scale(2.0));
            this.highVelocity = false;
        }
        super.tick();
        if (!this.isInGround() && this.getDeltaMovement().lengthSqr() > 0.001) {
            this.setOrientation(this.getDeltaMovement());
        }
    }
    @Override
    protected void detonate(Position position) {
        if (!this.level().isClientSide) {
            if (this.hasPotion && this.potionContents != PotionContents.EMPTY) {
                Entity owner = this.getOwner();
                AreaEffectCloud cloud = new AreaEffectCloud(this.level(), position.x(), position.y(), position.z());
                if (owner instanceof LivingEntity livingOwner) {
                    cloud.setOwner(livingOwner);
                }
                cloud.setRadius(3.5f);
                cloud.setRadiusOnUse(-0.3f);
                cloud.setWaitTime(0);
                cloud.setDuration(200);
                cloud.setRadiusPerTick(-0.005f);
                cloud.setParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE);
                for (MobEffectInstance effect : this.potionContents.getAllEffects()) {
                    cloud.addEffect(new MobEffectInstance(effect));
                }
                this.level().addFreshEntity(cloud);

                EntityType<?> smokeType = BuiltInRegistries.ENTITY_TYPE.get(SMOKE_EMITTER_ID);
                if (smokeType != null) {
                    Entity e = smokeType.create(this.level());
                    if (e instanceof SmokeEmitterEntity smoke) {
                        smoke.setPos(new Vec3(position.x(), position.y(), position.z()));
                        smoke.setDuration(120);
                        smoke.setSize(4.0f);
                        this.level().addFreshEntity(smoke);
                    }
                }
            } else {
                EntityType<?> smokeType = BuiltInRegistries.ENTITY_TYPE.get(SMOKE_EMITTER_ID);
                if (smokeType != null) {
                    Entity e = smokeType.create(this.level());
                    if (e instanceof SmokeEmitterEntity smoke) {
                        smoke.setPos(new Vec3(position.x(), position.y(), position.z()));
                        smoke.setDuration(160);
                        smoke.setSize(4.0f);
                        this.level().addFreshEntity(smoke);
                    }
                }
            }
        }
        this.discard();
    }
}