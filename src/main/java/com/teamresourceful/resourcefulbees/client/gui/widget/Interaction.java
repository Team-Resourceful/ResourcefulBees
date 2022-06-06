package com.teamresourceful.resourcefulbees.client.gui.widget;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public record Interaction(int xPos, int yPos, int width, int height, Supplier<Boolean> supplier) {

    public boolean onMouseClick(int mouseX, int mouseY) {
        if (mouseX >= xPos && mouseY >= yPos && mouseX <= xPos + width && mouseY <= yPos + height) {
            return supplier.get();
        }
        return false;
    }
}