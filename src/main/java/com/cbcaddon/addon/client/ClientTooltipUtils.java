package com.cbcaddon.addon.client;

import net.minecraft.client.Minecraft;

public final class ClientTooltipUtils {
    private ClientTooltipUtils() {
    }

    public static boolean isPlayerSneaking() {
        return Minecraft.getInstance().player != null && Minecraft.getInstance().player.isShiftKeyDown();
    }
}
