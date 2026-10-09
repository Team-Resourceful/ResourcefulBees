package com.teamresourceful.resourcefulbees.client.screen.beecon;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

public class BeeconScrollbarWidget extends AbstractWidget {

    private static final int TEXTURE_SIZE = 256;

    private final Identifier texture;
    private final int spriteU;
    private final int spriteV;
    private final int thumbHeight;

    private final DoubleSupplier scrollSupplier;
    private final DoubleSupplier maxScrollSupplier;
    private final DoubleConsumer scrollConsumer;

    private boolean dragging = false;
    private double dragOffset = 0.0;

    public BeeconScrollbarWidget(int x, int y, int width, int trackHeight, int thumbHeight, int spriteU, int spriteV, Identifier texture, DoubleSupplier scrollSupplier, DoubleSupplier maxScrollSupplier, DoubleConsumer scrollConsumer) {
        super(x, y, width, trackHeight, Component.empty());

        this.thumbHeight = thumbHeight;
        this.spriteU = spriteU;
        this.spriteV = spriteV;
        this.texture = texture;

        this.scrollSupplier = scrollSupplier;
        this.maxScrollSupplier = maxScrollSupplier;
        this.scrollConsumer = scrollConsumer;
    }

    private int getThumbY() {
        double maxScroll = maxScrollSupplier.getAsDouble();
        int travel = getHeight() - thumbHeight;

        if (maxScroll <= 0.0 || travel <= 0) {
            return getY();
        }

        double fraction = Math.clamp(
                scrollSupplier.getAsDouble() / maxScroll,
                0.0,
                1.0
        );

        return getY() + (int) Math.round(fraction * travel);
    }

    private void scrollToMouse(double mouseY) {
        int travel = getHeight() - thumbHeight;
        double maxScroll = maxScrollSupplier.getAsDouble();

        if (travel <= 0 || maxScroll <= 0.0) {
            return;
        }

        double thumbTop = mouseY - dragOffset;

        double fraction = Math.clamp(
                (thumbTop - getY()) / travel,
                0.0,
                1.0
        );

        scrollConsumer.accept(fraction * maxScroll);
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (!visible || maxScrollSupplier.getAsDouble() <= 0.0) {
            return;
        }
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, TEXTURE_SIZE, TEXTURE_SIZE, spriteU, spriteV, getX(), getThumbY(), getWidth(), thumbHeight);
    }

    @Override
    public void playDownSound(@NonNull SoundManager soundManager) {
        // quiet you
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        int thumbY = getThumbY();

        dragging = true;

        if (event.y() >= thumbY && event.y() < thumbY + thumbHeight) {
            dragOffset = event.y() - thumbY;
        } else {
            dragOffset = thumbHeight / 2.0;
            scrollToMouse(event.y());
        }
    }

    @Override
    protected void onDrag(@NonNull MouseButtonEvent event, double dragX, double dragY) {
        if (dragging) {
            scrollToMouse(event.y());
        }
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        if (dragging) {
            dragging = false;
            return true;
        }

        return false;
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}