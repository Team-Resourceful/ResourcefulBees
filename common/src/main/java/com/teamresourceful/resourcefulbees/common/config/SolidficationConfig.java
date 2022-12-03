package com.teamresourceful.resourcefulbees.common.config;

import com.teamresourceful.resourcefulconfig.common.annotations.Category;
import com.teamresourceful.resourcefulconfig.common.annotations.Comment;
import com.teamresourceful.resourcefulconfig.common.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.common.annotations.IntRange;
import com.teamresourceful.resourcefulconfig.common.config.EntryType;

@Category(id = "solidfication", translation = "Solidification Chamber")
public final class SolidficationConfig {

    @ConfigEntry(
        id = "honeyProcessTime",
        type = EntryType.INTEGER,
        translation = "Honey Process Time"
    )
    @Comment(
        value = "Amount of time in ticks required to finish processing a honey bottle."
    )
    @IntRange(min = 0, max = 2400)
    public static int honeyProcessTime = 5;

    @ConfigEntry(
        id = "solidficationTimeMultiplier",
        type = EntryType.INTEGER,
        translation = "Solidification Time Multiplier"
    )
    @Comment(
        value = """
                Multiplier to the amount of ticks needed to process honey into honey blocks in the congealer.
                This value is multiplying the honeyProcessTime.
                """
    )
    @IntRange(min = 1, max = 16)
    public static int solidficationTimeMultiplier = 4;

}
