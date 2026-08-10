package com.cbcaddon.addon.network;

import com.cbcaddon.addon.CBCAddon;
import com.cbcaddon.addon.block.FuzeControllerBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record FuzeUpdatePacket(BlockPos pos, int modeIndex, int timer, float distance) implements CustomPacketPayload {
    public static final Type<FuzeUpdatePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CBCAddon.MOD_ID, "fuze_update"));

    public static final StreamCodec<ByteBuf, FuzeUpdatePacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public FuzeUpdatePacket decode(ByteBuf buf) {
            int x = buf.readInt();
            int y = buf.readInt();
            int z = buf.readInt();
            return new FuzeUpdatePacket(new BlockPos(x, y, z), buf.readInt(), buf.readInt(), buf.readFloat());
        }
        @Override
        public void encode(ByteBuf buf, FuzeUpdatePacket pkt) {
            buf.writeInt(pkt.pos.getX());
            buf.writeInt(pkt.pos.getY());
            buf.writeInt(pkt.pos.getZ());
            buf.writeInt(pkt.modeIndex);
            buf.writeInt(pkt.timer);
            buf.writeFloat(pkt.distance);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(FuzeUpdatePacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Level level = ctx.player().level();
            if (level.isLoaded(packet.pos)) {
                BlockEntity be = level.getBlockEntity(packet.pos);
                if (be instanceof FuzeControllerBlockEntity controller) {
                    String mode = switch (packet.modeIndex) {
                        case 1 -> "timed";
                        case 2 -> "proximity";
                        default -> "contact";
                    };
                    controller.setFuzeMode(mode);
                    controller.setFuzeTimer(Math.max(10, Math.min(600, packet.timer)));
                    controller.setProximityDistance(Math.max(0.5f, Math.min(32f, packet.distance)));
                }
            }
        });
    }
}