package com.teamresourceful.resourcefulbees.centrifuge.client.components.controlpanels.terminal;

import com.teamresourceful.resourcefulbees.centrifuge.client.components.controlpanels.NavigableControlPanel;
import com.teamresourceful.resourcefulbees.centrifuge.client.screens.CentrifugeTerminalScreen;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.CentrifugeVoidEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.CentrifugeTranslations;
import com.teamresourceful.resourcefulbees.common.lib.enums.ControlPanelTabs;
import com.teamresourceful.resourcefulbees.common.lib.enums.TerminalPanels;
import net.minecraft.network.chat.Component;

public class TerminalVoidNavPanel extends NavigableControlPanel<CentrifugeVoidEntity> {

    public TerminalVoidNavPanel(int x, int y, CentrifugeTerminalScreen screen) {
        super(x, y, screen, screen.centrifugeState().getFilters());
    }

    @Override
    protected void init() {
        createNavPanelDisplayTab(y + 18, ControlPanelTabs.HOME, TerminalPanels.FILTERS_HOME);
    }

    @Override
    protected void updateSelectedEntity() {
        selectedEntity = screen.getBlockEntity(CentrifugeUtils.getFromCollection(navList, screen.selectionIndex()), CentrifugeVoidEntity.class);
    }

    @Override
    protected Component getNavType() {
        return CentrifugeTranslations.FILTER_TAB;
    }
}
