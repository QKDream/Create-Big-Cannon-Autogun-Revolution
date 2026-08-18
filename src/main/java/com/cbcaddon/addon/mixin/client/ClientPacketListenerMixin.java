package com.cbcaddon.addon.mixin.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * cbcaddon projectiles are always tracked in world space (the server never
 * stores them in ship-local coordinates). SABLE's client-side packet handling
 * still poses their spawn position, movement lerps and render rotation by the
 * ship pose whenever they are inside a sublevel region, which makes fast
 * autocannon rounds appear to spiral or fly sideways on ships even though the
 * real trajectory is correct.
 *
 * These handlers replicate vanilla 1.21.1 behaviour 1:1 for cbcaddon entities
 * and cancel the rest, so SABLE's posing never runs for them. They target only
 * vanilla classes, so they are independent of SABLE's mixin application order.
 */
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Shadow
    @Final
    private ClientLevel level;

    private static boolean cbcaddon$isOurEntity(Entity entity) {
        return entity != null && !entity.isRemoved() && entity.getType().toShortString().startsWith("cbcaddon:");
    }

    @Inject(method = "handleMoveEntity", at = @At("HEAD"), cancellable = true)
    private void cbcaddon$handleMoveEntity(ClientboundMoveEntityPacket packet, CallbackInfo ci) {
        Entity entity = packet.getEntity(this.level);
        if (!cbcaddon$isOurEntity(entity) || entity.isControlledByLocalInstance()) return;
        if (packet.hasPosition()) {
            VecDeltaCodec codec = entity.getPositionCodec();
            Vec3 pos = codec.decode(packet.getXa(), packet.getYa(), packet.getZa());
            codec.setBase(pos);
            float yRot = packet.hasRotation() ? packet.getyRot() * 360.0F / 256.0F : entity.lerpTargetYRot();
            float xRot = packet.hasRotation() ? packet.getxRot() * 360.0F / 256.0F : entity.lerpTargetXRot();
            entity.lerpTo(pos.x, pos.y, pos.z, yRot, xRot, 3);
        } else if (packet.hasRotation()) {
            entity.lerpTo(entity.lerpTargetX(), entity.lerpTargetY(), entity.lerpTargetZ(),
                    packet.getyRot() * 360.0F / 256.0F, packet.getxRot() * 360.0F / 256.0F, 3);
        }
        entity.setOnGround(packet.isOnGround());
        ci.cancel();
    }

    @Inject(method = "handleTeleportEntity", at = @At("HEAD"), cancellable = true)
    private void cbcaddon$handleTeleportEntity(ClientboundTeleportEntityPacket packet, CallbackInfo ci) {
        Entity entity = this.level.getEntity(packet.getId());
        if (!cbcaddon$isOurEntity(entity)) return;
        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        entity.syncPacketPositionCodec(x, y, z);
        if (!entity.isControlledByLocalInstance()) {
            entity.lerpTo(x, y, z, packet.getyRot() * 360.0F / 256.0F, packet.getxRot() * 360.0F / 256.0F, 3);
        }
        entity.setOnGround(packet.isOnGround());
        ci.cancel();
    }

    @Inject(method = "handleAddEntity", at = @At("TAIL"))
    private void cbcaddon$fixSpawnPose(ClientboundAddEntityPacket packet, CallbackInfo ci) {
        Entity entity = this.level.getEntity(packet.getId());
        if (cbcaddon$isOurEntity(entity)) {
            entity.setPos(packet.getX(), packet.getY(), packet.getZ());
            entity.setOldPosAndRot();
        }
    }
}

