package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.enums;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;

@OnlyIn(Dist.CLIENT)
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
