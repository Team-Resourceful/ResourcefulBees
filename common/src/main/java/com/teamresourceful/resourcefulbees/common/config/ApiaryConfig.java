package com.teamresourceful.resourcefulbees.common.config;

import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryOutputType;
import com.teamresourceful.resourcefulconfig.common.annotations.Category;
import com.teamresourceful.resourcefulconfig.common.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.common.annotations.IntRange;
import com.teamresourceful.resourcefulconfig.common.config.EntryType;

@Category(id = "apiary", translation = "Apiary")
public final class ApiaryConfig {

    @ConfigEntry(
            id = "tierOneApiaryOutput",
            type = EntryType.ENUM,
            translation = "Tier One Apiary Output"
    )
    public static ApiaryOutputType tierOneApiaryOutput = ApiaryOutputType.COMB;

    @ConfigEntry(
            id = "tierOneApiaryQuantity",
            type = EntryType.INTEGER,
            translation = "Tier One Apiary Quantity"
    )
    @IntRange(min = 1, max = 64)
    public static int tierOneApiaryQuantity = 8;

    @ConfigEntry(
            id = "tierTwoApiaryOutput",
            type = EntryType.ENUM,
            translation = "Tier Two Apiary Output"
    )
    public static ApiaryOutputType tierTwoApiaryOutput = ApiaryOutputType.COMB;

    @ConfigEntry(
            id = "tierTwoApiaryQuantity",
            type = EntryType.INTEGER,
            translation = "Tier Two Apiary Quantity"
    )
    @IntRange(min = 1, max = 64)
    public static int tierTwoApiaryQuantity = 16;

    @ConfigEntry(
            id = "tierThreeApiaryOutput",
            type = EntryType.ENUM,
            translation = "Tier Three Apiary Output"
    )
    public static ApiaryOutputType tierThreeApiaryOutput = ApiaryOutputType.BLOCK;

    @ConfigEntry(
            id = "tierThreeApiaryQuantity",
            type = EntryType.INTEGER,
            translation = "Tier Three Apiary Quantity"
    )
    @IntRange(min = 1, max = 64)
    public static int tierThreeApiaryQuantity = 4;

    @ConfigEntry(
            id = "tierFourApiaryOutput",
            type = EntryType.ENUM,
            translation = "Tier Four Apiary Output"
    )
    public static ApiaryOutputType tierFourApiaryOutput = ApiaryOutputType.BLOCK;

    @ConfigEntry(
            id = "tierFourApiaryQuantity",
            type = EntryType.INTEGER,
            translation = "Tier Four Apiary Quantity"
    )
    @IntRange(min = 1, max = 64)
    public static int tierFourApiaryQuantity = 8;

}
