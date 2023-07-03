package com.teamresourceful.resourcefulbees.centrifuge.client.screens;

import com.teamresourceful.resourcefulbees.centrifuge.client.components.controlpanels.OutputControlPanel;
import com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.terminal.output.TerminalOutputHomePanel;
import com.teamresourceful.resourcefulbees.centrifuge.common.containers.CentrifugeFluidOutputContainer;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.CentrifugeFluidOutputEntity;
import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.lib.enums.TerminalPanels;
import com.teamresourceful.resourcefulbees.common.util.MathUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import static com.teamresourceful.resourcefulbees.client.util.TextUtils.FONT_COLOR_1;
import static com.teamresourceful.resourcefulbees.client.util.TextUtils.TERMINAL_FONT_8;

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
    protected void drawContainerSlots(@NotNull GuiGraphics graphics, int x, int y) {
        //has fluid tank!
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float pPartialTicks, int pX, int pY) {
        super.renderBg(graphics, pPartialTicks, pX, pY);
        if (!menu.displaySlots()) return;
        drawFluidTank(graphics, leftPos + 228, topPos + 45);
        FluidStack fluidStack = menu.getEntity().getFluidTank().getFluid();

        graphics.drawString(TERMINAL_FONT_8, "Fluid: ", this.leftPos + 148, this.topPos + 55, FONT_COLOR_1, false);
        graphics.drawString(TERMINAL_FONT_8, getDisplayName(fluidStack), this.leftPos + 150, this.topPos + 65, FONT_COLOR_1, false);
        graphics.drawString(TERMINAL_FONT_8, "Amount: ", this.leftPos + 148, this.topPos + 75, FONT_COLOR_1, false);
        graphics.drawString(TERMINAL_FONT_8, fluidStack.getAmount() + "mB", this.leftPos + 150, this.topPos + 85, FONT_COLOR_1, false);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (!menu.displaySlots()) return;
        if (MathUtils.inRangeInclusive(mouseX, this.leftPos+229, this.leftPos+245) && MathUtils.inRangeInclusive(mouseY, this.topPos+46, this.topPos+113)) {
            int fluidAmount = menu.getEntity().getFluidTank().getFluidAmount();
            Component tooltip = Screen.hasShiftDown() || fluidAmount < 1000 ? getMillibuckets(fluidAmount) : getBuckets(fluidAmount);
            setTooltipForNextRenderPass(tooltip);
        }
    }

    private Component getDisplayName(FluidStack stack) {
        return stack.isEmpty() ? GuiTranslations.NO_FLUID : stack.getDisplayName();
    }

    private Component getMillibuckets(int fluidAmount) {
        return Component.literal(fluidAmount + "mB");
    }

    private Component getBuckets(int fluidAmount) {
        return Component.literal(((double) fluidAmount / 1000) + "B");
    }

    private void drawFluidTank(GuiGraphics graphics, int x, int y) {
        graphics.blit(CentrifugeTextures.COMPONENTS, x, y, u, v, 18, 69);
        FluidStack fluidStack = menu.getEntity().getFluidTank().getFluid();
        int height = (int) ((float) fluidStack.getAmount() / tier.getTankCapacity() * 67);
        IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        TextureAtlasSprite sprite = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(props.getStillTexture(fluidStack));
        int fluidColor = props.getTintColor(fluidStack);
        ClientRenderUtils.drawFluid(graphics, height, 16, sprite, fluidColor, x+1, y+68-height);
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
