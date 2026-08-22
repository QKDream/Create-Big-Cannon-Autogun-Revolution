package com.cbcaddon.addon.mixin;

import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile;

/**
 * Self-destruct for cbcaddon projectiles:
 * - airborne for 20 seconds (400 ticks) -> silently discarded
 * - leaves the loaded chunk area while airborne -> discarded immediately
 *   (frozen entities in unloaded chunks no longer tick, so this must be
 *   checked at the end of the tick in which the projectile crossed over)
 * Grounded rounds are left alone so they can still be picked up.
 */
@Mixin(AbstractCannonProjectile.class)
public abstract class ProjectileSelfDestructMixin {

    private static final int SELF_DESTRUCT_TICKS = 400;

    @Inject(method = "tick", at = @At("HEAD"))
    private void cbcaddon$selfDestructAfter20s(CallbackInfo ci) {
        AbstractCannonProjectile self = (AbstractCannonProjectile) (Object) this;
        if (self.level().isClientSide()) return;
        if (!self.getType().toShortString().startsWith("cbcaddon:")) return;
        if (self.isInGround()) return;
        if (self.tickCount >= SELF_DESTRUCT_TICKS) {
            self.discard();
            return;
        }
        if (self.level() instanceof ServerLevel serverLevel && !serverLevel.hasChunkAt(self.blockPosition())) {
            self.discard();
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void cbcaddon$removeWhenLeavingLoadedChunks(CallbackInfo ci) {
        AbstractCannonProjectile self = (AbstractCannonProjectile) (Object) this;
        if (self.level().isClientSide()) return;
        if (!self.getType().toShortString().startsWith("cbcaddon:")) return;
        if (self.isInGround()) return;
        if (self.level() instanceof ServerLevel serverLevel && !serverLevel.hasChunkAt(self.blockPosition())) {
            self.discard();
        }
    }
}