package com.cbcaddon.addon.mixin.sable;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * SABLE rotates rendered entities by the ship orientation whenever they are
 * inside a sublevel (EntitySubLevelRotationHelper.getSubLevelInheritedOrientation).
 * cbcaddon projectiles must render in world space, so no orientation quaternion
 * is returned for them and the renderer leaves their rotation alone.
 *
 * This targets a real SABLE helper class (not a mixin-added synthetic method),
 * so applying it does not depend on SABLE's own mixin application order.
 */
@Pseudo
@Mixin(targets = "dev.ryanhcode.sable.mixinhelpers.camera.camera_rotation.EntitySubLevelRotationHelper", remap = false)
public abstract class EntitySubLevelRotationHelperMixin {

    @Inject(method = "getSubLevelInheritedOrientation", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void cbcaddon$noShipRotation(Entity entity, @Coerce Object poseGetter, @Coerce Object type,
                                                CallbackInfoReturnable<Object> cir) {
        if (entity != null && !entity.isRemoved() && entity.getType().toShortString().startsWith("cbcaddon:")) {
            cir.setReturnValue(null);
        }
    }
}

