package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import com.teamresourceful.resourcefulbees.common.blockentities.SolidificationChamberBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.menus.SolidificationChamberMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class SolidificationChamberScreen extends AbstractContainerScreen<SolidificationChamberMenu> {

    private static final Identifier TEXTURE = ModIdentifier.of("textures/gui/solidification/solidification.png");

    private final SolidificationChamberBlockEntity tileEntity;

    public SolidificationChamberScreen(SolidificationChamberMenu menu, Inventory inventory, Component displayName) {
        super(menu, inventory, displayName);
        tileEntity = menu.getEntity();
        titleLabelY -= 3;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (tileEntity != null) {
            int i = this.leftPos;
            int j = this.topPos;
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

            //int height = (int) (tileEntity.fluidHandler().percentStored() * 62);
            ClientRenderUtils.drawTank(graphics, tileEntity.fluidHandler(), 0, i+67, j + 12, 14, 62);
        }
    }

    /*    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/gui/solidification/solidification.png");
        *//*todo if (tileEntity != null) {
            int i = this.leftPos;
            int j = this.topPos;
            graphics.blit(texture, i, j, 0, 0, this.imageWidth, this.imageHeight);
            FluidHolder holder = tileEntity.getFluid();
            int height = (int) ((holder.getFluidAmount() / FluidHooks.buckets(16)) * 62);
            ClientRenderUtils.drawFluid(graphics, height, 14, holder, i + 67, j + 12+(62-height));
        }*//*
    }

    private void renderProgressBar(GuiGraphics graphics) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/gui/solidification/solidification.png");
        int i = this.leftPos;
        int j = this.topPos;
        graphics.blit(texture, i + 84, j + 17, 176, 0, 24, (int) (34 * tileEntity.getProcessPercent()));
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.tileEntity != null) {
            this.renderBackground(graphics, mouseX, mouseY, partialTicks);
            super.render(graphics, mouseX, mouseY, partialTicks);
            this.renderProgressBar(graphics);
            this.renderTooltip(graphics, mouseX, mouseY);
            *//*todo if (MathUtils.inRangeInclusive(mouseX, leftPos+67, leftPos+81) && MathUtils.inRangeInclusive(mouseY, topPos+12, topPos+74)) {
                long fluidAmount = tileEntity.getFluid().getFluidAmount();
                setTooltipForNextRenderPass(Screen.hasShiftDown() || fluidAmount < 1000 ? getMillibuckets(fluidAmount) : getBuckets(fluidAmount));
            }*//*
        }
    }*/

    /*private Component getMillibuckets(long fluidAmount) {
        return Component.literal(FluidHooks.toMillibuckets(fluidAmount) + "mB");
    }

    private Component getBuckets(long fluidAmount) {
        return Component.literal(((double) fluidAmount / FluidHooks.getBucketAmount()) + "B");
    }*/
}
