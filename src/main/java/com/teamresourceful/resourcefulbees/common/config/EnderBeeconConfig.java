package com.teamresourceful.resourcefulbees.common.config;

import com.teamresourceful.resourcefulconfig.common.annotations.*;
import com.teamresourceful.resourcefulconfig.common.config.EntryType;
import com.teamresourceful.resourcefulconfig.web.annotations.WebInfo;

@Category(id = "ender_beecon", translation = "Ender Beecon")
@WebInfo(icon = "codesandbox")
public final class EnderBeeconConfig {

    @ConfigEntry(
            id = "beeconCalmingValue",
            type = EntryType.DOUBLE,
            translation = "Beecon Calming Value"
    )
    @Comment("Multiplier for the drain rate for the Ender Beecon when the Calming effect is active.")
    @DoubleRange(min = 1, max = 128)
    public static double beeconCalmingValue = 2d;

    @ConfigEntry(
            id = "beeconWaterBreathingValue",
            type = EntryType.DOUBLE,
            translation = "Beecon Water Breathing Value"
    )
    @Comment("Multiplier for the drain rate for the Ender Beecon when the Water Breathing effect is active.")
    @DoubleRange(min = 1, max = 128)
    public static double beeconWaterBreathingValue = 1.5d;

    @ConfigEntry(
            id = "beeconFireResistanceValue",
            type = EntryType.DOUBLE,
            translation = "Beecon Fire Resistance Value"
    )
    @Comment("Multiplier for the drain rate for the Ender Beecon when the Fire Resistance effect is active.")
    @DoubleRange(min = 1, max = 128)
    public static double beeconFireResistanceValue = 2d;

    @ConfigEntry(
            id = "beeconRegenerationValue",
            type = EntryType.DOUBLE,
            translation = "Beecon Regeneration Value"
    )
    @Comment("Multiplier for the drain rate for the Ender Beecon when the Regeneration effect is active.")
    @DoubleRange(min = 1, max = 128)
    public static double beeconRegenerationValue = 2.5d;

    @ConfigEntry(
            id = "beeconRangeMultiplier",
            type = EntryType.DOUBLE,
            translation = "Beecon Range Multiplier"
    )
    @Comment("Multiplier for each level of range applied to the Ender Beecon's drain.")
    @DoubleRange(min = 0, max = 2)
    public static double beeconRangeMultiplier = 0.33;

    @ConfigEntry(
            id = "beeconBaseDrain",
            type = EntryType.INTEGER,
            translation = "Beecon Base Drain"
    )
    @Comment("The base drain rate for the Ender Beecon when an effect is active.")
    @IntRange(min = 1, max = 128)
    public static int beeconBaseDrain = 1;

    @ConfigEntry(
            id = "beeconPullAmount",
            type = EntryType.INTEGER,
            translation = "Beecon Pull Amount"
    )
    @Comment("The amount of fluid per tick the Ender Beecon can pull from below blocks.")
    @IntRange(min = 1, max = 16000)
    public static int beeconPullAmount = 250;
}
