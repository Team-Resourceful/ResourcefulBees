package com.teamresourceful.resourcefulbees.client.components.centrifuge.controlpanels.terminal;

import com.teamresourceful.resourcefulbees.client.components.centrifuge.controlpanels.NavigableControlPanel;
import com.teamresourceful.resourcefulbees.common.lib.enums.ControlPanelTabs;
import com.teamresourceful.resourcefulbees.common.lib.enums.TerminalPanels;
import com.teamresourceful.resourcefulbees.client.screens.centrifuge.CentrifugeTerminalScreen;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeInputEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeUtils;
import net.minecraft.network.chat.Component;

public class TerminalInputNavPanel extends NavigableControlPanel<CentrifugeInputEntity> {

    public TerminalInputNavPanel(int x, int y, CentrifugeTerminalScreen screen) {
        super(x, y, screen, screen.centrifugeState().getInputs());
    }

    @Override
    protected void init() {
        createNavPanelDisplayTab(y+18, ControlPanelTabs.HOME, TerminalPanels.INPUTS_HOME);
        createNavPanelDisplayTab(y+32, ControlPanelTabs.ITEM_OUTPUTS, TerminalPanels.INPUTS_ITEM_OUTPUTS);
        createNavPanelDisplayTab(y+46, ControlPanelTabs.FLUID_OUTPUTS, TerminalPanels.INPUTS_FLUID_OUTPUTS);
    }

    @Override
    protected Component getNavType() {
        return Component.literal("Input");
    }

    @Override
    protected void updateSelectedEntity() {
        selectedEntity = screen.getBlockEntity(CentrifugeUtils.getFromCollection(navList, screen.selectionIndex()), CentrifugeInputEntity.class);
    }
}
