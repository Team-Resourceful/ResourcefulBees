package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.enums;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;

@OnlyIn(Dist.CLIENT)
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
