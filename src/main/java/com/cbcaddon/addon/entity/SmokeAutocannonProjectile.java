package com.cbcaddon.addon.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleTypes;
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

public class SmokeAutocannonProjectile extends FlakAutocannonProjectile {
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
        ServerLevel sl = (ServerLevel) this.level();

        if (hasPotion && !potionStack.isEmpty()) {
            // Potion mode: AreaEffectCloud
            AreaEffectCloud cloud = new AreaEffectCloud(this.level(), center.getX() + 0.5, center.getY() + 0.5, center.getZ() + 0.5);
            cloud.setRadius(2.0f);
            cloud.setDuration(100);
            cloud.setWaitTime(0);
            PotionContents contents = potionStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            contents.getAllEffects().forEach(e -> cloud.addEffect(new MobEffectInstance(e)));
            this.level().addFreshEntity(cloud);
        } else {
            // Smoke mode: dense smoke particles
            sl.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                center.getX() + 0.5, center.getY() + 0.5, center.getZ() + 0.5,
                40, 1.5, 1.0, 1.5, 0.01);
            sl.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                center.getX() + 0.5, center.getY() + 1.5, center.getZ() + 0.5,
                20, 2.0, 1.0, 2.0, 0.01);
        }
    }
}