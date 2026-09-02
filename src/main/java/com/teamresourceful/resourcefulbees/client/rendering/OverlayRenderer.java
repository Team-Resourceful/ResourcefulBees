package com.teamresourceful.resourcefulbees.client.rendering;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface OverlayRenderer {

    void render(GuiGraphicsExtractor graphics, DeltaTracker partialTick);
}
