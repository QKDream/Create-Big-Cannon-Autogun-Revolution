package com.cbcaddon.addon.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import rbasamoyai.createbigcannons.index.CBCDataComponents;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonRoundItem;
import rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem;

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
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof FuzeControllerBlockEntity controller)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        // Right-click with a fuze-compatible round to program it
        if (stack.getItem() instanceof AutocannonRoundItem) {
            if (!level.isClientSide) {
                String fuzeId = switch (controller.getFuzeMode()) {
                    case "timed" -> "createbigcannons:timed_fuze";
                    case "proximity" -> "createbigcannons:proximity_fuze";
                    default -> "createbigcannons:impact_fuze";
                };
                ItemStack fuzeStack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(fuzeId)));
                stack.set(CBCDataComponents.FUZE, ItemContainerContents.fromItems(java.util.List.of(fuzeStack)));
                player.displayClientMessage(
                    Component.translatable("message.cbcaddon.round_programmed", controller.getFuzeMode()), true);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        // Right-click with empty hand to open GUI, Shift+empty to bind mount
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