package com.cbcaddon.addon.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;
import net.minecraft.core.Position;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.nbt.CompoundTag;

public class SmokeAutocannonProjectile extends FlakAutocannonProjectile {
    private boolean hasPotion;
    private ItemStack potionStack = ItemStack.EMPTY;

    public SmokeAutocannonProjectile(EntityType<? extends SmokeAutocannonProjectile> type, Level level) { super(type, level); }

    public void setPotion(ItemStack potion) {
        this.hasPotion = !potion.isEmpty();
        this.potionStack = potion;
    }

    @Override
    protected void detonate(Position position) {
        super.detonate(position);
        if (this.level().isClientSide) return;
        BlockPos center = BlockPos.containing(position);

        if (hasPotion && !potionStack.isEmpty()) {
            // Small potion cloud (like fluid shell but smaller)
            AreaEffectCloud cloud = new AreaEffectCloud(this.level(), center.getX() + 0.5, center.getY() + 0.5, center.getZ() + 0.5);
            cloud.setRadius(2.0f);
            cloud.setDuration(100);
            cloud.setWaitTime(0);
            PotionContents contents = potionStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            contents.getAllEffects().forEach(e -> cloud.addEffect(new MobEffectInstance(e)));
            this.level().addFreshEntity(cloud);
        } else {
            // Smoke effect (small smoke shell)
            for (int i = 0; i < 30; i++) {
                this.level().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    center.getX() + 0.5, center.getY() + 0.5, center.getZ() + 0.5,
                    (this.random.nextDouble() - 0.5) * 0.5, this.random.nextDouble() * 0.3,
                    (this.random.nextDouble() - 0.5) * 0.5);
            }
        }
    }
}