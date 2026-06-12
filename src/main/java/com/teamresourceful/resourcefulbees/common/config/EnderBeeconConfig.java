package com.teamresourceful.resourcefulbees.common.config;

import com.teamresourceful.resourcefulconfig.api.annotations.*;

@Category(value = "ender_beecon")
@ConfigInfo(icon = "codesandbox")
public final class EnderBeeconConfig {

    @ConfigEntry(
            id = "beeconCalmingValue",
            translation = "Beecon Calming Value"
    )
    @Comment("Multiplier for the drain rate for the Ender Beecon when the Calming effect is active.")
    @ConfigOption.Range(min = 1, max = 128)
    public static double beeconCalmingValue = 2d;

    @ConfigEntry(
            id = "beeconWaterBreathingValue",
            translation = "Beecon Water Breathing Value"
    )
    @Comment("Multiplier for the drain rate for the Ender Beecon when the Water Breathing effect is active.")
    @ConfigOption.Range(min = 1, max = 128)
    public static double beeconWaterBreathingValue = 1.5d;

    @ConfigEntry(
            id = "beeconFireResistanceValue",
            translation = "Beecon Fire Resistance Value"
    )
    @Comment("Multiplier for the drain rate for the Ender Beecon when the Fire Resistance effect is active.")
    @ConfigOption.Range(min = 1, max = 128)
    public static double beeconFireResistanceValue = 2d;

    @ConfigEntry(
            id = "beeconRegenerationValue",
            translation = "Beecon Regeneration Value"
    )
    @Comment("Multiplier for the drain rate for the Ender Beecon when the Regeneration effect is active.")
    @ConfigOption.Range(min = 1, max = 128)
    public static double beeconRegenerationValue = 2.5d;

    @ConfigEntry(
            id = "beeconRangeMultiplier",
            translation = "Beecon Range Multiplier"
    )
    @Comment("Multiplier for each level of range applied to the Ender Beecon's drain.")
    @ConfigOption.Range(min = 0, max = 2)
    public static double beeconRangeMultiplier = 0.33;

    @ConfigEntry(
            id = "beeconBaseDrain",
            translation = "Beecon Base Drain"
    )
    @Comment("The base drain rate for the Ender Beecon when an effect is active.")
    @ConfigOption.Range(min = 1, max = 128)
    public static int beeconBaseDrain = 1;

    @ConfigEntry(
            id = "beeconPullAmount",
            translation = "Beecon Pull Amount"
    )
    @Comment("The amount of fluid per tick the Ender Beecon can pull from below blocks.")
    @ConfigOption.Range(min = 1, max = 16000)
    public static int beeconPullAmount = 250;
}
