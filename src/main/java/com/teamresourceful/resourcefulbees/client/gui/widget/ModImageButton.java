package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.jetbrains.annotations.NotNull;

public class ModImageButton extends Button {

    protected final ResourceLocation resourceLocation;
    protected final int xTexStart;
    protected final int yTexStart;
    protected final int yDiffText;
    protected final int imageWidth;
    protected final int imageHeight;

    public ModImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, int imageWidth, int imageHeight, IPressable onPressIn, ITextComponent message) {
        super(xIn, yIn, widthIn, heightIn, message, onPressIn);
        this.resourceLocation = resourceLocationIn;
        this.xTexStart = xTexStartIn;
        this.yTexStart = yTexStartIn;
        this.yDiffText = yDiffTextIn;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public ModImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, int imageWidth, int imageHeight, IPressable onPressIn) {
        this(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, imageWidth, imageHeight, onPressIn, StringTextComponent.EMPTY);
    }

    public ModImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, IPressable onPressIn, ITextComponent message) {
        this(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, widthIn, yDiffTextIn * 3,  onPressIn, message);
    }

    public ModImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, IPressable onPressIn) {
        this(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, onPressIn, StringTextComponent.EMPTY);
    }

    @Override
    public void renderButton(@NotNull MatrixStack matrix, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bind(this.resourceLocation);
        RenderSystem.disableDepthTest();
        int i = this.yTexStart;
        if (!this.active) {
            i += yDiffText * 2;
        } else if (this.isHovered()) {
            i += this.yDiffText;
        }
        blit(matrix, this.x, this.y, this.xTexStart, i, this.width, this.height, imageWidth, imageHeight);
        RenderSystem.enableDepthTest();
    }
}
