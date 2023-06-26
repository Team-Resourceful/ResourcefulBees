package com.teamresourceful.resourcefulbees.centrifuge.client.screens;

import com.teamresourceful.resourcefulbees.centrifuge.client.components.controlpanels.InputControlPanel;
import com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.terminal.input.TerminalIOPanel;
import com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.terminal.input.TerminalInputHomePanel;
import com.teamresourceful.resourcefulbees.centrifuge.common.containers.CentrifugeInputContainer;
import com.teamresourceful.resourcefulbees.common.lib.enums.CentrifugeOutputType;
import com.teamresourceful.resourcefulbees.common.lib.enums.TerminalPanels;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class CentrifugeInputScreen extends CentrifugeInventoryScreen<CentrifugeInputContainer> {

    public CentrifugeInputScreen(CentrifugeInputContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, 0, 36);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new InputControlPanel(leftPos+21, topPos+39, this));
        initializeControlPanelTab();
    }

    @Override
    protected void setNavPanelTab(boolean initialize) {
        if (initialize) {
            switchNavPanelTab(this.navPanelTab, currentInfoPanel);
        } else switch (controlPanelTab) {
            case HOME -> setDefaultNavPanelTab(TerminalPanels.INPUTS_HOME);
            case INVENTORY -> setDefaultNavPanelTab(TerminalPanels.INVENTORY);
            case ITEM_OUTPUTS -> setDefaultNavPanelTab(TerminalPanels.INPUTS_ITEM_OUTPUTS);
            case FLUID_OUTPUTS -> setDefaultNavPanelTab(TerminalPanels.INPUTS_FLUID_OUTPUTS);
            default -> removeNavPanelIfExists();
        }
    }

    @Override
    protected void updateInfoPanel(@NotNull TerminalPanels newInfoPanel) {
        int pX = leftPos+102;
        int pY = topPos+39;
        switch (newInfoPanel) {
            //Terminal_Home does not change the nav stuff or update/reset the selection index
            case INVENTORY -> {
                removeInfoPanelIfExists();
                menu.enableSlots();
            }
            case INPUTS_HOME -> {
                updateInfoPanel(new TerminalInputHomePanel(pX, pY, false, menu.getEnergyStorage()));
                menu.disableSlots();
            }
            case INPUTS_ITEM_OUTPUTS -> {
                updateInfoPanel(new TerminalIOPanel(pX, pY, CentrifugeOutputType.ITEM, centrifugeState.getItemOutputs(), false));
                menu.disableSlots();
            }
            case INPUTS_FLUID_OUTPUTS -> {
                updateInfoPanel(new TerminalIOPanel(pX, pY, CentrifugeOutputType.FLUID, centrifugeState.getFluidOutputs(), false));
                menu.disableSlots();
            }
            default -> removeInfoPanelIfExists();
        }
    }

    @Override
    protected void drawContainerSlots(@NotNull GuiGraphics graphics, int x, int y) {
        drawSlot(graphics, x+125, y+63, 0, 0);
        super.drawContainerSlots(graphics, x, y);
    }

    @Override
    public void notifyInfoPanelOfEntitySelection() {
        if (infoPanel == null) return;
        infoPanel.updateSelectedEntity(menu.getEntity());
        if (infoPanel instanceof TerminalInputHomePanel homePanel) {
            homePanel.setProcessData(menu.getProcessData());
        }
    }
}
