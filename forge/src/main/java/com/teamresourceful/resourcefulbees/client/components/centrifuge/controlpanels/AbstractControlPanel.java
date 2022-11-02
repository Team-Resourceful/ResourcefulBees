package com.teamresourceful.resourcefulbees.client.components.centrifuge.controlpanels;

import com.teamresourceful.resourcefulbees.client.components.centrifuge.buttons.DisplayTab;
import com.teamresourceful.resourcefulbees.client.components.centrifuge.buttons.ToggleDisplayTab;
import com.teamresourceful.resourcefulbees.client.screens.centrifuge.BaseCentrifugeScreen;
import com.teamresourceful.resourcefulbees.common.lib.enums.ControlPanelTabs;
import com.teamresourceful.resourcefulbees.common.lib.enums.TerminalPanels;
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
        createDisplayTabWithArrow(y, type, () -> this.screen.controlPanelTab().isType(type),() -> this.screen.setControlPanelTab(type));
    }

    protected final void createNavPanelDisplayTab(int y, ControlPanelTabs type, TerminalPanels infoPanel) {
        createDisplayTabWithArrow(y, type, () -> this.screen.navPanelTab().isType(type), () -> this.screen.switchNavPanelTab(type, infoPanel));
    }

    protected final void createNavPanelVoidExcessTab(int y, Supplier<Boolean> isSelected) {
        addRenderableWidget(new ToggleDisplayTab(x+2, y, ControlPanelTabs.VOID_EXCESS, isSelected, () -> this.screen.centrifugeState().hasDumps(), this.screen::voidExcess, false));
    }

    protected final void createNavPanelPurgeTab(int y) {
        createDisplayTab(y, ControlPanelTabs.PURGE, () -> false, this.screen::purgeContents, false);
    }

    private void createDisplayTabWithArrow(int y, ControlPanelTabs type, Supplier<Boolean> isSelected, Runnable callback) {
        createDisplayTab(y, type, isSelected, callback, true);
    }

    private void createDisplayTab(int y, ControlPanelTabs type, Supplier<Boolean> isSelected, Runnable callback, boolean showArrow) {
        addRenderableWidget(new DisplayTab(x+2, y, type, isSelected, () -> true, callback, showArrow));
    }
}
