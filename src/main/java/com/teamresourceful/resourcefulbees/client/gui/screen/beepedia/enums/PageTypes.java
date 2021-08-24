package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.enums;

import java.util.Locale;

public enum PageTypes {
    HOME,
    HELP,
    BEES,
    TRAITS,
    HONEY,
    COMBS,
    COLLECTED;

    public static PageTypes get(String name) {
        try {
            return PageTypes.valueOf(name.toUpperCase(Locale.ENGLISH));
        }catch (Exception e){
            return HOME;
        }
    }
}
