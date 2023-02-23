package com.teamresourceful.resourcefulbees.common.lib.enums;

import com.teamresourceful.resourcefulbees.common.lib.constants.translations.CentrifugeTranslations;
import net.minecraft.network.chat.Component;

public enum ControlPanelTabs {
    HOME(CentrifugeTranslations.HOME_TAB),
    INPUTS(CentrifugeTranslations.INPUTS_TAB),
    ITEM_OUTPUTS(CentrifugeTranslations.ITEM_OUTPUTS_TAB),
    FLUID_OUTPUTS(CentrifugeTranslations.FLUID_OUTPUTS_TAB),
    FILTERS(CentrifugeTranslations.FILTERS_TAB),
    VOID_EXCESS(CentrifugeTranslations.VOID_EXCESS_TAB),
    PURGE(CentrifugeTranslations.PURGE_TAB),
    INVENTORY(CentrifugeTranslations.INVENTORY_TAB);

    public final Component label;

    ControlPanelTabs(Component label) {
        this.label = label;
    }

    public boolean isType(ControlPanelTabs type) {
        return this == type;
    }
}
