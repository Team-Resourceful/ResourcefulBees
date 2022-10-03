package com.teamresourceful.resourcefulbees.client.components.centrifuge.buttons;

import com.teamresourceful.resourcefulbees.common.lib.enums.ControlPanelTabs;

import java.util.function.Supplier;

public class VoidExcessTab extends DisplayTab {

    public VoidExcessTab(int x, int y, ControlPanelTabs type, Supplier<Boolean> isSelected, Runnable onPress) {
        super(x, y, type, isSelected, onPress);
    }

    @Override
    public int getV(int mouseX, int mouseY) {
        return 0;
    }
}
