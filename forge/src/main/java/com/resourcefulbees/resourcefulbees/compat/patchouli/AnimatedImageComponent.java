package com.resourcefulbees.resourcefulbees.compat.patchouli;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

import java.util.function.UnaryOperator;

public class AnimatedImageComponent implements ICustomComponent {

    IVariable image;

    IVariable ticksPerFrame;
    IVariable texHeight;
    IVariable texWidth;
    IVariable height;
    IVariable width;
    IVariable scale;
    private transient ResourceLocation animatedImage;
    private transient int xOffset;
    private transient int yOffset;
    private transient int currentFrame = 0;
    private transient int textureHeight;
    private transient int textureWidth;
    private transient int imageHeight;
    private transient int imageWidth;
    private transient float imageScale;
    private int frames;


    @Override
    public void build(int x, int y, int page) {
        xOffset = x;
        yOffset = y;
    }

    @Override
    public void render(@NotNull MatrixStack matrixStack, @NotNull IComponentRenderContext context, float v, int i, int i1) {
        if (context.getTicksInBook() % 2 == 0) {
            currentFrame++;
        }
        if (currentFrame >= frames) {
            currentFrame = 0;
        }
        Minecraft.getInstance().getTextureManager().bind(animatedImage);

        AbstractGui.blit(matrixStack, xOffset, yOffset, 0, (float) imageHeight * currentFrame, (int) (imageWidth * imageScale), (int) (imageHeight * imageScale), textureWidth, textureHeight);
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
        image = lookup.apply(image);
        scale = lookup.apply(scale);
        ticksPerFrame = lookup.apply(ticksPerFrame);
        this.animatedImage = new ResourceLocation(image.asString());
        texHeight = lookup.apply(texHeight);
        texWidth = lookup.apply(texWidth);
        textureHeight = texHeight.asNumber().intValue();
        textureWidth = texWidth.asNumber().intValue();
        imageScale = scale.asNumber().floatValue();
        imageHeight = height.asNumber().intValue();
        imageWidth = width.asNumber().intValue();
        this.frames = textureHeight / imageHeight;
    }
}
