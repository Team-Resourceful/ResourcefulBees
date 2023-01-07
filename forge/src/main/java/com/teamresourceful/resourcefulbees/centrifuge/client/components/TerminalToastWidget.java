package com.teamresourceful.resourcefulbees.centrifuge.client.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static com.teamresourceful.resourcefulbees.client.utils.TextUtils.FONT_COLOR_1;
import static com.teamresourceful.resourcefulbees.client.utils.TextUtils.TERMINAL_FONT_8;

public class TerminalToastWidget extends GuiComponent implements Widget {

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
    public void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (displayTime > 0) {
            --displayTime;
            TERMINAL_FONT_8.draw(pPoseStack, toastText, x, y, FONT_COLOR_1);
        } else if (displayTime == 0) {
            displayTime = -1;
        }
    }
}
