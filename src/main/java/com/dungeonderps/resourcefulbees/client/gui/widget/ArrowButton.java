package com.dungeonderps.resourcefulbees.client.gui.widget;

import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;

public class ArrowButton extends ImageButton {

    public ArrowButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, IPressable onPressIn) {
        super(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, onPressIn);
    }
}