package com.resourcefulbees.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class TabImageButton extends ImageButton {

    private final ResourceLocation resourceLocation;
    private final int xTexStart;
    private final int yTexStart;
    private final int yDiffText;
    private boolean triggered;

    public TabImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, IPressable onPressIn) {
        super(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, onPressIn);
        this.xTexStart = xTexStartIn;
        this.yTexStart = yTexStartIn;
        this.yDiffText = yDiffTextIn;
        this.resourceLocation = resourceLocationIn;
    }

    @Override
    public void renderButton(@Nonnull MatrixStack matrix, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(this.resourceLocation);
        RenderSystem.disableDepthTest();
        int i = this.yTexStart;
        if (!this.active) {
            i += 36;
        } else if (this.isHovered()) {
            i += this.yDiffText;
        }
        blit(matrix, this.x, this.y, (float) this.xTexStart, (float) i, this.width, this.height, 128, 128);
        RenderSystem.enableDepthTest();
    }
}
