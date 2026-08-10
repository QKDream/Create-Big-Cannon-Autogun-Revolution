package com.cbcaddon.addon.block;

import com.cbcaddon.addon.gui.FuzeControllerMenu;
import com.cbcaddon.addon.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import rbasamoyai.createbigcannons.index.CBCDataComponents;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonRoundItem;

public class FuzeControllerBlockEntity extends BlockEntity implements MenuProvider {

    private String fuzeMode = "contact";
    private float proximityDistance = 3.0f;
    private int fuzeTimer = 60;
    private BlockPos boundMountPos = null;

    public FuzeControllerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FUZE_CONTROLLER.get(), pos, blockState);
    }

    public String getFuzeMode() { return fuzeMode; }
    public void setFuzeMode(String mode) {
        this.fuzeMode = mode;
        setChanged();
        syncToMount();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public float getProximityDistance() { return proximityDistance; }
    public void setProximityDistance(float dist) {
        this.proximityDistance = Math.max(0.5f, Math.min(32f, dist));
        setChanged();
        syncToMount();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public int getFuzeTimer() { return fuzeTimer; }
    public void setFuzeTimer(int timer) {
        this.fuzeTimer = Math.max(10, Math.min(600, timer));
        setChanged();
        syncToMount();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public BlockPos getBoundMountPos() { return boundMountPos; }
    public void setBoundMountPos(BlockPos pos) {
        this.boundMountPos = pos;
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
    public boolean hasBoundMount() { return boundMountPos != null; }

    private void syncToMount() {
        if (level == null || boundMountPos == null || level.isClientSide) return;
        if (!level.isLoaded(boundMountPos)) return;
        BlockEntity be = level.getBlockEntity(boundMountPos);
        if (be == null) return;
        if (!be.getClass().getName().contains("CannonMountBlockEntity")) return;

        // Try to get item handler from the mount
        IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, boundMountPos, null);
        if (handler == null) return;

        // Find the fuze item that matches our mode
        String fuzeId = switch (fuzeMode) {
            case "timed" -> "createbigcannons:timed_fuze";
            case "proximity" -> "createbigcannons:proximity_fuze";
            default -> "createbigcannons:impact_fuze";
        };

        // Update all rounds in the mount's inventory
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (stack.isEmpty()) continue;
            if (!(stack.getItem() instanceof AutocannonRoundItem)) continue;

            // Check if there's already a fuze
            if (stack.has(CBCDataComponents.FUZE)) {
                // Replace the fuze item
                ItemStack newFuze = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(fuzeId)));
                stack.set(CBCDataComponents.FUZE, ItemContainerContents.fromItems(java.util.List.of(newFuze)));
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("FuzeMode", fuzeMode);
        tag.putFloat("ProximityDistance", proximityDistance);
        tag.putInt("FuzeTimer", fuzeTimer);
        if (boundMountPos != null) {
            tag.putInt("MountX", boundMountPos.getX());
            tag.putInt("MountY", boundMountPos.getY());
            tag.putInt("MountZ", boundMountPos.getZ());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("FuzeMode")) fuzeMode = tag.getString("FuzeMode");
        if (tag.contains("ProximityDistance")) proximityDistance = tag.getFloat("ProximityDistance");
        if (tag.contains("FuzeTimer")) fuzeTimer = tag.getInt("FuzeTimer");
        if (tag.contains("MountX")) {
            boundMountPos = new BlockPos(tag.getInt("MountX"), tag.getInt("MountY"), tag.getInt("MountZ"));
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cbcaddon.fuze_controller");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new FuzeControllerMenu(containerId, playerInventory, this);
    }
}