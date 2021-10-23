package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.enums;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;

@OnlyIn(Dist.CLIENT)
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
