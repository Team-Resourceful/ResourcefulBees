package com.teamresourceful.resourcefulbees.centrifuge.client.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static com.teamresourceful.resourcefulbees.client.util.TextUtils.FONT_COLOR_1;
import static com.teamresourceful.resourcefulbees.client.util.TextUtils.TERMINAL_FONT_8;

public class TerminalToastWidget implements Renderable {

    private @NotNull Component toastText;
    private int displayTime;
    private final int x;
    private final int y;

    public TerminalToastWidget(int x, int y) {
        this.toastText = Component.empty();
        this.x = x;
        this.y = y;
    }

    public void setToastText(@NotNull Component toastText, int displayTime) {
        if (displayTime < 1) return;
        this.toastText = toastText;
        this.displayTime = displayTime;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (displayTime > 0) {
            --displayTime;
            graphics.drawString(TERMINAL_FONT_8, toastText, x, y, FONT_COLOR_1, false);
        } else if (displayTime == 0) {
            displayTime = -1;
        }
    }
}
