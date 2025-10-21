package com.teamresourceful.resourcefulbees.common.config;

import com.teamresourceful.resourcefulconfig.common.annotations.*;
import com.teamresourceful.resourcefulconfig.common.config.EntryType;
import com.teamresourceful.resourcefulconfig.web.annotations.WebInfo;

@Category(id = "centrifuge", translation = "Centrifuge")
@WebInfo(icon = "settings")
public final class CentrifugeConfig {

    @ConfigEntry(
        id = "defaultCentrifugeRecipeTime",
        type = EntryType.INTEGER,
        translation = "Default Centrifuge Recipe Time"
    )
    @Comment(
        value = "Default recipe time for recipes where a time value is not defined."
    )
    @IntRange(min = 100, max = 2400)
    public static int defaultCentrifugeRecipeTime = 200;

    @ConfigEntry(
        id = "centrifugeRfPerTick",
        type = EntryType.INTEGER,
        translation = "Centrifuge RF Per Tick"
    )
    @Comment(
        value = "RF/t consumed by the centrifuge when processing recipes."
    )
    @IntRange(min = 2, max = 1000)
    public static int centrifugeRfPerTick = 10;

    @ConfigEntry(
            id = "recipeTimeExponent",
            type = EntryType.DOUBLE,
            translation = "Recipe Time Exponent"
    )
    @Comment(
            value = """
                    The recipe time modifier is calculated as:
                    numGearboxes^(1-x/numInputs)
                    Larger values for X means gearboxes have more impact on the time reduction.
                    """
    )
    @DoubleRange(min = 0, max = 1)
    public static double recipeTimeExponent = 0.1;

    @ConfigEntry(
            id = "gearboxPowerExponent",
            type = EntryType.DOUBLE,
            translation = "Gearbox Power Exponent"
    )
    @Comment(
            value = """
                    The gearbox power modifier is calculated as:
                    1 + (x * 1.1^numGearboxes)
                    Larger values for X means gearboxes have more impact on the power required to process a recipe.
                    The calculated modifier is multiplied by the processor power modifier before being applied to the recipe rf/t.
                    """
    )
    @DoubleRange(min = 0, max = 1)
    public static double gearboxPowerExponent = 0.2;

    @ConfigEntry(
            id = "cpuPowerExponent",
            type = EntryType.DOUBLE,
            translation = "Processor Power Exponent"
    )
    @Comment(
            value = """
                    The processor power modifier is calculated as:
                    1 + (x * 1.1^numProcessors)
                    Larger values for X means processors have more impact on the power required to process a recipe.
                    The calculated modifier is multiplied by the gearbox power modifier before being applied to the recipe rf/t.
                    """
    )
    @DoubleRange(min = 0, max = 1)
    public static double cpuPowerExponent = 0.4;

}
