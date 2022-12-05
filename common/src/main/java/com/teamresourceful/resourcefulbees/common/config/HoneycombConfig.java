package com.teamresourceful.resourcefulbees.common.config;

import com.teamresourceful.resourcefulconfig.common.annotations.*;
import com.teamresourceful.resourcefulconfig.common.config.EntryType;

@Category(id = "honeycomb", translation = "Honeycomb")
public final class HoneycombConfig {

    @ConfigEntry(
        id = "honeycombsEdible",
        type = EntryType.BOOLEAN,
        translation = "Honeycombs Edible"
    )
    @Comment("Whether all honeycombs should be edible by default or not.")
    public static boolean honeycombsEdible = true;

    @ConfigEntry(
        id = "honeycombHunger",
        type = EntryType.INTEGER,
        translation = "Honeycomb Hunger"
    )
    @Comment("The amount of hunger restored when eating a honeycomb.")
    @IntRange(min = 0, max = 8)
    public static int honeycombHunger = 1;

    @ConfigEntry(
        id = "honeycombSaturation",
        type = EntryType.FLOAT,
        translation = "Honeycomb Saturation"
    )
    @Comment("The amount of saturation restored when eating a honeycomb.")
    @FloatRange(min = 0, max = 8)
    public static float honeycombSaturation = 0.6f;


}
