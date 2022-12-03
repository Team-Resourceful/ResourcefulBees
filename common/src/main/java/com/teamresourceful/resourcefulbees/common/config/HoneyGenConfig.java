package com.teamresourceful.resourcefulbees.common.config;

import com.teamresourceful.resourcefulconfig.common.annotations.Category;
import com.teamresourceful.resourcefulconfig.common.annotations.Comment;
import com.teamresourceful.resourcefulconfig.common.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.common.annotations.IntRange;
import com.teamresourceful.resourcefulconfig.common.config.EntryType;

@Category(id = "honey_generator", translation = "Honey Generator")
public final class HoneyGenConfig {

    @ConfigEntry(
        id = "honeyDrainAmount",
        type = EntryType.INTEGER,
        translation = "Honey Drain Amount"
    )
    @Comment("Amount of honey consumed in mb/t.")
    @IntRange(min = 1, max = 50)
    public static int honeyDrainAmount = 5;

    @ConfigEntry(
        id = "energyFillAmount",
        type = EntryType.INTEGER,
        translation = "Energy Fill Amount"
    )
    @Comment("Amount of rf/t generated.")
    @IntRange(min = 0, max = 500)
    public static int energyFillAmount = 125;

    @ConfigEntry(
        id = "energyTransferAmount",
        type = EntryType.INTEGER,
        translation = "Energy Transfer Amount"
    )
    @Comment("Amount of energy transferred out of the generator in rf/t.")
    @IntRange(min = 50, max = 500)
    public static int energyTransferAmount = 100;

    @ConfigEntry(
        id = "maxEnergyCapacity",
        type = EntryType.INTEGER,
        translation = "Max Energy Capacity"
    )
    @Comment("Maximum internal energy buffer.")
    @IntRange(min = 10000, max = 1000000)
    public static int maxEnergyCapacity = 100000;

    @ConfigEntry(
        id = "maxTankCapacity",
        type = EntryType.INTEGER,
        translation = "Max Tank Capacity"
    )
    @Comment("Maximum internal honey capacity.")
    @IntRange(min = 1000, max = 100000)
    public static int maxTankCapacity = 10000;
}
