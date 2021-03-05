package com.resourcefulbees.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

public class ListButton extends TabImageButton {
    private final ITextComponent text;
    private final int textX;
    private final int textY;
    private FontRenderer fontRenderer;
    private ButtonList parent = null;

    public ListButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, @NotNull ItemStack displayItem, int itemX, int itemY, ITextComponent text, int textX, int textY, IPressable onPressIn) {
        super(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, displayItem, itemX, itemY, onPressIn);
        this.text = text;
        this.textX = textX;
        this.textY = textY;
        this.fontRenderer = Minecraft.getInstance().fontRenderer;
    }

    public void setParent(ButtonList parent) {
        this.parent = parent;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (parent != null && (mouseX < parent.xPos || mouseY < parent.yPos || mouseX > parent.xPos + parent.width || mouseY > parent.yPos + parent.height)) {
            return false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void renderButton(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(this.resourceLocation);
        RenderSystem.disableDepthTest();
        float i = this.yTexStart;
        if (!this.active) {
            i += yDiffText * 2;
        } else if (this.isHovered()) {
            i += this.yDiffText;
        }
        if (parent != null) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            double scale = minecraft.getWindow().getGuiScaleFactor();
            int bottom = (int) (minecraft.getWindow().getFramebufferHeight() - (parent.yPos + parent.height) * scale);
            GL11.glScissor((int) (parent.xPos * scale), bottom, (int) (parent.width * scale), (int) (parent.height * scale));
            drawButton(matrix, i);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        } else {
            drawButton(matrix, i);
        }
    }

    private void drawButton(@Nonnull MatrixStack matrix, float texYPos) {
        drawTexture(matrix, this.x, this.y, (float) this.xTexStart, texYPos, this.width, this.height, width, yDiffText * 3);
        int color = this.active ? TextFormatting.GRAY.getColor() : TextFormatting.WHITE.getColor();
        fontRenderer.draw(matrix, text, (float) (this.x + textX), (float) (this.y + textY), color);
        if (this.displayItem != null)
            Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(this.displayItem, this.x + this.itemX, this.y + this.itemY);
        RenderSystem.enableDepthTest();
    }
}