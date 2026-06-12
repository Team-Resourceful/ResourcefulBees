package com.teamresourceful.resourcefulbees.common.config;

import com.teamresourceful.resourcefulconfig.api.annotations.*;

@Category(value = "solidification")
@ConfigInfo(icon = "boxes")
public final class SolidificationConfig {

    @ConfigEntry(
        id = "honeyProcessTime",
        translation = "Honey Process Time"
    )
    @Comment(
        value = "Amount of time in ticks required to finish processing a honey bottle."
    )
    @ConfigOption.Range(min = 0, max = 2400)
    public static int honeyProcessTime = 5;

    @ConfigEntry(
        id = "solidificationTimeMultiplier",
        translation = "Solidification Time Multiplier"
    )
    @Comment(
        value = """
                Multiplier to the amount of ticks needed to process honey into honey blocks in the congealer.
                This value is multiplying the honeyProcessTime.
                """
    )
    @ConfigOption.Range(min = 1, max = 16)
    public static int solidificationTimeMultiplier = 4;

}
