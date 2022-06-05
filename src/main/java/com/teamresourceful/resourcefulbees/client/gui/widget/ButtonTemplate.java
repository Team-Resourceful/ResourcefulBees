package com.teamresourceful.resourcefulbees.client.gui.widget;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record ButtonTemplate(int width, int height, int xTexStart, int yTexStart, int yTexDiff, int textureWidth,
                             int textureHeight, ResourceLocation resourceLocation) {

}
