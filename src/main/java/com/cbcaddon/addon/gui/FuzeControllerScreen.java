package com.cbcaddon.addon.gui;

import com.cbcaddon.addon.CBCAddon;
import com.cbcaddon.addon.network.FuzeUpdatePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class FuzeControllerScreen extends AbstractContainerScreen<FuzeControllerMenu> {
    private static final ResourceLocation BG = ResourceLocation.fromNamespaceAndPath(CBCAddon.MOD_ID, "textures/gui/fuze_controller.png");

    private EditBox distanceField;
    private EditBox timerField;
    private Button contactBtn;
    private Button timedBtn;
    private Button proximityBtn;
    private int lastMode = -1;
    private int lastTimer = -1;
    private float lastDist = -1;

    public FuzeControllerScreen(FuzeControllerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        int x = this.leftPos;
        int y = this.topPos;

        this.contactBtn = Button.builder(Component.translatable("gui.cbcaddon.fuze_controller.contact"), btn -> {
            sendUpdate(0);
        }).bounds(x + 8, y + 30, 50, 20).build();

        this.timedBtn = Button.builder(Component.translatable("gui.cbcaddon.fuze_controller.timed"), btn -> {
            sendUpdate(1);
        }).bounds(x + 62, y + 30, 50, 20).build();

        this.proximityBtn = Button.builder(Component.translatable("gui.cbcaddon.fuze_controller.proximity"), btn -> {
            sendUpdate(2);
        }).bounds(x + 116, y + 30, 50, 20).build();

        this.timerField = new EditBox(this.font, x + 10, y + 68, 60, 20,
            Component.translatable("gui.cbcaddon.fuze_controller.timer"));
        this.timerField.setFilter(val -> val.isEmpty() || val.matches("\\d{0,3}"));

        this.distanceField = new EditBox(this.font, x + 10, y + 98, 60, 20,
            Component.translatable("gui.cbcaddon.fuze_controller.distance"));
        this.distanceField.setFilter(val -> val.isEmpty() || val.matches("\\d{0,2}(\\.\\d{0,1})?"));

        this.addRenderableWidget(contactBtn);
        this.addRenderableWidget(timedBtn);
        this.addRenderableWidget(proximityBtn);
        this.addRenderableWidget(timerField);
        this.addRenderableWidget(distanceField);

        refreshFromBE();
    }

    private void sendUpdate(int mode) {
        if (this.menu.blockEntity == null) return;
        int timer = 60;
        float dist = 3.0f;
        try {
            if (!this.timerField.getValue().isEmpty()) {
                timer = Math.max(10, Math.min(600, Integer.parseInt(this.timerField.getValue())));
            }
        } catch (NumberFormatException ignored) {}
        try {
            if (!this.distanceField.getValue().isEmpty()) {
                dist = Math.max(0.5f, Math.min(32f, Float.parseFloat(this.distanceField.getValue())));
            }
        } catch (NumberFormatException ignored) {}

        PacketDistributor.sendToServer(new FuzeUpdatePacket(
            this.menu.blockEntity.getBlockPos(), mode, timer, dist));
    }

    private void refreshFromBE() {
        if (this.menu.blockEntity == null) return;
        String mode = this.menu.getMode();
        int modeIdx = switch (mode) { case "timed" -> 1; case "proximity" -> 2; default -> 0; };
        if (modeIdx != lastMode) {
            lastMode = modeIdx;
            contactBtn.active = modeIdx != 0;
            timedBtn.active = modeIdx != 1;
            proximityBtn.active = modeIdx != 2;
        }
        int timer = this.menu.getTimer();
        if (timer != lastTimer && !this.timerField.isFocused()) {
            lastTimer = timer;
            this.timerField.setValue(String.valueOf(timer));
        }
        float dist = this.menu.getDistance();
        if (Math.abs(dist - lastDist) > 0.05f && !this.distanceField.isFocused()) {
            lastDist = dist;
            this.distanceField.setValue(String.valueOf(dist));
        }
    }

    @Override
    protected void renderBg(GuiGraphics gfx, float partialTick, int mouseX, int mouseY) {
        gfx.blit(BG, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics gfx, int mouseX, int mouseY) {
        gfx.drawString(this.font, this.title, 8, 6, 0x404040, false);
        gfx.drawString(this.font, Component.translatable("gui.cbcaddon.fuze_controller.mode"), 8, 18, 0x606060, false);
        gfx.drawString(this.font, Component.translatable("gui.cbcaddon.fuze_controller.timer_label"), 80, 71, 0x606060, false);
        gfx.drawString(this.font, Component.translatable("gui.cbcaddon.fuze_controller.distance_label"), 80, 101, 0x606060, false);
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        super.render(gfx, mouseX, mouseY, partialTick);
        this.renderTooltip(gfx, mouseX, mouseY);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        refreshFromBE();
    }

    @Override
    public void onClose() {
        int modeIdx = lastMode;
        sendUpdate(modeIdx);
        super.onClose();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.timerField.keyPressed(keyCode, scanCode, modifiers) || this.timerField.isFocused()) return true;
        if (this.distanceField.keyPressed(keyCode, scanCode, modifiers) || this.distanceField.isFocused()) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}