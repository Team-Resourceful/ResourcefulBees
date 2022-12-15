package com.teamresourceful.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.utils.ClientUtils;
import com.teamresourceful.resourcefulbees.common.blockentity.SolidificationChamberBlockEntity;
import com.teamresourceful.resourcefulbees.common.inventory.menus.SolidificationChamberMenu;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.util.MathUtils;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;


@OnlyIn(Dist.CLIENT)
public class SolidificationChamberScreen extends AbstractContainerScreen<SolidificationChamberMenu> {

    private final SolidificationChamberBlockEntity tileEntity;

    public SolidificationChamberScreen(SolidificationChamberMenu container, Inventory inventory, Component displayName) {
        super(container, inventory, displayName);
        tileEntity = container.getEntity();
        titleLabelY -= 3;
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrix, float partialTicks, int mouseX, int mouseY) {
        ResourceLocation texture = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/solidification/solidification.png");
        if (tileEntity != null) {
            RenderUtils.bindTexture(texture);
            int i = this.leftPos;
            int j = this.topPos;
            this.blit(matrix, i, j, 0, 0, this.imageWidth, this.imageHeight);
            FluidStack fluidStack = tileEntity.getTank().getFluid();
            int height = (int) ((fluidStack.getAmount() / 16000f) * 62);
            ClientUtils.drawFluid(matrix, height, 14, fluidStack, i + 67, j + 12+(62-height), getBlitOffset());
        }
    }

    private void renderProgressBar(PoseStack matrix) {
        ResourceLocation texture = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/solidification/solidification.png");
        RenderUtils.bindTexture(texture);
        int i = this.leftPos;
        int j = this.topPos;
        this.blit(matrix, i + 84, j + 17, 176, 0, 24, (int) (34 * tileEntity.getProcessPercent()));
    }

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (this.tileEntity != null) {
            this.renderBackground(stack);
            super.render(stack, mouseX, mouseY, partialTicks);
            this.renderProgressBar(stack);
            this.renderTooltip(stack, mouseX, mouseY);
            if (MathUtils.inRangeInclusive(mouseX, leftPos+67, leftPos+81) && MathUtils.inRangeInclusive(mouseY, topPos+12, topPos+74)) {
                int fluidAmount = tileEntity.getTank().getFluidAmount();
                Component tooltip = Screen.hasShiftDown() || fluidAmount < 1000 ? getMillibuckets(fluidAmount) : getBuckets(fluidAmount);
                this.renderTooltip(stack, tooltip, mouseX, mouseY);
            }
        }
    }

    private Component getMillibuckets(int fluidAmount) {
        return Component.literal(fluidAmount + "mB");
    }

    private Component getBuckets(int fluidAmount) {
        return Component.literal(((double) fluidAmount / 1000) + "B");
    }
}
