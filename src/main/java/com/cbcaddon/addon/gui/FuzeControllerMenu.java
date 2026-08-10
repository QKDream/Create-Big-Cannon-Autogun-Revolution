package com.cbcaddon.addon.gui;

import com.cbcaddon.addon.block.FuzeControllerBlockEntity;
import com.cbcaddon.addon.init.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FuzeControllerMenu extends AbstractContainerMenu {
    public static final int DATA_MODE = 0;
    public static final int DATA_TIMER = 1;
    public static final int DATA_DISTANCE_INT = 2;

    public final FuzeControllerBlockEntity blockEntity;
    public final ContainerData data;

    // Server constructor
    public FuzeControllerMenu(int containerId, Inventory playerInventory, FuzeControllerBlockEntity be) {
        super(ModMenuTypes.FUZE_CONTROLLER.get(), containerId);
        this.blockEntity = be;
        this.data = new SimpleContainerData(3);
        syncFromBlockEntity();
        this.addDataSlots(this.data);
    }

    // Client constructor
    public FuzeControllerMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(ModMenuTypes.FUZE_CONTROLLER.get(), containerId);
        BlockPos pos = buf.readBlockPos();
        Level level = playerInventory.player.level();
        BlockEntity be = level.getBlockEntity(pos);
        this.blockEntity = (be instanceof FuzeControllerBlockEntity controller) ? controller : null;
        this.data = new SimpleContainerData(3);
        this.addDataSlots(this.data);
    }

    private void syncFromBlockEntity() {
        if (blockEntity != null) {
            this.data.set(DATA_MODE, switch (blockEntity.getFuzeMode()) {
                case "timed" -> 1;
                case "proximity" -> 2;
                default -> 0;
            });
            this.data.set(DATA_TIMER, blockEntity.getFuzeTimer());
            this.data.set(DATA_DISTANCE_INT, (int)(blockEntity.getProximityDistance() * 10));
        }
    }

    public int getModeIndex() { return data.get(DATA_MODE); }
    public int getTimer() { return data.get(DATA_TIMER); }
    public float getProximityDistance() { return data.get(DATA_DISTANCE_INT) / 10.0f; }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (blockEntity != null && !player.level().isClientSide) {
            if (id >= 0 && id <= 2) {
                String mode = switch (id) {
                    case 1 -> "timed";
                    case 2 -> "proximity";
                    default -> "contact";
                };
                blockEntity.setFuzeMode(mode);
                syncFromBlockEntity();
                return true;
            }
        }
        return super.clickMenuButton(player, id);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        if (blockEntity == null) return true;
        return blockEntity.getLevel() != null &&
               blockEntity.getLevel().getBlockEntity(blockEntity.getBlockPos()) == blockEntity &&
               player.distanceToSqr(
                   blockEntity.getBlockPos().getX() + 0.5,
                   blockEntity.getBlockPos().getY() + 0.5,
                   blockEntity.getBlockPos().getZ() + 0.5) <= 64.0;
    }
}