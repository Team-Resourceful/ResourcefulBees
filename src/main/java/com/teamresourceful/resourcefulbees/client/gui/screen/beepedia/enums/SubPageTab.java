package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.enums;

import java.util.Locale;

public enum SubPageTab {
    NONE,
    INFO,
    ENTITY_MUTATION,
    ITEM_MUTATION,
    BLOCK_MUTATION,
    CHILD_BREEDING,
    PARENT_BREEDING,
    BIOMES,
    EXTRA;

    public static SubPageTab get(String name) {
        try {
            return SubPageTab.valueOf(name.toUpperCase(Locale.ENGLISH));
        } catch (Exception e){
            return NONE;
        }
    }
}
