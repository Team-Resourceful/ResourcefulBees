package com.resourcefulbees.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class ToolTip {
    int minX;
    int minY;
    int maxX;
    int maxY;
    Supplier<Component> text;

    public ToolTip(int minX, int minY, int sizeX, int sizeY, Supplier<Component> text) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = minX + sizeX;
        this.maxY = minY + sizeY;
        this.text = text;
    }

    public void draw(Screen screen, PoseStack matrix, int mouseX, int mouseY) {
        if (mouseX > minX && mouseX < maxX && mouseY > minY && mouseY < maxY) {
            screen.renderTooltip(matrix, text.get(), mouseX, mouseY);
        }
    }
}