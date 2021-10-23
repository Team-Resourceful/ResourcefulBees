package com.teamresourceful.resourcefulbees.client.gui.widget;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ButtonTemplate {

    public final int width;
    public final int height;
    public final int xTexStart;
    public final int yTexStart;
    public final int yTexDiff;
    public final int textureWidth;
    public final int textureHeight;
    public final ResourceLocation resourceLocation;

    public ButtonTemplate(int width, int height, int xTexStart, int yTexStart, int yTexDiff, int textureWidth, int textureHeight, ResourceLocation resourceLocation) {
        this.width = width;
        this.height = height;
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.yTexDiff = yTexDiff;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.resourceLocation = resourceLocation;
    }
}
