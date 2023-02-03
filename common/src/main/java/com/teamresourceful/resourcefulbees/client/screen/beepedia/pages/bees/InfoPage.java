package com.teamresourceful.resourcefulbees.client.screen.beepedia.pages.bees;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import org.jetbrains.annotations.NotNull;

public class InfoPage extends Screen {

    public InfoPage() {
        super(CommonComponents.EMPTY);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Minecraft.getInstance().font.drawShadow(poseStack, "Join our Discord to leave", 10, 10, 0xffffff);
        Minecraft.getInstance().font.drawShadow(poseStack, "suggestions about what should", 10, 20, 0xffffff);
        Minecraft.getInstance().font.drawShadow(poseStack, "go here!", 10, 30, 0xffffff);
    }
}
