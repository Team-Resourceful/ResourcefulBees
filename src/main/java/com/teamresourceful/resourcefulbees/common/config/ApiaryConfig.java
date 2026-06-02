package com.teamresourceful.resourcefulbees.common.config;

import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryOutputType;
import com.teamresourceful.resourcefulconfig.api.annotations.*;

@Category(value = "apiary")
@ConfigInfo(icon = "archive")
public final class ApiaryConfig {

    @ConfigEntry(
            id = "tierOneApiaryOutput",
            translation = "Tier One Apiary Output"
    )
    public static ApiaryOutputType tierOneApiaryOutput = ApiaryOutputType.COMB;

    @ConfigEntry(
            id = "tierOneApiaryQuantity",
            translation = "Tier One Apiary Quantity"
    )
    @ConfigOption.Range(min = 1, max = 64)
    public static int tierOneApiaryQuantity = 8;

    @ConfigEntry(
            id = "tierTwoApiaryOutput",
            translation = "Tier Two Apiary Output"
    )
    public static ApiaryOutputType tierTwoApiaryOutput = ApiaryOutputType.COMB;

    @ConfigEntry(
            id = "tierTwoApiaryQuantity",
            translation = "Tier Two Apiary Quantity"
    )
    @ConfigOption.Range(min = 1, max = 64)
    public static int tierTwoApiaryQuantity = 16;

    @ConfigEntry(
            id = "tierThreeApiaryOutput",
            translation = "Tier Three Apiary Output"
    )
    public static ApiaryOutputType tierThreeApiaryOutput = ApiaryOutputType.BLOCK;

    @ConfigEntry(
            id = "tierThreeApiaryQuantity",
            translation = "Tier Three Apiary Quantity"
    )
    @ConfigOption.Range(min = 1, max = 64)
    public static int tierThreeApiaryQuantity = 4;

    @ConfigEntry(
            id = "tierFourApiaryOutput",
            translation = "Tier Four Apiary Output"
    )
    public static ApiaryOutputType tierFourApiaryOutput = ApiaryOutputType.BLOCK;

    @ConfigEntry(
            id = "tierFourApiaryQuantity",
            translation = "Tier Four Apiary Quantity"
    )
    @ConfigOption.Range(min = 1, max = 64)
    public static int tierFourApiaryQuantity = 8;

}
