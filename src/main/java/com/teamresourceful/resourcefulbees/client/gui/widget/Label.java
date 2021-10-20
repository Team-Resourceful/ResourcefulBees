package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.jetbrains.annotations.NotNull;

public class Label extends Widget {

    public ITextComponent text;
    private final int color;

    public Label(ITextComponent text, int x, int y, int width, int height, int color, ITextComponent message) {
        super(x, y, width, height, message);
        this.text = text;
        this.color = color;
    }

    public Label(ITextComponent text, int x, int y) {
        this(text, x, y, -1);
    }

    public Label(ITextComponent text, int x, int y, ITextComponent message) {
        this(text, x, y, -1, message);
    }

    public Label(ITextComponent text, int x, int y, int color) {
        this(text, x, y, color, new StringTextComponent(""));
    }

    public Label(ITextComponent text, int x, int y, int color, ITextComponent message) {
        this(text, x, y, Minecraft.getInstance().font.width(text), Minecraft.getInstance().font.lineHeight, color, message);
    }

    public Label(ITextComponent text, int x, int y, int width, int height, int color) {
        this(text, x, y, width, height, color, text);
    }

    public Label(ITextComponent text, int x, int y, int width, int height) {
        this(text, x, y, width, height, -1, text);
    }

    public Label(ITextComponent text, int x, int y, int width, int height, ITextComponent message) {
        this(text, x, y, width, height, -1, message);
    }

    @Override
    public void render(@NotNull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.render(matrix, mouseX, mouseY, partialTicks);
        Minecraft.getInstance().font.draw(matrix, text, this.x, this.y, color);
    }
}
