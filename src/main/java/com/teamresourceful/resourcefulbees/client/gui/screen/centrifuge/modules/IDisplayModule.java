package com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge.modules;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.ITextComponent;

public interface IDisplayModule {

    default void renderBackground(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {}

    default void renderText(MatrixStack stack, int mouseX, int mouseY) {}

    default boolean onCharTyped(char typedChar, int modifiers) {
        return false;
    }

    default boolean onKeyPressed(int key, int scan, int modifiers) {
        return false;
    }


    default boolean onMouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    default boolean onMouseScrolled(double mouseX, double mouseY, double delta) {
        return false;
    }

    default void onTerminalResponse(ITextComponent component) {}
}
