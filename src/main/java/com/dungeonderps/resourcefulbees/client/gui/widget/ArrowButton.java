package com.dungeonderps.resourcefulbees.client.gui.widget;

import net.minecraft.client.gui.widget.button.Button;

public class ArrowButton extends Button {

    private final Enum<ArrowDirection> DIRECTION;

    public enum DIRECTION{};

    public ArrowButton(int widthIn, int heightIn, int width, int height, Enum<ArrowDirection> direction, IPressable onPress) {
        super(widthIn, heightIn, width, height, "", onPress);
        DIRECTION = direction;
    }

    public enum ArrowDirection {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
}
