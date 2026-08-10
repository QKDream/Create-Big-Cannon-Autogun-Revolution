package com.cbcaddon.addon.gui;

import com.cbcaddon.addon.CBCAddon;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FuzeControllerScreen extends AbstractContainerScreen<FuzeControllerMenu> {
    private static final ResourceLocation BG = ResourceLocation.fromNamespaceAndPath(CBCAddon.MOD_ID, "textures/gui/fuze_controller.png");

    private EditBox distanceField;
    private Button contactBtn;
    private Button timedBtn;
    private Button proximityBtn;
    private int selectedMode = 0;
    private float proximityDist = 3.0f;

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
            selectedMode = 0;
            this.menu.setModeIndex(0);
            updateButtonStates();
            distanceField.setEditable(false);
        }).bounds(x + 10, y + 30, 50, 20).build();

        this.timedBtn = Button.builder(Component.translatable("gui.cbcaddon.fuze_controller.timed"), btn -> {
            selectedMode = 1;
            this.menu.setModeIndex(1);
            updateButtonStates();
            distanceField.setEditable(false);
        }).bounds(x + 65, y + 30, 50, 20).build();

        this.proximityBtn = Button.builder(Component.translatable("gui.cbcaddon.fuze_controller.proximity"), btn -> {
            selectedMode = 2;
            this.menu.setModeIndex(2);
            updateButtonStates();
            distanceField.setEditable(true);
        }).bounds(x + 120, y + 30, 50, 20).build();

        this.distanceField = new EditBox(this.font, x + 10, y + 60, 80, 20,
            Component.translatable("gui.cbcaddon.fuze_controller.distance"));
        this.distanceField.setValue(String.valueOf(proximityDist));
        this.distanceField.setResponder(val -> {
            try {
                float d = Float.parseFloat(val);
                proximityDist = Math.max(0.5f, Math.min(32f, d));
                this.menu.setProximityDistance(proximityDist);
            } catch (NumberFormatException ignored) {}
        });

        this.addRenderableWidget(contactBtn);
        this.addRenderableWidget(timedBtn);
        this.addRenderableWidget(proximityBtn);
        this.addRenderableWidget(distanceField);

        int modeFromMenu = this.menu.getModeIndex();
        selectedMode = modeFromMenu;
        proximityDist = this.menu.getProximityDistance();
        distanceField.setValue(String.valueOf(proximityDist));
        updateButtonStates();
        distanceField.setEditable(selectedMode == 2);
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
        gfx.drawString(this.font,
            Component.translatable("gui.cbcaddon.fuze_controller.mode"), 10, 18, 0x606060, false);
        gfx.drawString(this.font,
            Component.translatable("gui.cbcaddon.fuze_controller.distance_label"), 10, 48, 0x606060, false);
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        super.render(gfx, mouseX, mouseY, partialTick);
        this.renderTooltip(gfx, mouseX, mouseY);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        if (this.menu.getModeIndex() != selectedMode) {
            selectedMode = this.menu.getModeIndex();
            updateButtonStates();
            distanceField.setEditable(selectedMode == 2);
        }
    }
}