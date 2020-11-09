package com.resourcefulbees.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.CentrifugeMultiblockContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;

public class CentrifugeMultiblockScreen extends ContainerScreen<CentrifugeMultiblockContainer> {
    public CentrifugeMultiblockScreen(CentrifugeMultiblockContainer screenContainer, PlayerInventory inventory, ITextComponent titleIn) {
        super(screenContainer, inventory, titleIn);
        this.xSize = 176;
        this.ySize = 228;
    }

    @Override
    protected void drawBackground(@Nonnull MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        Minecraft client = this.client;
        if (client != null) {
            client.getTextureManager().bindTexture(new ResourceLocation(ResourcefulBees.MOD_ID,"textures/gui/centrifuges/multiblock_centrifuge.png"));
            int i = this.guiLeft;
            int j = this.guiTop;
            this.drawTexture(matrix, i, j, 0, 0, this.xSize, this.ySize);
            int scaledProgress1 = 16 * this.container.getTime(0) / this.container.getTotalTime(0);
            this.drawTexture(matrix, i + 52, j + 26, 176, 1, 16, scaledProgress1);
            int scaledProgress2 = 16 * this.container.getTime(1) / this.container.getTotalTime(1);
            this.drawTexture(matrix, i + 88, j + 26, 176, 1, 16, scaledProgress2);
            int scaledProgress3 = 16 * this.container.getTime(2) / this.container.getTotalTime(2);
            this.drawTexture(matrix, i + 124, j + 26, 176, 1, 16, scaledProgress3);
            int scaledRF = 58 * this.container.getEnergy() / (Config.MAX_CENTRIFUGE_RF.get() * 5);
            this.drawTexture(matrix, i + 10, j + 38 + (58-scaledRF), 176, 28 + (58-scaledRF), 12, scaledRF);
        }
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.drawMouseoverTooltip(matrix, mouseX, mouseY);
        DecimalFormat decimalFormat = new DecimalFormat("##0.0");
        if (mouseX >= this.guiLeft + 10 && mouseX <= this.guiLeft + 22 && mouseY >= this.guiTop + 38 && mouseY <= this.guiTop + 96){
            if (Screen.hasShiftDown() || this.container.getEnergy() < 500) this.renderTooltip(matrix, new StringTextComponent(this.container.getEnergy() + " RF"), mouseX, mouseY);
            else this.renderTooltip(matrix, new StringTextComponent(decimalFormat.format((double)this.container.getEnergy() / 1000) + " kRF"), mouseX, mouseY);
        }
    }

    @Override
    public void drawForeground(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
    }
}
