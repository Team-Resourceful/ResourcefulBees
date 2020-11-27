package com.resourcefulbees.resourcefulbees.client.gui.screen;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.container.HoneyGeneratorContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.text.DecimalFormat;

public class HoneyGeneratorScreen extends ContainerScreen<HoneyGeneratorContainer> {
    public HoneyGeneratorScreen(HoneyGeneratorContainer screenContainer, PlayerInventory inventory, ITextComponent titleIn) {
        super(screenContainer, inventory, titleIn);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        ResourceLocation texture = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/gui/generator/honey_gen.png");
        Minecraft client = this.minecraft;
        if (client != null) {
            client.getTextureManager().bindTexture(texture);
            int i = this.guiLeft;
            int j = this.guiTop;
            this.blit(i, j, 0, 0, this.xSize, this.ySize);
            int scaledRF = 62 * this.container.honeyGeneratorTileEntity.energyStorage.getEnergyStored() / Math.max(this.container.honeyGeneratorTileEntity.energyStorage.getMaxEnergyStored(),1);
            this.blit(i + 130, j + 12 + (62-scaledRF), 215, (62-scaledRF), 11, scaledRF);
            int scaledTank = 62 * this.container.honeyGeneratorTileEntity.fluidTank.getFluidAmount() / Math.max(this.container.honeyGeneratorTileEntity.fluidTank.getCapacity(),1);
            this.blit(i + 83, j + 12 + (62-scaledTank), 226, (62-scaledTank), 14, scaledTank);
            int scaledProgressX = 21 * this.container.honeyGeneratorTileEntity.time / Math.max(this.container.honeyGeneratorTileEntity.totalTime,1);
            int scaledProgressY = 20 * this.container.honeyGeneratorTileEntity.time / Math.max(this.container.honeyGeneratorTileEntity.totalTime,1);
            int energyScaledProgressX = 21 * this.container.honeyGeneratorTileEntity.energyTime / Math.max(this.container.honeyGeneratorTileEntity.energyTotalTime,1);
            this.blit(i + 35, j + 37, 176, 0, 18, scaledProgressY);
            this.blit(i + 57, j + 42, 194, 0, scaledProgressX, 10);
            this.blit(i + 103, j + 42, 194, 0, energyScaledProgressX, 10);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        DecimalFormat decimalFormat = new DecimalFormat("##0.0");
        if (mouseX >= this.guiLeft + 83 && mouseX <= this.guiLeft + 97 && mouseY >= this.guiTop + 12 && mouseY <= this.guiTop + 74){
            if (Screen.hasShiftDown() || this.container.honeyGeneratorTileEntity.fluidTank.getFluidAmount() < 500) this.renderTooltip(this.container.honeyGeneratorTileEntity.fluidTank.getFluidAmount() + " MB", mouseX, mouseY);
            else this.renderTooltip(decimalFormat.format((double)this.container.honeyGeneratorTileEntity.fluidTank.getFluidAmount() / 1000) + " Buckets", mouseX, mouseY);
        }
        if (mouseX >= this.guiLeft + 130 && mouseX <= this.guiLeft + 141 && mouseY >= this.guiTop + 12 && mouseY <= this.guiTop + 74){
            if (Screen.hasShiftDown() || this.container.honeyGeneratorTileEntity.energyStorage.getEnergyStored() < 500) this.renderTooltip(this.container.honeyGeneratorTileEntity.energyStorage.getEnergyStored() + " RF", mouseX, mouseY);
            else this.renderTooltip(decimalFormat.format((double)this.container.honeyGeneratorTileEntity.energyStorage.getEnergyStored() / 1000) + " kRF", mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(this.title.getString(), 8, 5, 0x404040);
    }
}
