package com.resourcefulbees.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.CentrifugeContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;

public class CentrifugeScreen extends ContainerScreen<CentrifugeContainer> {
    public CentrifugeScreen(CentrifugeContainer screenContainer, PlayerInventory inventory, ITextComponent titleIn) {
        super(screenContainer, inventory, titleIn);
    }
    int textColor = 0x404040;
    public static String currentMonth;

    @Override
    protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        ResourceLocation texture;
        if (currentMonth.equals("12")) texture = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/gui/centrifuges/christmas_centrifuge.png");
        else texture = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/gui/centrifuges/centrifuge.png");
        Minecraft client = this.minecraft;
        if (client != null) {
            client.getTextureManager().bindTexture(texture);
            int i = this.guiLeft;
            int j = this.guiTop;
            this.blit(matrix, i, j, 0, 0, this.xSize, this.ySize);
            int scaledprogress = 74 * this.container.centrifugeTileEntity.time / Math.max(this.container.centrifugeTileEntity.totalTime,1);
            this.blit(matrix, i + 51, j + 28, 176, 0, scaledprogress, 28);
            int scaledRF = 52 * this.container.getEnergy() / Math.max(Config.MAX_CENTRIFUGE_RF.get(),1);
            this.blit(matrix, i + 8, j + 8 + (52-scaledRF), 176, 28 + (52-scaledRF), 11, scaledRF);
        }
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.func_230459_a_(matrix, mouseX, mouseY);
        DecimalFormat decimalFormat = new DecimalFormat("##0.0");
        if (mouseX >= this.guiLeft + 7 && mouseX <= this.guiLeft + 20 && mouseY >= this.guiTop + 7 && mouseY <= this.guiTop + 59){
            if (Screen.hasShiftDown() || this.container.getEnergy() < 500) this.renderTooltip(matrix, new StringTextComponent(this.container.getEnergy() + " RF"), mouseX, mouseY);
            else this.renderTooltip(matrix, new StringTextComponent(decimalFormat.format((double)this.container.getEnergy() / 1000) + " kRF"), mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        this.font.drawString(matrix, this.title.getString(), 25, 5, textColor);
    }
}
