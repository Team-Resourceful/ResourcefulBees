package com.teamresourceful.resourcefulbees.client.components.centrifuge.buttons;

import com.teamresourceful.resourcefulbees.common.lib.enums.ControlPanelTabs;

import java.util.function.Supplier;

public class ToggleDisplayTab extends DisplayTab {

    public ToggleDisplayTab(int x, int y, ControlPanelTabs type, Supplier<Boolean> isSelected, Supplier<Boolean> isEnabled, Runnable onPress, boolean showArrow) {
        super(x, y, type, isSelected, isEnabled, onPress, showArrow);
    }

    public ToggleDisplayTab(int x, int y, ControlPanelTabs type, Supplier<Boolean> isSelected, Runnable onPress) {
        super(x, y, type, isSelected, onPress);
    }

    @Override
    public void onPress() {
       if (isEnabled()) onPress.run();
    }
}
