package com.teamresourceful.resourcefulbees.common.config;

import com.teamresourceful.resourcefulconfig.api.annotations.*;

@Category(value = "centrifuge")
@ConfigInfo(icon = "settings")
public final class CentrifugeConfig {

    @ConfigEntry(
        id = "defaultCentrifugeRecipeTime",
        translation = "Default Centrifuge Recipe Time"
    )
    @Comment(
        value = "Default recipe time for recipes where a time value is not defined."
    )
    @ConfigOption.Range(min = 100, max = 2400)
    public static int defaultCentrifugeRecipeTime = 200;

    @ConfigEntry(
        id = "centrifugeRfPerTick",
        translation = "Centrifuge RF Per Tick"
    )
    @Comment(
        value = "RF/t consumed by the centrifuge when processing recipes."
    )
    @ConfigOption.Range(min = 2, max = 1000)
    public static int centrifugeRfPerTick = 10;
}
