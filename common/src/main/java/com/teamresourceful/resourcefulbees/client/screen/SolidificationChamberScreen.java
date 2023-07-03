package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import com.teamresourceful.resourcefulbees.common.blockentities.SolidificationChamberBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.menus.SolidificationChamberMenu;
import com.teamresourceful.resourcefulbees.common.util.MathUtils;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class SolidificationChamberScreen extends AbstractContainerScreen<SolidificationChamberMenu> {

    private final SolidificationChamberBlockEntity tileEntity;

    public SolidificationChamberScreen(SolidificationChamberMenu container, Inventory inventory, Component displayName) {
        super(container, inventory, displayName);
        tileEntity = container.getEntity();
        titleLabelY -= 3;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        ResourceLocation texture = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/solidification/solidification.png");
        if (tileEntity != null) {
            int i = this.leftPos;
            int j = this.topPos;
            graphics.blit(texture, i, j, 0, 0, this.imageWidth, this.imageHeight);
            FluidHolder holder = tileEntity.getFluid();
            int height = (int) ((holder.getFluidAmount() / 16000f) * 62);
            ClientRenderUtils.drawFluid(graphics, height, 14, holder, i + 67, j + 12+(62-height));
        }
    }

    private void renderProgressBar(GuiGraphics graphics) {
        ResourceLocation texture = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/solidification/solidification.png");
        int i = this.leftPos;
        int j = this.topPos;
        graphics.blit(texture, i + 84, j + 17, 176, 0, 24, (int) (34 * tileEntity.getProcessPercent()));
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.tileEntity != null) {
            this.renderBackground(graphics);
            super.render(graphics, mouseX, mouseY, partialTicks);
            this.renderProgressBar(graphics);
            this.renderTooltip(graphics, mouseX, mouseY);
            if (MathUtils.inRangeInclusive(mouseX, leftPos+67, leftPos+81) && MathUtils.inRangeInclusive(mouseY, topPos+12, topPos+74)) {
                long fluidAmount = tileEntity.getFluid().getFluidAmount();
                setTooltipForNextRenderPass(Screen.hasShiftDown() || fluidAmount < 1000 ? getMillibuckets(fluidAmount) : getBuckets(fluidAmount));
            }
        }
    }

    private Component getMillibuckets(long fluidAmount) {
        return Component.literal(FluidHooks.toMillibuckets(fluidAmount) + "mB");
    }

    private Component getBuckets(long fluidAmount) {
        return Component.literal(((double) fluidAmount / FluidHooks.getBucketAmount()) + "B");
    }
}
