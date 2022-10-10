package com.teamresourceful.resourcefulbees.common.lib.enums;

import net.minecraft.network.chat.Component;

public enum ControlPanelTabs {
    HOME(Component.literal("Home")),
    INPUTS(Component.literal("Inputs")),
    ITEM_OUTPUTS(Component.literal("Item Outputs")),
    FLUID_OUTPUTS(Component.literal("Fluid Outputs")),
    DUMPS(Component.literal("Dumps")),
    VOID_EXCESS(Component.literal("Void Excess")),
    PURGE(Component.literal("Purge Contents"));

    public final Component label;

    ControlPanelTabs(Component label) {
        this.label = label;
    }

    public boolean isType(ControlPanelTabs type) {
        return this == type;
    }
}
