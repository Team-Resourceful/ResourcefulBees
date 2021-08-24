package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.enums;

import java.util.Locale;

public enum SubPageTypes {
    INFO,
    COMBAT,
    MUTATIONS,
    TRAITS,
    HONEYCOMB,
    SPAWNING,
    BREEDING,
    BEES;

    public static SubPageTypes get(String name) {
        try {
            return SubPageTypes.valueOf(name.toUpperCase(Locale.ENGLISH));
        }catch (Exception e){
            return INFO;
        }
    }
}
