package com.cbcaddon.addon.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class FuzeControllerBlock extends BaseEntityBlock {
    public static final MapCodec<FuzeControllerBlock> CODEC = simpleCodec(FuzeControllerBlock::new);

    public FuzeControllerBlock(BlockBehaviour.Properties properties) { super(properties); }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    @Override
    public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FuzeControllerBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof FuzeControllerBlockEntity controller) {
                // Shift+right-click: bind to nearby CBC cannon mount
                if (player.isShiftKeyDown()) {
                    boolean found = false;
                    for (BlockPos p : BlockPos.betweenClosed(pos.offset(-5, -5, -5), pos.offset(5, 5, 5))) {
                        BlockEntity nearby = level.getBlockEntity(p);
                        if (nearby != null && nearby.getClass().getName().contains("CannonMountBlockEntity")) {
                            controller.setBoundMountPos(p.immutable());
                            player.displayClientMessage(
                                Component.translatable("message.cbcaddon.controller_bound_mount", p.toShortString()), true);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        player.displayClientMessage(
                            Component.translatable("message.cbcaddon.controller_no_mount"), true);
                    }
                    return InteractionResult.SUCCESS;
                }
                sp.openMenu(controller, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}