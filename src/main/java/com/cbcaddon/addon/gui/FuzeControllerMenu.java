package com.cbcaddon.addon.gui;

import com.cbcaddon.addon.block.FuzeControllerBlockEntity;
import com.cbcaddon.addon.init.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FuzeControllerMenu extends AbstractContainerMenu {

    public final FuzeControllerBlockEntity blockEntity;

    public FuzeControllerMenu(int containerId, Inventory playerInventory, FuzeControllerBlockEntity be) {
        super(ModMenuTypes.FUZE_CONTROLLER.get(), containerId);
        this.blockEntity = be;
    }

    public FuzeControllerMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(ModMenuTypes.FUZE_CONTROLLER.get(), containerId);
        BlockPos pos = buf.readBlockPos();
        Level level = playerInventory.player.level();
        BlockEntity be = level.getBlockEntity(pos);
        this.blockEntity = (be instanceof FuzeControllerBlockEntity controller) ? controller : null;
    }

    public String getMode() {
        return blockEntity != null ? blockEntity.getFuzeMode() : "contact";
    }

    public int getTimer() {
        return blockEntity != null ? blockEntity.getFuzeTimer() : 60;
    }

    public float getDistance() {
        return blockEntity != null ? blockEntity.getProximityDistance() : 3.0f;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        if (blockEntity == null) return false;
        return blockEntity.getLevel() != null &&
               blockEntity.getLevel().getBlockEntity(blockEntity.getBlockPos()) == blockEntity &&
               player.distanceToSqr(
                   blockEntity.getBlockPos().getX() + 0.5,
                   blockEntity.getBlockPos().getY() + 0.5,
                   blockEntity.getBlockPos().getZ() + 0.5) <= 64.0;
    }
}