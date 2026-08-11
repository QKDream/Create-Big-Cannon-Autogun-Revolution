package com.cbcaddon.addon.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.core.component.DataComponents;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.big_cannon.smoke_shell.SmokeExplosion;

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
        if (this.level().isClientSide) return;

        if (hasPotion && !potionStack.isEmpty()) {
            // Potion mode: Fluid Shell style - AreaEffectCloud only, no flak explosion
            BlockPos center = BlockPos.containing(position);
            AreaEffectCloud cloud = new AreaEffectCloud(this.level(), center.getX() + 0.5, center.getY() + 0.5, center.getZ() + 0.5);
            cloud.setRadius(2.0f);
            cloud.setDuration(100);
            cloud.setWaitTime(0);
            PotionContents contents = potionStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            contents.getAllEffects().forEach(e -> cloud.addEffect(new MobEffectInstance(e)));
            this.level().addFreshEntity(cloud);
        } else {
            // Smoke mode: CBC native SmokeExplosion (smaller scale)
            SmokeExplosion explosion = new SmokeExplosion(
                this.level(), this,
                position.x(), position.y(), position.z(),
                2.0f, 1.5f,
                Explosion.BlockInteraction.KEEP
            );
            explosion.explode();
        }
    }
}