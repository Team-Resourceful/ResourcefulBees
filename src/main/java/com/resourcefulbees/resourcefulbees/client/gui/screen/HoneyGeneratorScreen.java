package com.resourcefulbees.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.container.HoneyGeneratorContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;

public class HoneyGeneratorScreen extends ContainerScreen<HoneyGeneratorContainer> {
    public HoneyGeneratorScreen(HoneyGeneratorContainer screenContainer, PlayerInventory inventory, ITextComponent titleIn) {
        super(screenContainer, inventory, titleIn);
    }

    @Override
    protected void drawBackground(@Nonnull MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        ResourceLocation texture = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/gui/generator/honey_gen.png");
        Minecraft client = this.client;
        if (client != null) {
            client.getTextureManager().bindTexture(texture);
            int i = this.guiLeft;
            int j = this.guiTop;
            this.drawTexture(matrix, i, j, 0, 0, this.xSize, this.ySize);
            int scaledRF = 62 * this.container.honeyGeneratorTileEntity.energyStorage.getEnergyStored() / Math.max(this.container.honeyGeneratorTileEntity.energyStorage.getMaxEnergyStored(),1);
            this.drawTexture(matrix, i + 130, j + 12 + (62-scaledRF), 215, (62-scaledRF), 11, scaledRF);
            int scaledTank = 62 * this.container.honeyGeneratorTileEntity.fluidTank.getFluidAmount() / Math.max(this.container.honeyGeneratorTileEntity.fluidTank.getCapacity(),1);
            this.drawTexture(matrix, i + 83, j + 12 + (62-scaledTank), 226, (62-scaledTank), 14, scaledTank);
            int scaledProgressX = 21 * this.container.honeyGeneratorTileEntity.time / Math.max(this.container.honeyGeneratorTileEntity.totalTime,1);
            int scaledProgressY = 20 * this.container.honeyGeneratorTileEntity.time / Math.max(this.container.honeyGeneratorTileEntity.totalTime,1);
            int energyScaledProgressX = 21 * this.container.honeyGeneratorTileEntity.energyTime / Math.max(this.container.honeyGeneratorTileEntity.energyTotalTime,1);
            this.drawTexture(matrix, i + 35, j + 37, 176, 0, 18, scaledProgressY);
            this.drawTexture(matrix, i + 57, j + 42, 194, 0, scaledProgressX, 10);
            this.drawTexture(matrix, i + 103, j + 42, 194, 0, energyScaledProgressX, 10);
        }
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.drawMouseoverTooltip(matrix, mouseX, mouseY);
        DecimalFormat decimalFormat = new DecimalFormat("##0.0");
        if (mouseX >= this.guiLeft + 83 && mouseX <= this.guiLeft + 97 && mouseY >= this.guiTop + 12 && mouseY <= this.guiTop + 74){
            if (Screen.hasShiftDown() || this.container.honeyGeneratorTileEntity.fluidTank.getFluidAmount() < 500) this.renderTooltip(matrix, new StringTextComponent(this.container.honeyGeneratorTileEntity.fluidTank.getFluidAmount() + " MB"), mouseX, mouseY);
            else this.renderTooltip(matrix, new StringTextComponent(decimalFormat.format((double)this.container.honeyGeneratorTileEntity.fluidTank.getFluidAmount() / 1000) + " Buckets"), mouseX, mouseY);
        }
        if (mouseX >= this.guiLeft + 130 && mouseX <= this.guiLeft + 141 && mouseY >= this.guiTop + 12 && mouseY <= this.guiTop + 74){
            if (Screen.hasShiftDown() || this.container.honeyGeneratorTileEntity.energyStorage.getEnergyStored() < 500) this.renderTooltip(matrix, new StringTextComponent(this.container.honeyGeneratorTileEntity.energyStorage.getEnergyStored() + " RF"), mouseX, mouseY);
            else this.renderTooltip(matrix, new StringTextComponent(decimalFormat.format((double)this.container.honeyGeneratorTileEntity.energyStorage.getEnergyStored() / 1000) + " kRF"), mouseX, mouseY);
        }
    }

    @Override
    protected void drawForeground(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        this.textRenderer.draw(matrix, this.title.getString(), 8, 5, 0x404040);
    }
}
