package com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge.modules;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;

public interface IDisplayModule {

    default void renderBackground(PoseStack matrix, float partialTicks, int mouseX, int mouseY) {}

    default void renderText(PoseStack stack, int mouseX, int mouseY) {}

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

    default void onTerminalResponse(Component component) {}
}
