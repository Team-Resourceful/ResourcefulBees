package com.teamresourceful.resourcefulbees.client.screens.centrifuge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.components.centrifuge.controlpanels.InputControlPanel;
import com.teamresourceful.resourcefulbees.client.components.centrifuge.infopanels.terminal.input.TerminalIOPanel;
import com.teamresourceful.resourcefulbees.client.components.centrifuge.infopanels.terminal.input.TerminalInputHomePanel;
import com.teamresourceful.resourcefulbees.common.lib.enums.CentrifugeOutputType;
import com.teamresourceful.resourcefulbees.common.lib.enums.TerminalPanels;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeInputContainer;
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
    protected void drawContainerSlots(@NotNull PoseStack matrix, int x, int y) {
        drawSlot(matrix, x+125, y+63, 0, 0);
        super.drawContainerSlots(matrix, x, y);
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
