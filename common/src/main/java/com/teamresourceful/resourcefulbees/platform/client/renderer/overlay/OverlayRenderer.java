package com.teamresourceful.resourcefulbees.platform.client.renderer.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;

public interface OverlayRenderer {

    void render(Minecraft minecraft, PoseStack stack, float partialTicks, int screenWidth, int screenHeight);
}
