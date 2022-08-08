package com.teamresourceful.resourcefulbees.client.components.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.utils.RenderUtils;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public abstract class ImageButton extends AbstractButton {

    public ImageButton(int x, int y, int width, int height, Component text) {
        super(x, y, width, height, text);
    }

    @Override
    public void renderButton(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderUtils.bindTexture(getTexture(mouseX, mouseY));
        blit(stack, this.x, this.y, getU(mouseX, mouseY), getV(mouseX, mouseY), this.width, this.height);
    }

    public abstract ResourceLocation getTexture(int mouseX, int mouseY);
    public abstract int getU(int mouseX, int mouseY);
    public abstract int getV(int mouseX, int mouseY);

    @Override
    public void updateNarration(@NotNull NarrationElementOutput narration) {

    }
}
