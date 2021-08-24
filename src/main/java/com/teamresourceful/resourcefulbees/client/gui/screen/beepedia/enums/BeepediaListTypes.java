package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.enums;

import java.util.Locale;

public enum BeepediaListTypes {
    BEES,
    TRAITS,
    HONEY,
    COMBS;

    public static BeepediaListTypes get(String name) {
        try {
            return BeepediaListTypes.valueOf(name.toUpperCase(Locale.ENGLISH));
        }catch (Exception e){
            return BEES;
        }
    }
}
