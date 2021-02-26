package com.resourcefulbees.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

import net.minecraft.client.gui.widget.button.Button.IPressable;

public class TabImageButton extends ImageButton {

    protected final ResourceLocation resourceLocation;
    protected final int xTexStart;
    protected final int yTexStart;
    protected final int yDiffText;
    protected final ItemStack displayItem;
    protected final int itemX;
    protected final int itemY;

    public TabImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, @Nonnull ItemStack displayItem, int itemX, int itemY, IPressable onPressIn) {
        super(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, onPressIn);
        this.xTexStart = xTexStartIn;
        this.yTexStart = yTexStartIn;
        this.yDiffText = yDiffTextIn;
        this.resourceLocation = resourceLocationIn;
        this.displayItem = displayItem;
        this.itemX = itemX;
        this.itemY = itemY;
    }

    @Override
    public void renderButton(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(this.resourceLocation);
        RenderSystem.disableDepthTest();
        int i = this.yTexStart;
        if (!this.active) {
            i += 36;
        } else if (this.isHovered()) {
            i += this.yDiffText;
        }
        drawTexture(matrix, this.x, this.y, (float) this.xTexStart, (float) i, this.width, this.height, 128, 128);
        if (this.displayItem != null)
            Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(this.displayItem, this.x + this.itemX, this.y + this.itemY);
        RenderSystem.enableDepthTest();
    }


}
