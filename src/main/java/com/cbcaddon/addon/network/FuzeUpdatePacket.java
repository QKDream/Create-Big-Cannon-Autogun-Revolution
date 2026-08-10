package com.cbcaddon.addon.network;

import com.cbcaddon.addon.CBCAddon;
import com.cbcaddon.addon.block.FuzeControllerBlockEntity;
import com.cbcaddon.addon.item.SmartFuzeItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import rbasamoyai.createbigcannons.index.CBCDataComponents;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonRoundItem;

public record FuzeUpdatePacket(BlockPos pos, int modeIndex, int timer, float distance) implements CustomPacketPayload {
    public static final Type<FuzeUpdatePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CBCAddon.MOD_ID, "fuze_update"));

    public static final StreamCodec<ByteBuf, FuzeUpdatePacket> STREAM_CODEC = new StreamCodec<>() {
        @Override public FuzeUpdatePacket decode(ByteBuf buf) {
            return new FuzeUpdatePacket(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()), buf.readInt(), buf.readInt(), buf.readFloat());
        }
        @Override public void encode(ByteBuf buf, FuzeUpdatePacket pkt) {
            buf.writeInt(pkt.pos.getX()); buf.writeInt(pkt.pos.getY()); buf.writeInt(pkt.pos.getZ());
            buf.writeInt(pkt.modeIndex); buf.writeInt(pkt.timer); buf.writeFloat(pkt.distance);
        }
    };

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(FuzeUpdatePacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            Level level = player.level();
            String mode = switch (packet.modeIndex) { case 1 -> "timed"; case 2 -> "proximity"; default -> "contact"; };
            int timer = Math.max(10, Math.min(600, packet.timer));
            float dist = Math.max(0.5f, Math.min(32f, packet.distance));
            SmartFuzeItem.Mode sm = SmartFuzeItem.Mode.fromId(mode);

            // Update controller BE
            if (level.isLoaded(packet.pos)) {
                BlockEntity be = level.getBlockEntity(packet.pos);
                if (be instanceof FuzeControllerBlockEntity controller) {
                    controller.setFuzeMode(mode);
                    controller.setFuzeTimer(timer);
                    controller.setProximityDistance(dist);
                }
            }

            // Scan ALL inventory slots (including hotbar, main, armor, offhand)
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                updateFuzeIfBound(player.getInventory().getItem(i), packet.pos, sm, dist);
            }
            updateFuzeIfBound(player.getOffhandItem(), packet.pos, sm, dist);
        });
    }

    private static void updateFuzeIfBound(ItemStack stack, BlockPos controllerPos, SmartFuzeItem.Mode mode, float dist) {
        if (stack.isEmpty()) return;

        // Direct smart fuze item
        if (stack.getItem() instanceof SmartFuzeItem) {
            BlockPos bound = SmartFuzeItem.getControllerPos(stack);
            if (bound != null && bound.equals(controllerPos)) {
                SmartFuzeItem.setMode(stack, mode);
                SmartFuzeItem.setProximityDistance(stack, dist);
            }
            return;
        }

        // Ammo round containing a smart fuze in its FUZE slot
        if (stack.getItem() instanceof AutocannonRoundItem && stack.has(CBCDataComponents.FUZE)) {
            ItemStack fuzeStack = stack.getOrDefault(CBCDataComponents.FUZE, ItemContainerContents.EMPTY).copyOne();
            if (!fuzeStack.isEmpty() && fuzeStack.getItem() instanceof SmartFuzeItem) {
                BlockPos bound = SmartFuzeItem.getControllerPos(fuzeStack);
                if (bound != null && bound.equals(controllerPos)) {
                    SmartFuzeItem.setMode(fuzeStack, mode);
                    SmartFuzeItem.setProximityDistance(fuzeStack, dist);
                    // Write back to the round's FUZE component
                    stack.set(CBCDataComponents.FUZE, ItemContainerContents.fromItems(java.util.List.of(fuzeStack)));
                }
            }
        }
    }
}