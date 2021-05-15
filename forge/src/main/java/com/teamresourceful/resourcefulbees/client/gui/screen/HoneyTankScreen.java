package com.teamresourceful.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.container.HoneyTankContainer;
import com.teamresourceful.resourcefulbees.tileentity.HoneyTankTileEntity;
import com.teamresourceful.resourcefulbees.utils.MathUtils;
import com.teamresourceful.resourcefulbees.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;


public class HoneyTankScreen extends AbstractContainerScreen<HoneyTankContainer> {

    final HoneyTankTileEntity tileEntity;

    public HoneyTankScreen(HoneyTankContainer container, Inventory inventory, Component displayName) {
        super(container, inventory, displayName);
        tileEntity = container.getHoneyTankTileEntity();
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrix, float partialTicks, int mouseX, int mouseY) {
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

    private void renderProgressBar(PoseStack matrix) {
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
    public void render(@NotNull PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (this.tileEntity != null) {
            this.renderBackground(matrix);
            super.render(matrix, mouseX, mouseY, partialTicks);
            this.renderProgressBar(matrix);
            this.renderTooltip(matrix, mouseX, mouseY);
            DecimalFormat decimalFormat = new DecimalFormat("##0.0");
            if (tileEntity != null && MathUtils.inRangeInclusive(mouseX, this.leftPos + 81, this.leftPos + 93) && MathUtils.inRangeInclusive(mouseY, this.topPos + 12, this.topPos + 74)) {
                if (Screen.hasShiftDown() || tileEntity.getFluidTank().getFluidAmount() < 500) {
                    this.renderTooltip(matrix, new TextComponent(tileEntity.getFluidTank().getFluidAmount() + " MB"), mouseX, mouseY);
                } else {
                    this.renderTooltip(matrix, new TextComponent(decimalFormat.format((double) tileEntity.getFluidTank().getFluidAmount() / 1000) + " Buckets"), mouseX, mouseY);
                }
            }
        }
    }
}
