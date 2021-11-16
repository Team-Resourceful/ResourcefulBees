package com.teamresourceful.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.inventory.containers.HoneyPotContainer;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import com.teamresourceful.resourcefulbees.common.utils.RenderUtils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class HoneyPotScreen extends ContainerScreen<HoneyPotContainer> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/honey_tank/honey_pot.png");

    public HoneyPotScreen(HoneyPotContainer pMenu, PlayerInventory pPlayerInventory, ITextComponent pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(@NotNull MatrixStack matrix, float partialTicks, int x, int y) {
        this.renderBackground(matrix);

        if (this.minecraft == null) return;

        this.minecraft.getTextureManager().bind(BACKGROUND);
        blit(matrix, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        FluidStack fluidStack = menu.getTileEntity().getTank().getFluid();

        this.font.drawShadow(matrix, "Fluid: ",this.leftPos + 36f, this.topPos + 17f, 0xffffff);
        this.font.drawShadow(matrix, getDisplayName(fluidStack),this.leftPos + 40f, this.topPos + 27f, 0xffffff);
        this.font.drawShadow(matrix, "Amount: ",this.leftPos + 36f, this.topPos + 37f, 0xffffff);
        this.font.drawShadow(matrix, fluidStack.getAmount()+"mB",this.leftPos + 40f, this.topPos + 47f, 0xffffff);

        int height = (int) ((fluidStack.getAmount() / 64000f) * 54);
        RenderUtils.drawFluid(matrix, height, 12, fluidStack, this.leftPos+129, this.topPos+16+(54-height), this.getBlitOffset());
    }

    private ITextComponent getDisplayName(FluidStack stack) {
        return stack.isEmpty() ? TranslationConstants.Guis.NO_FLUID : stack.getDisplayName();
    }

    @Override
    public void render(@NotNull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.render(matrix, mouseX, mouseY, partialTicks);

        renderTooltip(matrix, mouseX, mouseY);

        if (this.minecraft == null) return;

        if (MathUtils.inRangeInclusive(mouseX, this.leftPos+129, this.leftPos+141) && MathUtils.inRangeInclusive(mouseY, this.topPos+16, this.topPos+70)) {
            FluidStack fluidStack = menu.getTileEntity().getTank().getFluid();
            ITextComponent component = getDisplayName(fluidStack).copy().append(new StringTextComponent(" : " + fluidStack.getAmount() + "mB"));
            renderTooltip(matrix, component, mouseX, mouseY);
        }
    }
}
