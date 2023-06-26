package com.teamresourceful.resourcefulbees.client.screen.beepedia.pages.bees;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import org.jetbrains.annotations.NotNull;

public class InfoPage extends Screen {

    public InfoPage() {
        super(CommonComponents.EMPTY);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        graphics.drawString(font, "Join our Discord to leave", 10, 10, 0xffffff);
        graphics.drawString(font, "suggestions about what should", 10, 20, 0xffffff);
        graphics.drawString(font, "go here!", 10, 30, 0xffffff);
    }
}
