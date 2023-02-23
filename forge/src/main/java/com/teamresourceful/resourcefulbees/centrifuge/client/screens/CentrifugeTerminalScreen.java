package com.teamresourceful.resourcefulbees.centrifuge.client.screens;

import com.teamresourceful.resourcefulbees.centrifuge.client.components.controlpanels.terminal.*;
import com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.terminal.TerminalHomePanel;
import com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.terminal.dump.TerminalVoidHomePanel;
import com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.terminal.input.TerminalIOPanel;
import com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.terminal.input.TerminalInputHomePanel;
import com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.terminal.output.TerminalOutputHomePanel;
import com.teamresourceful.resourcefulbees.centrifuge.common.containers.CentrifugeTerminalContainer;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.CentrifugeFluidOutputEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.CentrifugeItemOutputEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.CentrifugeTerminalEntity;
import com.teamresourceful.resourcefulbees.common.lib.enums.CentrifugeOutputType;
import com.teamresourceful.resourcefulbees.common.lib.enums.ControlPanelTabs;
import com.teamresourceful.resourcefulbees.common.lib.enums.TerminalPanels;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class CentrifugeTerminalScreen extends BaseCentrifugeScreen<CentrifugeTerminalContainer> {

    public CentrifugeTerminalScreen(CentrifugeTerminalContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new TerminalControlPanel(leftPos+21, topPos+39, this));
        initializeControlPanelTab();
    }

    @Override
    protected void switchControlPanelTab(ControlPanelTabs controlPanelTab, boolean initialize) {
        this.controlPanelTab = controlPanelTab;
        int pX = leftPos+21;
        int pY = topPos+140;

        switch (controlPanelTab) {
            //Terminal_Home does not change the nav stuff or update/reset the selection index
            case HOME -> setNavPanelAndUpdate(null, initialize);
            case INPUTS -> setNavPanelAndUpdate(new TerminalInputNavPanel(pX, pY, this), initialize);
            case ITEM_OUTPUTS -> setNavPanelAndUpdate(new TerminalItemOutputNavPanel(pX, pY, this), initialize);
            case FLUID_OUTPUTS -> setNavPanelAndUpdate(new TerminalFluidOutputNavPanel(pX, pY, this), initialize);
            case FILTERS -> setNavPanelAndUpdate(new TerminalVoidNavPanel(pX, pY, this), initialize);
            default -> setNavPanelAndUpdate(null, initialize);
        }
    }

    @Override
    protected void setNavPanelTab(boolean initialize) {
        if (initialize) {
            switchNavPanelTab(this.navPanelTab, currentInfoPanel);
        } else switch (controlPanelTab) {
            case HOME -> setDefaultNavPanelTab(TerminalPanels.TERMINAL_HOME);
            case INPUTS -> setDefaultNavPanelTab(TerminalPanels.INPUTS_HOME);
            case ITEM_OUTPUTS -> setDefaultNavPanelTab(TerminalPanels.ITEM_OUTPUTS_HOME);
            case FLUID_OUTPUTS -> setDefaultNavPanelTab(TerminalPanels.FLUID_OUTPUTS_HOME);
            case FILTERS -> setDefaultNavPanelTab(TerminalPanels.FILTERS_HOME);
            default -> removeNavPanelIfExists();
        }
    }

    @Override
    protected void updateInfoPanel(@NotNull TerminalPanels newInfoPanel) {
        int pX = leftPos+102;
        int pY = topPos+39;
        switch (newInfoPanel) {
            //Terminal_Home does not change the nav stuff or update/reset the selection index
            case TERMINAL_HOME -> updateInfoPanel(new TerminalHomePanel(pX, pY, centrifugeState, getTerminal(), menu.getEnergyStorage()));
            case INPUTS_HOME -> updateInfoPanel(new TerminalInputHomePanel(pX, pY, menu.getEnergyStorage()));
            case INPUTS_ITEM_OUTPUTS -> updateInfoPanel(new TerminalIOPanel(pX, pY, CentrifugeOutputType.ITEM, centrifugeState.getItemOutputs()));
            case INPUTS_FLUID_OUTPUTS -> updateInfoPanel(new TerminalIOPanel(pX, pY, CentrifugeOutputType.FLUID, centrifugeState.getFluidOutputs()));
            case ITEM_OUTPUTS_HOME -> updateInfoPanel(new TerminalOutputHomePanel<>(pX, pY, CentrifugeItemOutputEntity.class));
            case FLUID_OUTPUTS_HOME -> updateInfoPanel(new TerminalOutputHomePanel<>(pX, pY, CentrifugeFluidOutputEntity.class));
            case FILTERS_HOME -> updateInfoPanel(new TerminalVoidHomePanel(pX, pY));
            default -> removeInfoPanelIfExists();
        }
    }

    private CentrifugeTerminalEntity getTerminal() {
        return getBlockEntity(BlockPos.of(centrifugeState.getTerminal()), CentrifugeTerminalEntity.class);
    }

    @Override
    public void notifyInfoPanelOfEntitySelection() {
        if (infoPanel == null || navPanel == null) return;
        infoPanel.updateSelectedEntity(navPanel.selectedEntity());
        if (infoPanel instanceof TerminalInputHomePanel homePanel) {
            homePanel.setProcessData(menu.getProcessData(navPanel.selectedEntity().getBlockPos()));
        }
    }

    @Override
    protected ControlPanelTabs defaultControlPanelTab() {
        return ControlPanelTabs.HOME;
    }

    @Override
    protected ControlPanelTabs defaultNavPanelTab() {
        return ControlPanelTabs.HOME;
    }

    @Override
    protected TerminalPanels defaultInfoPanelTab() {
        return TerminalPanels.TERMINAL_HOME;
    }
}
