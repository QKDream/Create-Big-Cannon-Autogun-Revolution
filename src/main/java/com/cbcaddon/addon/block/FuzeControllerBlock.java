package com.cbcaddon.addon.block;

import com.cbcaddon.addon.item.SmartFuzeItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                               Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() instanceof SmartFuzeItem) {
            if (!level.isClientSide) {
                SmartFuzeItem.setControllerPos(stack, pos);
                SmartFuzeItem.setMode(stack, SmartFuzeItem.Mode.CONTACT);
                SmartFuzeItem.setProximityDistance(stack, 3.0f);
                SmartFuzeItem.setFuzeTimer(stack, 60);
                player.displayClientMessage(
                    Component.translatable("message.cbcaddon.fuze_bound", pos.toShortString()), true);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof FuzeControllerBlockEntity controller) {
                sp.openMenu(controller, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}