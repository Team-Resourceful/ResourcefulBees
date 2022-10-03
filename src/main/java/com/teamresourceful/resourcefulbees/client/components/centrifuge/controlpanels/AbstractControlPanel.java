package com.teamresourceful.resourcefulbees.client.components.centrifuge.controlpanels;

import com.teamresourceful.resourcefulbees.common.lib.enums.ControlPanelTabs;
import com.teamresourceful.resourcefulbees.client.components.centrifuge.buttons.DisplayTab;
import com.teamresourceful.resourcefulbees.client.components.centrifuge.buttons.VoidExcessTab;
import com.teamresourceful.resourcefulbees.common.lib.enums.TerminalPanels;
import com.teamresourceful.resourcefulbees.client.screens.centrifuge.BaseCentrifugeScreen;
import com.teamresourceful.resourcefullib.client.components.ParentWidget;

import java.util.function.Supplier;

public abstract class AbstractControlPanel extends ParentWidget {

    protected final BaseCentrifugeScreen<?> screen;

    protected AbstractControlPanel(int x, int y, BaseCentrifugeScreen<?> screen) {
        super(x, y);
        this.screen = screen;
        init();
    }

    protected final void createControlPanelDisplayTab(int y, ControlPanelTabs type) {
        createDisplayTab(y, type, () -> this.screen.controlPanelTab().isType(type),() -> this.screen.setControlPanelTab(type));
    }

    protected final void createNavPanelDisplayTab(int y, ControlPanelTabs type, TerminalPanels infoPanel) {
        createDisplayTab(y, type, () -> this.screen.navPanelTab().isType(type), () -> this.screen.switchNavPanelTab(type, infoPanel));
    }

    protected final void createNavPanelVoidExcessTab(int y, Supplier<Boolean> isSelected) {
        addRenderableWidget(new VoidExcessTab(x+2, y, ControlPanelTabs.VOID_EXCESS, isSelected, this.screen::voidExcess));
    }

    private void createDisplayTab(int y, ControlPanelTabs type, Supplier<Boolean> isSelected, Runnable callback) {
        addRenderableWidget(new DisplayTab(x+2, y, type, isSelected, callback));
    }
}
