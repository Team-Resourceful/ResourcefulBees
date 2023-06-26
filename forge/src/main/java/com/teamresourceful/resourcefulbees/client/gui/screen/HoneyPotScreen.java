package com.teamresourceful.resourcefulbees.client.gui.screen;

import com.teamresourceful.resourcefulbees.client.utils.ClientUtils;
import com.teamresourceful.resourcefulbees.common.inventory.menus.HoneyPotMenu;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.util.MathUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
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

        FluidStack fluidStack = menu.getEntity().getTank().getFluid();

        graphics.drawString(this.font, "Fluid: ",this.leftPos + 36, this.topPos + 17, 0xffffff);
        graphics.drawString(this.font, getDisplayName(fluidStack),this.leftPos + 40, this.topPos + 27, 0xffffff);
        graphics.drawString(this.font, "Amount: ",this.leftPos + 36, this.topPos + 37, 0xffffff);
        graphics.drawString(this.font, fluidStack.getAmount()+"mB",this.leftPos + 40, this.topPos + 47, 0xffffff);

        int height = (int) ((fluidStack.getAmount() / 64000f) * 54);
        ClientUtils.drawFluid(graphics, height, 12, fluidStack, this.leftPos+129, this.topPos+16+(54-height));
    }

    private Component getDisplayName(FluidStack stack) {
        return stack.isEmpty() ? GuiTranslations.NO_FLUID : stack.getDisplayName();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
        if (this.minecraft == null) return;

        if (MathUtils.inRangeInclusive(mouseX, leftPos+129, leftPos+141) && MathUtils.inRangeInclusive(mouseY, topPos+16, topPos+70)) {
            int fluidAmount = menu.getEntity().getTank().getFluidAmount();
            Component tooltip = Screen.hasShiftDown() || fluidAmount < 1000 ? getMillibuckets(fluidAmount) : getBuckets(fluidAmount);
            setTooltipForNextRenderPass(tooltip);
        }

/*        if (MathUtils.inRangeInclusive(mouseX, this.leftPos+129, this.leftPos+141) && MathUtils.inRangeInclusive(mouseY, this.topPos+16, this.topPos+70)) {
            FluidStack fluidStack = menu.getEntity().getTank().getFluid();
            Component component = getDisplayName(fluidStack).copy().append(Component.literal(" : " + fluidStack.getAmount() + "mB"));
            renderTooltip(stack, component, mouseX, mouseY);
        }*/
    }



    private Component getMillibuckets(int fluidAmount) {
        return Component.literal(fluidAmount + "mB");
    }

    private Component getBuckets(int fluidAmount) {
        return Component.literal(((double) fluidAmount / 1000) + "B");
    }
}
