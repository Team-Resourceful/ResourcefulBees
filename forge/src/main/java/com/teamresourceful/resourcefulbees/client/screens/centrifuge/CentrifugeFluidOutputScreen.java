package com.teamresourceful.resourcefulbees.client.screens.centrifuge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.components.centrifuge.controlpanels.OutputControlPanel;
import com.teamresourceful.resourcefulbees.client.components.centrifuge.infopanels.terminal.output.TerminalOutputHomePanel;
import com.teamresourceful.resourcefulbees.client.utils.ClientUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.TerminalPanels;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeFluidOutputContainer;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeFluidOutputEntity;
import com.teamresourceful.resourcefulbees.common.util.MathUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import static com.teamresourceful.resourcefulbees.client.utils.TextUtils.FONT_COLOR_1;
import static com.teamresourceful.resourcefulbees.client.utils.TextUtils.TERMINAL_FONT_8;

public class CentrifugeFluidOutputScreen extends CentrifugeInventoryScreen<CentrifugeFluidOutputContainer> {

    public CentrifugeFluidOutputScreen(CentrifugeFluidOutputContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, 0, 90);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new OutputControlPanel(leftPos + 21, topPos + 39, this));
        initializeControlPanelTab();
    }

    @Override
    protected void drawContainerSlots(@NotNull PoseStack matrix, int x, int y) {
        //has fluid tank!
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrix, float pPartialTicks, int pX, int pY) {
        super.renderBg(matrix, pPartialTicks, pX, pY);
        if (!menu.displaySlots()) return;
        drawFluidTank(matrix, leftPos + 228, topPos + 45);
        FluidStack fluidStack = menu.getEntity().getFluidTank().getFluid();

        TERMINAL_FONT_8.draw(matrix, "Fluid: ", this.leftPos + 148f, this.topPos + 55f, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(matrix, getDisplayName(fluidStack), this.leftPos + 150f, this.topPos + 65f, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(matrix, "Amount: ", this.leftPos + 148f, this.topPos + 75f, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(matrix, fluidStack.getAmount() + "mB", this.leftPos + 150f, this.topPos + 85f, FONT_COLOR_1);
    }

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        if (!menu.displaySlots()) return;
        if (MathUtils.inRangeInclusive(mouseX, this.leftPos+229, this.leftPos+245) && MathUtils.inRangeInclusive(mouseY, this.topPos+46, this.topPos+113)) {
            int fluidAmount = menu.getEntity().getFluidTank().getFluidAmount();
            Component tooltip = Screen.hasShiftDown() || fluidAmount < 1000 ? getMillibuckets(fluidAmount) : getBuckets(fluidAmount);
            this.renderTooltip(stack, tooltip, mouseX, mouseY);
        }
    }

    private Component getDisplayName(FluidStack stack) {
        return stack.isEmpty() ? TranslationConstants.Guis.NO_FLUID : stack.getDisplayName();
    }

    private Component getMillibuckets(int fluidAmount) {
        return Component.literal(fluidAmount + "mB");
    }

    private Component getBuckets(int fluidAmount) {
        return Component.literal(((double) fluidAmount / 1000) + "B");
    }

    private void drawFluidTank(PoseStack matrix, int x, int y) {
        blit(matrix, x, y, u, v, 18, 69);
        FluidStack fluidStack = menu.getEntity().getFluidTank().getFluid();
        int height = (int) ((float) fluidStack.getAmount() / tier.getTankCapacity() * 67);
        ClientUtils.drawFluid(matrix, height, 16, fluidStack, x+1, y+68-height, getBlitOffset());
    }

    @Override
    protected void setNavPanelTab(boolean initialize) {
        if (initialize) {
            switchNavPanelTab(this.navPanelTab, currentInfoPanel);
        } else switch (controlPanelTab) {
            case HOME -> setDefaultNavPanelTab(TerminalPanels.FLUID_OUTPUTS_HOME);
            case INVENTORY -> setDefaultNavPanelTab(TerminalPanels.INVENTORY);
            default -> removeNavPanelIfExists();
        }
    }

    @Override
    protected void updateInfoPanel(@NotNull TerminalPanels newInfoPanel) {
        int pX = leftPos+102;
        int pY = topPos+39;
        switch (newInfoPanel) {
            case INVENTORY -> {
                removeInfoPanelIfExists();
                menu.enableSlots();
            }
            case FLUID_OUTPUTS_HOME -> {
                updateInfoPanel(new TerminalOutputHomePanel<>(pX, pY, CentrifugeFluidOutputEntity.class, false));
                menu.disableSlots();
            }
            default -> removeInfoPanelIfExists();
        }
    }
}
