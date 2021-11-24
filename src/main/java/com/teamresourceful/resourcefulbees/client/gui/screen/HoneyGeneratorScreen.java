package com.teamresourceful.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.capabilities.HoneyFluidTank;
import com.teamresourceful.resourcefulbees.common.inventory.containers.HoneyGeneratorContainer;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import com.teamresourceful.resourcefulbees.common.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

@OnlyIn(Dist.CLIENT)
public class HoneyGeneratorScreen extends ContainerScreen<HoneyGeneratorContainer> {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/gui/generator/honey_gen.png");

    public HoneyGeneratorScreen(HoneyGeneratorContainer screenContainer, PlayerInventory inventory, ITextComponent titleIn) {
        super(screenContainer, inventory, titleIn);
    }

    @Override
    protected void renderBg(@NotNull MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        Minecraft client = this.minecraft;
        if (client != null) {
            client.getTextureManager().bind(BACKGROUND);
            int i = this.leftPos;
            int j = this.topPos;
            this.blit(matrix, i, j, 0, 0, this.imageWidth, this.imageHeight);
            renderEnergy(matrix, i+136, j+16, this.menu.getEnergy().getPercentage());

            HoneyFluidTank tank = menu.getHoneyGeneratorTileEntity().getTank();
            FluidStack fluidStack = tank.getFluid();

            int height = (int)(54*((float)tank.getFluidAmount() / tank.getCapacity()));
            RenderUtils.drawFluid(matrix, height, 12, fluidStack, i+28, j+16+(54-height), getBlitOffset());

            this.font.drawShadow(matrix, "Fluid: ",this.leftPos + 44f, this.topPos + 17f, 0xffffff);
            this.font.drawShadow(matrix, getDisplayName(fluidStack),this.leftPos + 48f, this.topPos + 27f, 0xffffff);
            this.font.drawShadow(matrix, "Amount: ",this.leftPos + 44f, this.topPos + 37f, 0xffffff);
            this.font.drawShadow(matrix, fluidStack.getAmount()+"mB",this.leftPos + 48f, this.topPos + 47f, 0xffffff);
        }
    }

    private ITextComponent getDisplayName(FluidStack stack) {
        return stack.isEmpty() ? TranslationConstants.Guis.NO_FLUID : stack.getDisplayName();
    }

    private void renderEnergy(MatrixStack stack, int x, int y, float percentage) {
        int scaledRF = (int) (54*percentage);
        this.blit(stack, x, y+(54-scaledRF), 176, 54-scaledRF, 12, scaledRF);
    }

    @Override
    public void render(@NotNull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (this.menu.getHoneyGeneratorTileEntity() != null) {
            this.renderBackground(matrix);
            super.render(matrix, mouseX, mouseY, partialTicks);
            this.renderTooltip(matrix, mouseX, mouseY);
            renderFluidTooltip(matrix, mouseX, mouseY, ModConstants.DECIMAL_FORMAT);
            renderEnergyTooltip(matrix, mouseX, mouseY, ModConstants.DECIMAL_FORMAT);
        }
    }

    public void renderEnergyTooltip(@NotNull MatrixStack matrix, int mouseX, int mouseY, DecimalFormat decimalFormat) {
        if (MathUtils.inRangeInclusive(mouseX, this.leftPos + 136, this.leftPos + 148) && MathUtils.inRangeInclusive(mouseY, this.topPos + 16, this.topPos + 70)) {
            if (Screen.hasShiftDown() || this.menu.getEnergy().getEnergyStored() < 500)
                this.renderTooltip(matrix, new StringTextComponent(this.menu.getEnergy().getEnergyStored() + " RF"), mouseX, mouseY);
            else
                this.renderTooltip(matrix, new StringTextComponent(decimalFormat.format((double) this.menu.getEnergy().getEnergyStored() / 1000) + " kRF"), mouseX, mouseY);
        }
    }

    public void renderFluidTooltip(@NotNull MatrixStack matrix, int mouseX, int mouseY, DecimalFormat decimalFormat) {
        if (MathUtils.inRangeInclusive(mouseX, this.leftPos + 28, this.leftPos + 40) && MathUtils.inRangeInclusive(mouseY, this.topPos + 16, this.topPos + 70)) {
            FluidStack fluid = this.menu.getHoneyGeneratorTileEntity().getTank().getFluid();
            if (Screen.hasShiftDown() || fluid.getAmount() < 500)
                this.renderTooltip(matrix, new StringTextComponent(fluid.getAmount() + " MB"), mouseX, mouseY);
            else
                this.renderTooltip(matrix, new StringTextComponent(decimalFormat.format((double) fluid.getAmount() / 1000) + " Buckets"), mouseX, mouseY);
        }
    }
}
