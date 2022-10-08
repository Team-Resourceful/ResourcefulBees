package com.teamresourceful.resourcefulbees.client.components.centrifuge.controlpanels.terminal;

import com.teamresourceful.resourcefulbees.client.components.centrifuge.controlpanels.NavigableControlPanel;
import com.teamresourceful.resourcefulbees.common.lib.enums.ControlPanelTabs;
import com.teamresourceful.resourcefulbees.common.lib.enums.TerminalPanels;
import com.teamresourceful.resourcefulbees.client.screens.centrifuge.CentrifugeTerminalScreen;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeItemOutputEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeUtils;
import net.minecraft.network.chat.Component;

public class TerminalItemOutputNavPanel extends NavigableControlPanel<CentrifugeItemOutputEntity> {

    public TerminalItemOutputNavPanel(int x, int y, CentrifugeTerminalScreen screen) {
        super(x, y, screen, screen.centrifugeState().getItemOutputs());
    }

    @Override
    protected void init() {
        createNavPanelDisplayTab(y+18, ControlPanelTabs.HOME, TerminalPanels.ITEM_OUTPUTS_HOME);
        createNavPanelVoidExcessTab(y+32, () -> selectedEntity != null && selectedEntity.voidsExcess());
        createNavPanelPurgeTab(y+46);
    }



    @Override
    protected void updateSelectedEntity() {
        selectedEntity = screen.getBlockEntity(CentrifugeUtils.getFromCollection(navList, screen.selectionIndex()), CentrifugeItemOutputEntity.class);
    }

    @Override
    protected Component getNavType() {
        return Component.literal("Output");
    }
}
