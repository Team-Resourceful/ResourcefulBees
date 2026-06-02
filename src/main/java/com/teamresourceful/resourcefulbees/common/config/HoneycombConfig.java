package com.teamresourceful.resourcefulbees.common.config;

import com.teamresourceful.resourcefulconfig.api.annotations.*;

@Category(value = "honeycomb")
@ConfigInfo(icon = "honeycomb")
public final class HoneycombConfig {

    @ConfigEntry(
        id = "honeycombsEdible",
        translation = "Honeycombs Edible"
    )
    @Comment("Whether all honeycombs should be edible by default or not.")
    public static boolean honeycombsEdible = true;

    @ConfigEntry(
        id = "honeycombHunger",
        translation = "Honeycomb Hunger"
    )
    @Comment("The amount of hunger restored when eating a honeycomb.")
    @ConfigOption.Range(min = 0, max = 8)
    public static int honeycombHunger = 1;

    @ConfigEntry(
        id = "honeycombSaturation",
        translation = "Honeycomb Saturation"
    )
    @Comment("The amount of saturation restored when eating a honeycomb.")
    @ConfigOption.Range(min = 0.0, max = 8.0)
    public static float honeycombSaturation = 0.6f;
}
