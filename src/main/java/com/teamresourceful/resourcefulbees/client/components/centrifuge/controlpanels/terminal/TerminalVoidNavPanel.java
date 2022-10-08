package com.teamresourceful.resourcefulbees.client.components.centrifuge.controlpanels.terminal;

import com.teamresourceful.resourcefulbees.client.components.centrifuge.controlpanels.NavigableControlPanel;
import com.teamresourceful.resourcefulbees.client.screens.centrifuge.CentrifugeTerminalScreen;
import com.teamresourceful.resourcefulbees.common.lib.enums.ControlPanelTabs;
import com.teamresourceful.resourcefulbees.common.lib.enums.TerminalPanels;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeVoidEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeUtils;
import net.minecraft.network.chat.Component;

public class TerminalVoidNavPanel extends NavigableControlPanel<CentrifugeVoidEntity> {

    public TerminalVoidNavPanel(int x, int y, CentrifugeTerminalScreen screen) {
        super(x, y, screen, screen.centrifugeState().getDumps());
    }

    @Override
    protected void init() {
        createNavPanelDisplayTab(y + 18, ControlPanelTabs.HOME, TerminalPanels.FLUID_OUTPUTS_HOME);
    }

    @Override
    protected void updateSelectedEntity() {
        selectedEntity = screen.getBlockEntity(CentrifugeUtils.getFromCollection(navList, screen.selectionIndex()), CentrifugeVoidEntity.class);
    }

    @Override
    protected Component getNavType() {
        return Component.literal("Dump");
    }
}
