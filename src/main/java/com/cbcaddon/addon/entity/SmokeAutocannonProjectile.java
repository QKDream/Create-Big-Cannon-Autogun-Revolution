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
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.big_cannon.smoke_shell.SmokeEmitterEntity;

public class SmokeAutocannonProjectile extends FlakAutocannonProjectile {
    private static final ResourceLocation SMOKE_EMITTER_ID =
            ResourceLocation.fromNamespaceAndPath("createbigcannons", "smoke_emitter");

    private boolean hasPotion;
    private PotionContents potionContents;
    private boolean highVelocity;

    private static final BallisticPropertiesComponent BALLISTIC = new BallisticPropertiesComponent(
        -0.02, 0.01, false, 0.5f, 0.0f, 0.0f, 0.50f
    );
    private static final EntityDamagePropertiesComponent DAMAGE = new EntityDamagePropertiesComponent(
        0.5f, false, false, true, 0.1f
    );

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

    @Override protected BallisticPropertiesComponent getBallisticProperties() { return BALLISTIC; }
    @Override public EntityDamagePropertiesComponent getDamageProperties() { return DAMAGE; }
    @Override public float getProjectileMass() { return 0.5f; }

    @Override
    protected void detonate(Position position) {
        if (!this.level().isClientSide) {
            if (this.hasPotion && !this.potionContents.equals(PotionContents.EMPTY)) {
                AreaEffectCloud cloud = new AreaEffectCloud(this.level(), position.x(), position.y(), position.z());
                cloud.setOwner(this.getOwner() instanceof LivingEntity le ? le : null);
                cloud.setRadius(3.0f);
                cloud.setRadiusOnUse(-0.5f);
                cloud.setWaitTime(10);
                cloud.setDuration(160);
                for (MobEffectInstance effect : this.potionContents.getAllEffects()) {
                    cloud.addEffect(effect);
                }
                this.level().addFreshEntity(cloud);
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