package com.teamresourceful.resourcefulbees.platform.client.renderer.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public interface OverlayRenderer {

    void render(Minecraft minecraft, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight);
}
