package com.cbcaddon.addon.gui;

import com.cbcaddon.addon.CBCAddon;
import com.cbcaddon.addon.network.FuzeUpdatePacket;
import net.minecraft.client.Minecraft;
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
    private int selectedMode = 0;
    private float proximityDist = 3.0f;
    private int fuzeTimer = 60;
    private int tickCounter = 0;

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
            sendUpdate(0, fuzeTimer, proximityDist);
        }).bounds(x + 8, y + 30, 50, 20).build();

        this.timedBtn = Button.builder(Component.translatable("gui.cbcaddon.fuze_controller.timed"), btn -> {
            sendUpdate(1, fuzeTimer, proximityDist);
        }).bounds(x + 62, y + 30, 50, 20).build();

        this.proximityBtn = Button.builder(Component.translatable("gui.cbcaddon.fuze_controller.proximity"), btn -> {
            sendUpdate(2, fuzeTimer, proximityDist);
        }).bounds(x + 116, y + 30, 50, 20).build();

        this.timerField = new EditBox(this.font, x + 10, y + 70, 60, 20,
            Component.translatable("gui.cbcaddon.fuze_controller.timer"));
        this.timerField.setValue(String.valueOf(fuzeTimer));
        this.timerField.setFilter(val -> val.isEmpty() || val.matches("\\d{0,3}"));
        this.timerField.setResponder(val -> {
            if (!val.isEmpty()) {
                try {
                    int t = Integer.parseInt(val);
                    fuzeTimer = Math.max(10, Math.min(600, t));
                } catch (NumberFormatException ignored) {}
            }
        });

        this.distanceField = new EditBox(this.font, x + 10, y + 100, 60, 20,
            Component.translatable("gui.cbcaddon.fuze_controller.distance"));
        this.distanceField.setValue(String.valueOf(proximityDist));
        this.distanceField.setFilter(val -> val.isEmpty() || val.matches("\\d{0,2}(\\.\\d{0,1})?"));
        this.distanceField.setResponder(val -> {
            if (!val.isEmpty()) {
                try {
                    float d = Float.parseFloat(val);
                    proximityDist = Math.max(0.5f, Math.min(32f, d));
                } catch (NumberFormatException ignored) {}
            }
        });

        this.addRenderableWidget(contactBtn);
        this.addRenderableWidget(timedBtn);
        this.addRenderableWidget(proximityBtn);
        this.addRenderableWidget(timerField);
        this.addRenderableWidget(distanceField);

        updateButtonStates();
    }

    private void sendUpdate(int mode, int timer, float dist) {
        if (this.menu.blockEntity != null) {
            PacketDistributor.sendToServer(new FuzeUpdatePacket(
                this.menu.blockEntity.getBlockPos(), mode, timer, dist));
        }
        selectedMode = mode;
        updateButtonStates();
    }

    private void updateButtonStates() {
        contactBtn.active = selectedMode != 0;
        timedBtn.active = selectedMode != 1;
        proximityBtn.active = selectedMode != 2;
    }

    @Override
    protected void renderBg(GuiGraphics gfx, float partialTick, int mouseX, int mouseY) {
        gfx.blit(BG, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics gfx, int mouseX, int mouseY) {
        gfx.drawString(this.font, this.title, 8, 6, 0x404040, false);
        gfx.drawString(this.font, Component.translatable("gui.cbcaddon.fuze_controller.mode"), 8, 18, 0x606060, false);
        gfx.drawString(this.font, Component.translatable("gui.cbcaddon.fuze_controller.timer_label"), 80, 73, 0x606060, false);
        gfx.drawString(this.font, Component.translatable("gui.cbcaddon.fuze_controller.distance_label"), 80, 103, 0x606060, false);
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        super.render(gfx, mouseX, mouseY, partialTick);
        this.renderTooltip(gfx, mouseX, mouseY);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        tickCounter++;
        // Sync from server data every 5 ticks
        if (tickCounter % 5 == 0) {
            int serverMode = this.menu.getModeIndex();
            if (serverMode != selectedMode) {
                selectedMode = serverMode;
                updateButtonStates();
            }
            int serverTimer = this.menu.getTimer();
            if (serverTimer != fuzeTimer && !this.timerField.isFocused()) {
                fuzeTimer = serverTimer;
                this.timerField.setValue(String.valueOf(fuzeTimer));
            }
            float serverDist = this.menu.getProximityDistance();
            if (Math.abs(serverDist - proximityDist) > 0.05f && !this.distanceField.isFocused()) {
                proximityDist = serverDist;
                this.distanceField.setValue(String.valueOf(proximityDist));
            }
        }
        // Auto-send timer/distance changes when not focused
        if (tickCounter % 20 == 0 && !this.timerField.isFocused() && !this.distanceField.isFocused()) {
            sendUpdate(selectedMode, fuzeTimer, proximityDist);
        }
    }

    @Override
    public void onClose() {
        sendUpdate(selectedMode, fuzeTimer, proximityDist);
        super.onClose();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.timerField.keyPressed(keyCode, scanCode, modifiers) || this.timerField.isFocused()) {
            return true;
        }
        if (this.distanceField.keyPressed(keyCode, scanCode, modifiers) || this.distanceField.isFocused()) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}