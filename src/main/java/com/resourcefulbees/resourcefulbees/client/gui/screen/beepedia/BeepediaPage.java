package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.client.gui.widget.TabImageButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public abstract class BeepediaPage {

    public BeepediaScreen.Page pageType;
    public BeepediaScreen beepedia;

    public ListButton listButton = null;

    public ResourceLocation listImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/list_button.png");

    public BeepediaPage(BeepediaScreen beepedia, BeepediaScreen.Page pageType) {
        this.pageType = pageType;
        this.beepedia = beepedia;
    }

    public void updateListPosition(int xPos, int yPos) {
        if (listButton == null) return;
        listButton.x = xPos;
        listButton.y = yPos;
    }

    public abstract void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY);

    public abstract void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY);

    public abstract String getTranslation();

    public class ListButton extends TabImageButton {
        private final TextComponent text;
        private final int textX;
        private final int textY;
        private FontRenderer fontRenderer;

        public ListButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, @NotNull ItemStack displayItem, int itemX, int itemY, TextComponent text, int textX, int textY, IPressable onPressIn) {
            super(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, displayItem, itemX, itemY, onPressIn);
            this.text = text;
            this.textX = textX;
            this.textY = textY;
            this.fontRenderer = Minecraft.getInstance().fontRenderer;
        }

        @Override
        public void renderButton(@Nonnull MatrixStack matrix, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.getTextureManager().bindTexture(this.resourceLocation);
            RenderSystem.disableDepthTest();
            int i = this.yTexStart;
            if (!this.active) {
                i += yDiffText * 2;
            } else if (this.isHovered()) {
                i += this.yDiffText;
            }

            drawTexture(matrix, this.x, this.y, (float) this.xTexStart, (float) i, this.width, this.height, 128, 128);
            fontRenderer.draw(matrix, fontRenderer.trimToWidth(text, 80).getString(), this.x + textX, this.y + textY, Color.parse("white").getRgb());

            if (this.displayItem != null)
                Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(this.displayItem, this.x + this.itemX, this.y + this.itemY);
            RenderSystem.enableDepthTest();
        }
    }
}
