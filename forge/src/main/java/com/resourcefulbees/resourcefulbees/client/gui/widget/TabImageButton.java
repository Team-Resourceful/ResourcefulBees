package com.resourcefulbees.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class TabImageButton extends ModImageButton {

    protected final OnTooltip tooltipProvider;
    protected final ItemStack displayItem;
    protected final int itemX;
    protected final int itemY;

    public TabImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, @NotNull ItemStack displayItem, int itemX, int itemY, OnPress onPressIn, int textureWidth, int textureHeight) {
        super(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, textureHeight, textureWidth, onPressIn);
        this.displayItem = displayItem;
        this.itemX = itemX;
        this.itemY = itemY;
        this.tooltipProvider = null;
    }

    public TabImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, @NotNull ItemStack displayItem, int itemX, int itemY, OnPress onPressIn) {
        this(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, displayItem, itemX, itemY, onPressIn, widthIn, yDiffTextIn * 3);
    }

    public TabImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, @NotNull ItemStack displayItem, int itemX, int itemY, OnPress onPressIn, OnTooltip tooltipProvider) {
        super(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, onPressIn);
        this.displayItem = displayItem;
        this.itemX = itemX;
        this.itemY = itemY;
        this.tooltipProvider = tooltipProvider;
    }

    @Override
    public void renderButton(@Nonnull PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(matrix, mouseX, mouseY, partialTicks);
        if (this.displayItem != null)
            Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(this.displayItem, this.x + this.itemX, this.y + this.itemY);
        RenderSystem.enableDepthTest();
    }

    @Override
    public void renderToolTip(@NotNull PoseStack matrix, int mouseX, int mouseY) {
        if (this.isHovered() && tooltipProvider != null) {
            tooltipProvider.onTooltip(this, matrix, mouseX, mouseY);
        }
    }

}
