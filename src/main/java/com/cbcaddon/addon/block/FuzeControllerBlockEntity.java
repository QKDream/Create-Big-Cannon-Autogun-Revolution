package com.cbcaddon.addon.block;

import com.cbcaddon.addon.gui.FuzeControllerMenu;
import com.cbcaddon.addon.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FuzeControllerBlockEntity extends BlockEntity implements MenuProvider {

    private String fuzeMode = "contact";
    private float proximityDistance = 3.0f;
    private int fuzeTimer = 60;

    public FuzeControllerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FUZE_CONTROLLER.get(), pos, blockState);
    }

    public String getFuzeMode() { return fuzeMode; }
    public void setFuzeMode(String mode) {
        this.fuzeMode = mode;
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public float getProximityDistance() { return proximityDistance; }
    public void setProximityDistance(float dist) {
        this.proximityDistance = Math.max(0.5f, Math.min(32f, dist));
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public int getFuzeTimer() { return fuzeTimer; }
    public void setFuzeTimer(int timer) {
        this.fuzeTimer = Math.max(10, Math.min(600, timer));
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("FuzeMode", fuzeMode);
        tag.putFloat("ProximityDistance", proximityDistance);
        tag.putInt("FuzeTimer", fuzeTimer);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("FuzeMode")) fuzeMode = tag.getString("FuzeMode");
        if (tag.contains("ProximityDistance")) proximityDistance = tag.getFloat("ProximityDistance");
        if (tag.contains("FuzeTimer")) fuzeTimer = tag.getInt("FuzeTimer");
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