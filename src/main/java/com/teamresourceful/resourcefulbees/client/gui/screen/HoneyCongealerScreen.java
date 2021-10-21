package com.teamresourceful.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.inventory.containers.HoneyCongealerContainer;
import com.teamresourceful.resourcefulbees.common.tileentity.HoneyCongealerTileEntity;
import com.teamresourceful.resourcefulbees.common.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;


public class HoneyCongealerScreen extends ContainerScreen<HoneyCongealerContainer> {

    private final HoneyCongealerTileEntity tileEntity;

    public HoneyCongealerScreen(HoneyCongealerContainer container, PlayerInventory inventory, ITextComponent displayName) {
        super(container, inventory, displayName);
        tileEntity = container.getHoneyCongealerTileEntity();
    }

    @Override
    protected void renderBg(@NotNull MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        ResourceLocation texture = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/congealer/honey_congealer.png");
        Minecraft client = this.minecraft;
        if (client != null && tileEntity != null) {
            client.getTextureManager().bind(texture);
            int i = this.leftPos;
            int j = this.topPos;
            this.blit(matrix, i, j, 0, 0, this.imageWidth, this.imageHeight);
            RenderUtils.renderFluid(matrix, tileEntity.getFluidTank(), i + 67, j + 12, 14, 62, getBlitOffset());
        }
    }

    private void renderProgressBar(MatrixStack matrix) {
        ResourceLocation texture = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/congealer/honey_congealer.png");
        Minecraft client = this.minecraft;
        if (client != null) {
            client.getTextureManager().bind(texture);
            int i = this.leftPos;
            int j = this.topPos;
            this.blit(matrix, i + 84, j + 17, 176, 0, 24, (int) (34 * tileEntity.getProcessPercent()));
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
            if (mouseX >= this.leftPos + 67 && mouseX <= this.leftPos + 81 && mouseY >= this.topPos + 12 && mouseY <= this.topPos + 74) {
                if (Screen.hasShiftDown() || tileEntity.getFluidTank().getFluidAmount() < 500) {
                    this.renderTooltip(matrix, new StringTextComponent(tileEntity.getFluidTank().getFluidAmount() + " MB"), mouseX, mouseY);
                } else {
                    this.renderTooltip(matrix, new StringTextComponent(decimalFormat.format((double) tileEntity.getFluidTank().getFluidAmount() / 1000) + " Buckets"), mouseX, mouseY);
                }
            }
        }
    }

}
