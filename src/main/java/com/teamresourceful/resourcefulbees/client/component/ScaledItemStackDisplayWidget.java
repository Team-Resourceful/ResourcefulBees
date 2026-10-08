package com.teamresourceful.resourcefulbees.client.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public final class ScaledItemStackDisplayWidget extends AbstractWidget {

    public static final int SIZE = 32;
    private static final float SCALE = 2.0f;

    private final ItemStack stack;

    public ScaledItemStackDisplayWidget(ItemStack stack) {
        super(0, 0, SIZE, SIZE, Component.empty());
        this.stack = stack;
    }

    @Override
    protected void extractWidgetRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (stack.isEmpty()) {
            return;
        }

        graphics.pose().pushMatrix();
        graphics.pose().translate(getX(), getY());
        graphics.pose().scale(SCALE, SCALE);

        graphics.item(stack, 0, 0);
        graphics.itemDecorations(Minecraft.getInstance().font, stack, 0, 0);

        graphics.pose().popMatrix();

        if (isHovered()) {
            graphics.setTooltipForNextFrame(Minecraft.getInstance().font, stack, mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        if (!stack.isEmpty()) {
            output.add(NarratedElementType.TITLE, stack.getHoverName());
        }
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        return false;
    }
}