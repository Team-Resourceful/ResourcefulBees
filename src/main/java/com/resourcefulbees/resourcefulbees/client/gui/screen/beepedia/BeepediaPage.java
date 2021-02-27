package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.client.gui.widget.TabImageButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

public abstract class BeepediaPage {

    public final int yPos;
    public final int xPos;
    public final BeepediaScreen beepedia;

    public ListButton listButton = null;

    public final ResourceLocation arrowImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/arrows.png");
    public final ResourceLocation listImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/list_button.png");
    public final String id;

    protected BeepediaPage(BeepediaScreen beepedia, int xPos, int yPos, String id) {
        this.beepedia = beepedia;
        this.yPos = yPos;
        this.xPos = xPos;
        this.id = id;
    }

    public void updateListPosition(int xPos, int yPos) {
        if (listButton == null) return;
        listButton.x = xPos;
        listButton.y = yPos;
    }

    public void newListButton(ItemStack item, TextComponent text) {
        listButton = new ListButton(0, 0, 100, 20, 0, 0, 20, listImage, item, 2, 2, text, 22, 6, onPress -> beepedia.setActive(this));
    }

    public void openPage() {
        if (listButton == null) return;
        listButton.active = false;
    }

    public void closePage() {
        if (listButton == null) return;
        listButton.active = true;
    }

    public abstract void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY);

    public void renderForeground(MatrixStack matrix, int mouseX, int mouseY) {
        // override to implement
    }

    public abstract String getSearch();

    public void tick(int ticksActive) {
        // override to implement
    }

    public void drawTooltips(MatrixStack matrixStack, int mouseX, int mouseY) {
        // override to implement
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        // override to implement
        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        // override to implement
        return false;
    }

    public class ListButton extends TabImageButton {
        private final TextComponent text;
        private final int textX;
        private final int textY;
        private FontRenderer fontRenderer;
        private BeepediaScreen.ButtonList parent = null;

        public ListButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, @NotNull ItemStack displayItem, int itemX, int itemY, TextComponent text, int textX, int textY, IPressable onPressIn) {
            super(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, displayItem, itemX, itemY, onPressIn);
            this.text = text;
            this.textX = textX;
            this.textY = textY;
            this.fontRenderer = Minecraft.getInstance().fontRenderer;
        }

        public void setParent(BeepediaScreen.ButtonList parent) {
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
}
