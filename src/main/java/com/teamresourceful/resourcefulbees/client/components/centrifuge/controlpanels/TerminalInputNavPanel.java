package com.teamresourceful.resourcefulbees.client.components.centrifuge.controlpanels;

import com.teamresourceful.resourcefulbees.common.lib.enums.ControlPanelTabs;
import com.teamresourceful.resourcefulbees.common.lib.enums.TerminalPanels;
import com.teamresourceful.resourcefulbees.client.screens.centrifuge.CentrifugeTerminalScreen;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeInputEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeUtils;

public class TerminalInputNavPanel extends NavigableControlPanel<CentrifugeInputEntity> {

    public TerminalInputNavPanel(int x, int y, CentrifugeTerminalScreen screen) {
        super(x, y, screen, screen.centrifugeState().getInputs(), "Input");
    }

    @Override
    protected void init() {
        createNavPanelDisplayTab(y+18, ControlPanelTabs.HOME, TerminalPanels.INPUTS_HOME);
        createNavPanelDisplayTab(y+32, ControlPanelTabs.ITEM_OUTPUTS, TerminalPanels.INPUTS_ITEM_OUTPUTS);
        createNavPanelDisplayTab(y+46, ControlPanelTabs.FLUID_OUTPUTS, TerminalPanels.INPUTS_FLUID_OUTPUTS);
    }

    @Override
    protected void updateSelectedEntity() {
        selectedEntity = screen.getBlockEntity(CentrifugeUtils.getFromCollection(navList, screen.selectionIndex()), CentrifugeInputEntity.class);
    }
}
