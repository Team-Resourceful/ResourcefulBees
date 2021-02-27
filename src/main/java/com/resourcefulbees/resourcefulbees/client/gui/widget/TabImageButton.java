package com.resourcefulbees.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class TabImageButton extends ImageButton {

    protected final ITooltip tooltipProvider;
    protected final ItemStack displayItem;
    protected final int itemX;
    protected final int itemY;

    public TabImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, @NotNull ItemStack displayItem, int itemX, int itemY, IPressable onPressIn) {
        super(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, onPressIn);
        this.displayItem = displayItem;
        this.itemX = itemX;
        this.itemY = itemY;
        this.tooltipProvider = null;
    }

    public TabImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, @NotNull ItemStack displayItem, int itemX, int itemY, IPressable onPressIn, ITooltip tooltipProvider) {
        super(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, onPressIn);
        this.displayItem = displayItem;
        this.itemX = itemX;
        this.itemY = itemY;
        this.tooltipProvider = tooltipProvider;
    }

    @Override
    public void renderButton(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(matrix, mouseX, mouseY, partialTicks);
        if (this.displayItem != null)
            Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(this.displayItem, this.x + this.itemX, this.y + this.itemY);
        RenderSystem.enableDepthTest();
    }

    @Override
    public void renderToolTip(@NotNull MatrixStack matrix, int mouseX, int mouseY) {
        if (this.isHovered() && tooltipProvider != null) {
            tooltipProvider.onTooltip(this, matrix, mouseX, mouseY);
        }
    }

}
