package com.resourcefulbees.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.container.HoneyTankContainer;
import com.resourcefulbees.resourcefulbees.tileentity.HoneyTankTileEntity;
import com.resourcefulbees.resourcefulbees.utils.MathUtils;
import com.resourcefulbees.resourcefulbees.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;


public class HoneyTankScreen extends ContainerScreen<HoneyTankContainer> {

    HoneyTankTileEntity tileEntity;

    public HoneyTankScreen(HoneyTankContainer container, PlayerInventory inventory, ITextComponent displayName) {
        super(container, inventory, displayName);
        tileEntity = container.getHoneyTankTileEntity();
    }

    @Override
    protected void renderBg(@NotNull MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        ResourceLocation texture = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/honey_tank/honey_tank.png");
        Minecraft client = this.minecraft;
        if (client != null && tileEntity != null) {
            client.getTextureManager().bind(texture);
            int i = this.leftPos;
            int j = this.topPos;
            this.blit(matrix, i, j, 0, 0, this.imageWidth, this.imageHeight);
            RenderUtils.renderFluid(matrix, tileEntity.getFluidTank(), i + 81, j + 12, 14, 62, getBlitOffset());
        }
    }

    private void renderProgressBar(MatrixStack matrix) {
        ResourceLocation texture = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/honey_tank/honey_tank.png");
        Minecraft client = this.minecraft;
        if (client != null) {
            client.getTextureManager().bind(texture);
            int i = this.leftPos;
            int j = this.topPos;
            this.blit(matrix, i + 52, j + 35, 176, 0, 28, (int) (16 * tileEntity.getProcessEmptyPercent()));
            this.blit(matrix, i + 96, j + 35, 176, 16, 28, (int) (16 * tileEntity.getProcessFillPercent()));
        }
    }

    @Override
    public void render(@NotNull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (this.tileEntity != null) {
            this.renderBackground(matrix);
            super.render(matrix, mouseX, mouseY, partialTicks);
            this.renderProgressBar(matrix);
            this.renderTooltip(matrix, mouseX, mouseY);
            DecimalFormat decimalFormat = new DecimalFormat("##0.0");

            if (tileEntity.getFluidTank().isEmpty()) return;
            if (MathUtils.inRangeInclusive(mouseX, this.leftPos + 81, this.leftPos + 93) && MathUtils.inRangeInclusive(mouseY, this.topPos + 12, this.topPos + 74)) {
                List<IReorderingProcessor> tooltip = new LinkedList<>();
                StringTextComponent fluidCount;
                tooltip.add(new TranslationTextComponent(tileEntity.getFluidTank().getFluid().getTranslationKey()).getVisualOrderText());
                if (tileEntity.getFluidTank().getFluidAmount() < 1000f || Screen.hasShiftDown()) {
                    fluidCount = new StringTextComponent(decimalFormat.format(tileEntity.getFluidTank().getFluidAmount()) + " mb");
                } else {
                    fluidCount = new StringTextComponent(decimalFormat.format(tileEntity.getFluidTank().getFluidAmount() / 1000f) + " B");
                }
                tooltip.add(fluidCount.withStyle(TextFormatting.GRAY).getVisualOrderText());
                renderTooltip(matrix, tooltip, mouseX, mouseY);
            }
        }
    }
}
