package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ScrollPane extends Pane {

    private final int startHeight;
    private int minY;
    private int maxY;
    private int scrollOffset;

    public ScrollPane(int xPos, int yPos, int viewWidth, int viewHeight, int startHeight, ITextComponent message) {
        super(xPos, yPos, viewWidth, viewHeight, message);
        scrollOffset = 0;
        this.startHeight = startHeight;
        this.minY = 0;
        this.maxY = startHeight;
        this.toggleScissor();
    }

    public ScrollPane(int xPos, int yPos, int viewWidth, int viewHeight) {
        this(xPos, yPos, viewWidth, viewHeight, 0, new StringTextComponent(""));
    }

    public ScrollPane(int xPos, int yPos, int viewWidth, int viewHeight, ITextComponent message) {
        this(xPos, yPos, viewWidth, viewHeight, 0, message);
    }

    public ScrollPane(int xPos, int yPos, int viewWidth, int viewHeight, int startHeight) {
        this(xPos, yPos, viewWidth, viewHeight, startHeight, new StringTextComponent(""));
    }

    @Override
    public void modifyPane(MatrixStack matrix) {
        matrix.translate(x, y + scrollOffset, 0);
    }

    @Override
    public void add(Widget widget) {
        super.add(widget);
        if (widget.y < minY) minY = widget.y;
        else if (widget.y + widget.getHeight() > maxY) maxY = widget.y + widget.getHeight();
    }

    @Override
    public void reset() {
        super.reset();
        scrollOffset = 0;
        maxY = 0;
        minY = startHeight;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        int yStart = scrollOffset;
        scrollOffset += scrollAmount;
        if (scrollOffset > maxY - height) {
            scrollOffset = maxY - height;
        }
        if (scrollOffset < minY) {
            scrollOffset = minY;
        }
        return scrollOffset != yStart;
    }
}
