package com.teamresourceful.resourcefulbees.client.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public class ItemStackDisplayWidget extends AbstractWidget {

    public static final int SIZE = 16;

    private final ItemStack stack;

    public ItemStackDisplayWidget(ItemStack stack) {
        super(0, 0, SIZE, SIZE, Component.empty());
        this.stack = stack;
    }

    protected ItemStack getStack() {
        return stack;
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        ItemStack itemStack = getStack();

        if (itemStack.isEmpty()) {
            return;
        }

        graphics.item(itemStack, getX(), getY());
        graphics.itemDecorations(Minecraft.getInstance().font, itemStack, getX(), getY());

        if (isHovered()) {
            graphics.setTooltipForNextFrame(Minecraft.getInstance().font, itemStack, mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
        ItemStack itemStack = getStack();

        if (!itemStack.isEmpty()) {
            output.add(NarratedElementType.TITLE, itemStack.getHoverName());
        }
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        return false;
    }
}