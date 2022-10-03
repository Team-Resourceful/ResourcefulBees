package com.teamresourceful.resourcefulbees.client.components.centrifuge.controlpanels;

import com.teamresourceful.resourcefulbees.common.lib.enums.ControlPanelTabs;
import com.teamresourceful.resourcefulbees.common.lib.enums.TerminalPanels;
import com.teamresourceful.resourcefulbees.client.screens.centrifuge.CentrifugeTerminalScreen;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeFluidOutputEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeUtils;

public class TerminalFluidOutputNavPanel extends NavigableControlPanel<CentrifugeFluidOutputEntity> {

    public TerminalFluidOutputNavPanel(int x, int y, CentrifugeTerminalScreen screen) {
        super(x, y, screen, screen.centrifugeState().getFluidOutputs(), "Output");
    }

    @Override
    protected void init() {
        createNavPanelDisplayTab(y + 18, ControlPanelTabs.HOME, TerminalPanels.FLUID_OUTPUTS_HOME);
        createNavPanelVoidExcessTab(y + 32, () -> selectedEntity != null && selectedEntity.voidsExcess());
    }

    @Override
    protected void updateSelectedEntity() {
        selectedEntity = screen.getBlockEntity(CentrifugeUtils.getFromCollection(navList, screen.selectionIndex()), CentrifugeFluidOutputEntity.class);
    }
}
