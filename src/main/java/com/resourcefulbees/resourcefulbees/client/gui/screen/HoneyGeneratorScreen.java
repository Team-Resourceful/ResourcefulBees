package com.resourcefulbees.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.container.HoneyGeneratorContainer;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.tileentity.HoneyGeneratorTileEntity;
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
    private static final int ENERGY_PER_BOTTLE = (ModConstants.HONEY_PER_BOTTLE/HoneyGeneratorTileEntity.HONEY_DRAIN_AMOUNT)*HoneyGeneratorTileEntity.ENERGY_FILL_AMOUNT;

    public HoneyGeneratorScreen(HoneyGeneratorContainer screenContainer, PlayerInventory inventory, ITextComponent titleIn) {
        super(screenContainer, inventory, titleIn);
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        ResourceLocation texture = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/gui/generator/honey_gen.png");
        Minecraft client = this.minecraft;
        if (client != null) {
            client.getTextureManager().bind(texture);
            int i = this.leftPos;
            int j = this.topPos;
            this.blit(matrix, i, j, 0, 0, this.imageWidth, this.imageHeight);
            int scaledRF = 62 * this.menu.getEnergy() / Math.max(this.menu.getMaxEnergy(),1);
            this.blit(matrix, i + 130, j + 12 + (62-scaledRF), 215, (62-scaledRF), 11, scaledRF);
            int scaledTank = 62 * this.menu.getFluidAmount() / Math.max(this.menu.getMaxFluid(),1);
            this.blit(matrix, i + 83, j + 12 + (62-scaledTank), 226, (62-scaledTank), 14, scaledTank);
            int scaledProgressX = 21 * this.menu.getTime() / Math.max(ModConstants.HONEY_PER_BOTTLE,1);
            int scaledProgressY = 20 * this.menu.getTime() / Math.max(ModConstants.HONEY_PER_BOTTLE,1);
            int energyScaledProgressX = 21 * this.menu.getEnergyTime() / Math.max(ENERGY_PER_BOTTLE,1);
            this.blit(matrix, i + 35, j + 37, 176, 0, 18, scaledProgressY);
            this.blit(matrix, i + 57, j + 42, 194, 0, scaledProgressX, 10);
            this.blit(matrix, i + 103, j + 42, 194, 0, energyScaledProgressX, 10);
        }
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrix, mouseX, mouseY);
        DecimalFormat decimalFormat = new DecimalFormat("##0.0");
        if (mouseX >= this.leftPos + 83 && mouseX <= this.leftPos + 97 && mouseY >= this.topPos + 12 && mouseY <= this.topPos + 74){
            if (Screen.hasShiftDown() || this.menu.getFluidAmount() < 500) this.renderTooltip(matrix, new StringTextComponent(this.menu.getFluidAmount() + " MB"), mouseX, mouseY);
            else this.renderTooltip(matrix, new StringTextComponent(decimalFormat.format((double)this.menu.getFluidAmount() / 1000) + " Buckets"), mouseX, mouseY);
        }
        if (mouseX >= this.leftPos + 130 && mouseX <= this.leftPos + 141 && mouseY >= this.topPos + 12 && mouseY <= this.topPos + 74){
            if (Screen.hasShiftDown() || this.menu.getEnergy() < 500) this.renderTooltip(matrix, new StringTextComponent(this.menu.getEnergy() + " RF"), mouseX, mouseY);
            else this.renderTooltip(matrix, new StringTextComponent(decimalFormat.format((double)this.menu.getEnergy() / 1000) + " kRF"), mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        this.font.draw(matrix, this.title.getString(), 8, 5, 0x404040);
    }
}
