package com.cbcaddon.addon.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.component.DataComponents;
import rbasamoyai.createbigcannons.munitions.ProjectileContext;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.big_cannon.smoke_shell.SmokeEmitterEntity;

public class SmokeAutocannonProjectile extends FlakAutocannonProjectile {
    private static final ResourceLocation SMOKE_EMITTER_ID =
            ResourceLocation.fromNamespaceAndPath("createbigcannons", "smoke_emitter");

    private boolean hasPotion;
    private ItemStack potionStack = ItemStack.EMPTY;

    public SmokeAutocannonProjectile(EntityType<? extends SmokeAutocannonProjectile> type, Level level) { super(type, level); }

    public void setPotion(ItemStack potion) {
        this.hasPotion = !potion.isEmpty();
        this.potionStack = potion;
    }

    @Override
    protected boolean onImpact(HitResult result, AbstractCannonProjectile.ImpactResult impactResult, ProjectileContext context) {
        this.detonate(this.position());
        return true;
    }

    @Override
    protected boolean onClip(ProjectileContext context, Vec3 motion, Vec3 hitPos) {
        this.detonate(hitPos);
        return true;
    }

    @Override
    protected void detonate(Position position) {
        if (this.level().isClientSide) return;
        BlockPos center = BlockPos.containing(position);

        EntityType<?> emitterType = BuiltInRegistries.ENTITY_TYPE.get(SMOKE_EMITTER_ID);
        if (emitterType != null) {
            SmokeEmitterEntity cloud = (SmokeEmitterEntity) emitterType.create(this.level());
            if (cloud != null) {
                cloud.setPos(position.x(), position.y(), position.z());
                cloud.setDuration(120);
                cloud.setSizeX(3.0f);
                cloud.setSizeY(2.0f);
                cloud.setSizeZ(3.0f);
                this.level().addFreshEntity(cloud);
            }
        }

        if (hasPotion && !potionStack.isEmpty()) {
            AreaEffectCloud aec = new AreaEffectCloud(this.level(), center.getX() + 0.5, center.getY() + 0.5, center.getZ() + 0.5);
            aec.setRadius(2.0f);
            aec.setDuration(100);
            aec.setWaitTime(0);
            PotionContents contents = potionStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            contents.getAllEffects().forEach(e -> aec.addEffect(new MobEffectInstance(e)));
            this.level().addFreshEntity(aec);
        }
    }
}