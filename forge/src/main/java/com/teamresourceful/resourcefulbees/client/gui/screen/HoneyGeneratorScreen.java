package com.teamresourceful.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.client.utils.ClientUtils;
import com.teamresourceful.resourcefulbees.common.capabilities.HoneyFluidTank;
import com.teamresourceful.resourcefulbees.common.inventory.menus.HoneyGeneratorMenu;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.util.MathUtils;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;

@OnlyIn(Dist.CLIENT)
public class HoneyGeneratorScreen extends AbstractContainerScreen<HoneyGeneratorMenu> {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/gui/generator/honey_gen.png");

    public HoneyGeneratorScreen(HoneyGeneratorMenu screenContainer, Inventory inventory, Component titleIn) {
        super(screenContainer, inventory, titleIn);
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrix, float partialTicks, int mouseX, int mouseY) {
        Minecraft client = this.minecraft;
        if (client != null) {
            RenderUtils.bindTexture(BACKGROUND);
            int i = this.leftPos;
            int j = this.topPos;
            this.blit(matrix, i, j, 0, 0, this.imageWidth, this.imageHeight);
            renderEnergy(matrix, i+136, j+16, this.menu.getEnergy().getPercentage());

            HoneyFluidTank tank = menu.getEntity().getTank();
            FluidStack fluidStack = tank.getFluid();

            int height = (int)(54*((float)tank.getFluidAmount() / tank.getCapacity()));
            ClientUtils.drawFluid(matrix, height, 12, fluidStack, i+28, j+16+(54-height), getBlitOffset());

            this.font.drawShadow(matrix, "Fluid: ",this.leftPos + 44f, this.topPos + 17f, 0xffffff);
            this.font.drawShadow(matrix, getDisplayName(fluidStack),this.leftPos + 48f, this.topPos + 27f, 0xffffff);
            this.font.drawShadow(matrix, "Amount: ",this.leftPos + 44f, this.topPos + 37f, 0xffffff);
            this.font.drawShadow(matrix, fluidStack.getAmount()+"mB",this.leftPos + 48f, this.topPos + 47f, 0xffffff);
        }
    }

    private Component getDisplayName(FluidStack stack) {
        return stack.isEmpty() ? TranslationConstants.Guis.NO_FLUID : stack.getDisplayName();
    }

    private void renderEnergy(PoseStack stack, int x, int y, float percentage) {
        int scaledRF = (int) (54*percentage);
        this.blit(stack, x, y+(54-scaledRF), 176, 54-scaledRF, 12, scaledRF);
    }

    @Override
    public void render(@NotNull PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrix, mouseX, mouseY);
        renderFluidTooltip(matrix, mouseX, mouseY, DecimalFormat.getNumberInstance());
        renderEnergyTooltip(matrix, mouseX, mouseY, DecimalFormat.getNumberInstance());
    }

    public void renderEnergyTooltip(@NotNull PoseStack matrix, int mouseX, int mouseY, NumberFormat decimalFormat) {
        if (MathUtils.inRangeInclusive(mouseX, this.leftPos + 136, this.leftPos + 148) && MathUtils.inRangeInclusive(mouseY, this.topPos + 16, this.topPos + 70)) {
            if (Screen.hasShiftDown() || this.menu.getEnergy().getEnergyStored() < 500)
                this.renderTooltip(matrix, Component.literal(this.menu.getEnergy().getEnergyStored() + " RF"), mouseX, mouseY);
            else
                this.renderTooltip(matrix, Component.literal(decimalFormat.format((double) this.menu.getEnergy().getEnergyStored() / 1000) + " kRF"), mouseX, mouseY);
        }
    }

    public void renderFluidTooltip(@NotNull PoseStack matrix, int mouseX, int mouseY, NumberFormat decimalFormat) {
        if (MathUtils.inRangeInclusive(mouseX, this.leftPos + 28, this.leftPos + 40) && MathUtils.inRangeInclusive(mouseY, this.topPos + 16, this.topPos + 70)) {
            FluidStack fluid = this.menu.getEntity().getTank().getFluid();
            if (Screen.hasShiftDown() || fluid.getAmount() < 500)
                this.renderTooltip(matrix, Component.literal(fluid.getAmount() + " MB"), mouseX, mouseY);
            else
                this.renderTooltip(matrix, Component.literal(decimalFormat.format((double) fluid.getAmount() / 1000) + " Buckets"), mouseX, mouseY);
        }
    }
}
