package com.dungeonderps.resourcefulbees.client.gui.screen;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.container.CentrifugeContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

public class CentrifugeScreen extends ContainerScreen<CentrifugeContainer> {
    public CentrifugeScreen(CentrifugeContainer screenContainer, PlayerInventory inventory, ITextComponent titleIn) {
        super(screenContainer, inventory, titleIn);
    }
    int textColor = 0x404040;
    public static String currentMonth;

    /**
     * Draws the background layer of this container (behind the items).
     *
     * @param partialTicks partial ticks
     * @param mouseX mouse x
     * @param mouseY mouse y
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        ResourceLocation texture;
        if (currentMonth.equals("12")) texture = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/gui/centrifuges/christmas_centrifuge.png");
        else texture = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/gui/centrifuges/centrifuge.png");
        Minecraft client = this.minecraft;
        if (client != null) {
            this.minecraft.getTextureManager().bindTexture(texture);
            int i = (this.width - this.xSize) / 2;
            int j = (this.height - this.ySize) / 2;
            this.blit(matrix, i, j, 0, 0, this.xSize, this.ySize);
            double scaledprogress = 74d * this.container.centrifugeTileEntity.time /
                    Math.max(this.container.centrifugeTileEntity.totalTime,1d);
            this.blit(matrix, i + 51, j + 28, 176, 0, (int)scaledprogress, 29);

        }
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.func_230459_a_(matrix, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        this.font.drawString(matrix, this.title.getString(), 5, 5, textColor);
    }
}
