package com.teamresourceful.resourcefulbees.centrifuge.client.components;

import com.teamresourceful.resourcefulbees.centrifuge.common.entities.CentrifugeFluidOutputEntity;
import com.teamresourceful.resourcefullib.client.components.ParentWidget;


//todo not sure if i should go through with this or not...
public class FluidTankWidget extends ParentWidget {

    private final CentrifugeFluidOutputEntity fluidOutput;

    public FluidTankWidget(int x, int y, CentrifugeFluidOutputEntity fluidOutput) {
        super(x, y);
        this.fluidOutput = fluidOutput;
    }

/*    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        if (isMouseOver(mouseX, mouseY)) {
            if (Screen.hasShiftDown() || fluidOutput.getFluidTank().getFluidAmount() < 1000) {
                this.renderTooltip(stack, Component.literal(fluidOutput.getFluidTank().getFluidAmount() + " MB"), mouseX, mouseY);
            } else {
                this.renderTooltip(stack, Component.literal(NumberFormat.getPercentInstance().format((double) fluidOutput.getFluidTank().getFluidAmount() / 1000) + " Buckets"), mouseX, mouseY);
            }
        }
    }

    private void drawFluidTank(PoseStack matrix, int x, int y) {
        blit(matrix, x, y, u, v, 18, 69);
        FluidStack fluidStack = menu.getEntity().getFluidTank().getFluid();
        int height = (fluidStack.getAmount() / tier.getTankCapacity()) * 67;
        ClientUtils.drawFluid(matrix, height, 16, fluidStack, x+1, y+67-height, getBlitOffset());
    }*/

    @Override
    protected void init() {

    }
}
