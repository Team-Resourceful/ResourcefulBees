package com.resourcefulbees.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.container.HoneyBottlerContainer;
import com.resourcefulbees.resourcefulbees.tileentity.HoneyBottlerTileEntity;
import com.resourcefulbees.resourcefulbees.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import org.jetbrains.annotations.NotNull;
import java.text.DecimalFormat;


public class HoneyBottlerScreen extends ContainerScreen<HoneyBottlerContainer> {

    HoneyBottlerTileEntity tileEntity;

    public HoneyBottlerScreen(HoneyBottlerContainer container, PlayerInventory inventory, ITextComponent displayName) {
        super(container, inventory, displayName);
        tileEntity = container.getHoneyBottlerTileEntity();
    }

    @Override
    protected void renderBg(@NotNull MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        ResourceLocation texture = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/bottler/honey_bottler.png");
        Minecraft client = this.minecraft;
        if (client != null) {
            client.getTextureManager().bind(texture);
            int i = this.leftPos;
            int j = this.topPos;
            this.blit(matrix, i, j, 0, 0, this.imageWidth, this.imageHeight);
            RenderUtils.renderFluid(matrix, tileEntity.getFluidTank(), i + 67, j + 12, 14, 62, getBlitOffset());
        }
    }

    @Override
    public void render(@NotNull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
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
