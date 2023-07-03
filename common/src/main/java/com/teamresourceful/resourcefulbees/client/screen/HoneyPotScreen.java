package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.menus.HoneyPotMenu;
import com.teamresourceful.resourcefulbees.common.util.MathUtils;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.ClientFluidHooks;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class HoneyPotScreen extends AbstractContainerScreen<HoneyPotMenu> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/honey_tank/honey_pot.png");

    public HoneyPotScreen(HoneyPotMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int x, int y) {
        this.renderBackground(graphics);

        if (this.minecraft == null) return;
        graphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        FluidHolder holder = menu.getEntity().getFluid();

        graphics.drawString(this.font, "Fluid: ",this.leftPos + 36, this.topPos + 17, 0xffffff);
        graphics.drawString(this.font, holder.isEmpty() ? GuiTranslations.NO_FLUID : ClientFluidHooks.getDisplayName(holder), this.leftPos + 40, this.topPos + 27, 0xffffff);
        graphics.drawString(this.font, "Amount: ",this.leftPos + 36, this.topPos + 37, 0xffffff);
        graphics.drawString(this.font, FluidHooks.toMillibuckets(holder.getFluidAmount())+"mB",this.leftPos + 40, this.topPos + 47, 0xffffff);

        int height = (int) ((holder.getFluidAmount() / FluidHooks.buckets(64)) * 54);
        ClientRenderUtils.drawFluid(graphics, height, 12, holder, this.leftPos+129, this.topPos+16);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
        if (this.minecraft == null) return;

        if (MathUtils.inRangeInclusive(mouseX, leftPos+129, leftPos+141) && MathUtils.inRangeInclusive(mouseY, topPos+16, topPos+70)) {
            long fluidAmount = menu.getEntity().getFluid().getFluidAmount();
            Component tooltip = Screen.hasShiftDown() || fluidAmount < 1000 ? getMillibuckets(fluidAmount) : getBuckets(fluidAmount);
            setTooltipForNextRenderPass(tooltip);
        }

/*        if (MathUtils.inRangeInclusive(mouseX, this.leftPos+129, this.leftPos+141) && MathUtils.inRangeInclusive(mouseY, this.topPos+16, this.topPos+70)) {
            FluidStack fluidStack = menu.getEntity().getTank().getFluid();
            Component component = getDisplayName(fluidStack).copy().append(Component.literal(" : " + fluidStack.getAmount() + "mB"));
            renderTooltip(stack, component, mouseX, mouseY);
        }*/
    }



    private Component getMillibuckets(long fluidAmount) {
        return Component.literal(FluidHooks.toMillibuckets(fluidAmount) + "mB");
    }

    private Component getBuckets(long fluidAmount) {
        return Component.literal(((double) fluidAmount / FluidHooks.getBucketAmount()) + "B");
    }
}
