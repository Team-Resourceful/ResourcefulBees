package com.teamresourceful.resourcefulbees.client.screen.beepedia.component.honey;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public final class HoneyItemsWidget extends AbstractWidget {

    private static final int HEIGHT = 34;
    private static final int ITEM_GAP = 12;

    private final ItemStack bottle;
    private final ItemStack bucket;
    private final ItemStack block;

    public HoneyItemsWidget(ItemStack bottle, ItemStack bucket, ItemStack block, int width) {
        super(0, 0, width, HEIGHT, Component.empty());

        this.bottle = bottle;
        this.bucket = bucket;
        this.block = block;
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = getX();

        drawItem(graphics, bottle, Component.literal("Bottle"), x, mouseX, mouseY);

        x += 50 + ITEM_GAP;

        drawItem(graphics, bucket, Component.literal("Bucket"), x, mouseX, mouseY);

        x += 50 + ITEM_GAP;

        drawItem(graphics, block, Component.literal("Block"), x, mouseX, mouseY);
    }

    private void drawItem(GuiGraphicsExtractor graphics, ItemStack stack, Component label, int x, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;

        graphics.item(stack, x, getY());
        graphics.itemDecorations(font, stack, x, getY());

        graphics.text(font, label, x, getY() + 20, 0xFFFFFFFF);

        if (mouseX >= x && mouseX < x + 16 && mouseY >= getY() && mouseY < getY() + 16) {
            graphics.setTooltipForNextFrame(font, stack, mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
        // pipe down
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        return false;
    }
}