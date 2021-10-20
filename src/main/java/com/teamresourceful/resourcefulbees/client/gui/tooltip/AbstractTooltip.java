package com.teamresourceful.resourcefulbees.client.gui.tooltip;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public abstract class AbstractTooltip {

    private final int x;
    private final int y;
    private final int hoverWidth;
    private final int hoverHeight;

    public AbstractTooltip(int x, int y, int hoverWidth, int hoverHeight) {
        this.x = x;
        this.y = y;
        this.hoverWidth = hoverWidth;
        this.hoverHeight = hoverHeight;
    }

    private boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= (double) this.x && mouseY >= (double) this.y && mouseX < (double) (this.x + this.hoverWidth) && mouseY < (double) (this.y + this.hoverHeight);
    }

    public void draw(Screen screen, MatrixStack matrix, int mouseX, int mouseY) {
        if (isHovered(mouseX, mouseY)) {
            if (Minecraft.getInstance().options.advancedItemTooltips) {
                screen.renderComponentTooltip(matrix, getAdvancedTooltip(), mouseX, mouseY);
            } else {
                screen.renderComponentTooltip(matrix, getTooltip(), mouseX, mouseY);
            }
        }
    }

    public abstract List<ITextComponent> getTooltip();

    public List<ITextComponent> getAdvancedTooltip() {
        return getTooltip();
    }
}
