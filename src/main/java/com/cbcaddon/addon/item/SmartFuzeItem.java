package com.cbcaddon.addon.item;

import com.cbcaddon.addon.block.FuzeControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem;

import java.util.List;

public class SmartFuzeItem extends FuzeItem {

    public enum Mode {
        CONTACT("contact", "tooltip.cbcaddon.smart_fuze.contact"),
        TIMED("timed", "tooltip.cbcaddon.smart_fuze.timed"),
        PROXIMITY("proximity", "tooltip.cbcaddon.smart_fuze.proximity");

        public final String id;
        public final String translationKey;
        Mode(String id, String key) { this.id = id; this.translationKey = key; }

        public static Mode fromId(String id) {
            for (Mode m : values()) if (m.id.equals(id)) return m;
            return CONTACT;
        }
    }

    public record FuzeSettings(Mode mode, float proximityDistance, int fuzeTimer) {}

    public static FuzeSettings readActiveSettings(ItemStack fuzeStack, Level level) {
        BlockPos controllerPos = getControllerPos(fuzeStack);
        if (controllerPos != null && level.isLoaded(controllerPos)) {
            BlockEntity be = level.getBlockEntity(controllerPos);
            if (be instanceof FuzeControllerBlockEntity controller) {
                return new FuzeSettings(
                    Mode.fromId(controller.getFuzeMode()),
                    controller.getProximityDistance(),
                    controller.getFuzeTimer()
                );
            }
        }
        return new FuzeSettings(getMode(fuzeStack), getProximityDistance(fuzeStack), getFuzeTimer(fuzeStack));
    }

    public SmartFuzeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof FuzeControllerBlockEntity) {
            if (!level.isClientSide) {
                ItemStack stack = context.getItemInHand();
                setControllerPos(stack, pos);
                setMode(stack, Mode.CONTACT);
                setProximityDistance(stack, 3.0f);
                setFuzeTimer(stack, 60);
                context.getPlayer().displayClientMessage(
                    Component.translatable("message.cbcaddon.fuze_bound", pos.toShortString()), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useOn(context);
    }

    public static boolean hasControllerPos(ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return data.contains("controllerX");
    }

    public static BlockPos getControllerPos(ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (!data.contains("controllerX")) return null;
        CompoundTag tag = data.copyTag();
        return new BlockPos(tag.getInt("controllerX"), tag.getInt("controllerY"), tag.getInt("controllerZ"));
    }

    public static void setControllerPos(ItemStack stack, BlockPos pos) {
        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY,
            d -> d.update(tag -> {
                tag.putInt("controllerX", pos.getX());
                tag.putInt("controllerY", pos.getY());
                tag.putInt("controllerZ", pos.getZ());
            }));
    }

    public static Mode getMode(ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (data.contains("fuzeMode")) {
            return Mode.fromId(data.copyTag().getString("fuzeMode"));
        }
        return Mode.CONTACT;
    }

    public static void setMode(ItemStack stack, Mode mode) {
        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY,
            d -> d.update(tag -> tag.putString("fuzeMode", mode.id)));
    }

    public static float getProximityDistance(ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (data.contains("proximityDistance")) {
            return data.copyTag().getFloat("proximityDistance");
        }
        return 3.0f;
    }

    public static void setProximityDistance(ItemStack stack, float dist) {
        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY,
            d -> d.update(tag -> tag.putFloat("proximityDistance", dist)));
    }

    public static int getFuzeTimer(ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (data.contains("fuzeTimer")) {
            return data.copyTag().getInt("fuzeTimer");
        }
        return 60;
    }

    public static void setFuzeTimer(ItemStack stack, int timer) {
        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY,
            d -> d.update(tag -> tag.putInt("fuzeTimer", Math.max(10, Math.min(600, timer)))));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (hasControllerPos(stack)) {
            BlockPos pos = getControllerPos(stack);
            tooltip.add(Component.translatable("tooltip.cbcaddon.smart_fuze.bound", pos.toShortString()));
        } else {
            tooltip.add(Component.translatable("tooltip.cbcaddon.smart_fuze.unbound"));
        }
        Mode mode = getMode(stack);
        tooltip.add(Component.translatable(mode.translationKey));
        if (mode == Mode.PROXIMITY) {
            tooltip.add(Component.translatable("tooltip.cbcaddon.smart_fuze.distance", getProximityDistance(stack)));
        }
        if (mode == Mode.TIMED) {
            tooltip.add(Component.translatable("tooltip.cbcaddon.smart_fuze.timer_info", getFuzeTimer(stack)));
        }
    }
}