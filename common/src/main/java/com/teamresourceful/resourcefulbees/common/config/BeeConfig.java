package com.teamresourceful.resourcefulbees.common.config;

import com.teamresourceful.resourcefulconfig.common.annotations.Category;
import com.teamresourceful.resourcefulconfig.common.annotations.Comment;
import com.teamresourceful.resourcefulconfig.common.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.common.annotations.IntRange;
import com.teamresourceful.resourcefulconfig.common.config.EntryType;

@Category(id = "bee", translation = "Bee")
public final class BeeConfig {

    @ConfigEntry(
            id = "beesDieFromSting",
            type = EntryType.BOOLEAN,
            translation = "Bees Die From Sting"
    )
    @Comment(
        """
        Should bees die from stinging?
        Note: Bees will continue to attack until they are no longer angry!
        """
    )
    public static boolean beesDieFromSting = true;

    @ConfigEntry(
            id = "beesInflictPoison",
            type = EntryType.BOOLEAN,
            translation = "Bees Inflict Poison"
    )
    @Comment(
        """
        Should bees inflict poison damage?
        Note: Poison is only inflicted if a bee has not been given a trait with a special damage output.
        Set to false if you want to configure bees individually.
        """
    )
    public static boolean beesInflictPoison = true;

    @ConfigEntry(
            id = "beesDieInVoid",
            type = EntryType.BOOLEAN,
            translation = "Bees Die In Void"
    )
    @Comment(
        """
        Should bees die when when they are below the world.
        Note: If false, bees will get stuck just below world and not move. **May not be useful with new AI**
        """
    )
    public static boolean beesDieInVoid = true;

    @ConfigEntry(
            id = "use_experimental_manual_mode",
            type = EntryType.BOOLEAN,
            translation = "Use Experimental Manual Mode"
    )
    @Comment(
        """
        This is an experimental setting. Using this setting means bees will need to be told by the player which flower and hive to use.
        Bees will not scan surroundings for flowers or hives and will instead go to their designated spot until changed.
        WARNING: For now, this will prevent bees from having their wander goal attached which effectively makes them dumb (seriously, they'll just hover in one spot), however it would also significantly improve performance until pathfinding can be optimized.
        """
    )
    public static boolean manualMode = false;

    @ConfigEntry(
            id = "beeAuraRange",
            type = EntryType.INTEGER,
            translation = "Bee Aura Range"
    )
    @Comment("The default radius that all bees will use for their auras.")
    @IntRange(min = 3, max = 20)
    public static int defaultAuraRange = 10;

    @ConfigEntry(
            id = "auraFrequency",
            type = EntryType.INTEGER,
            translation = "Aura Frequency"
    )
    @Comment(
        """
        The frequency, in seconds, that a bee will apply its aura effect.
        Set this to a higher value to improve performance
        """
    )
    @IntRange(min = 5, max = 15)
    public static int auraFrequency = 6;



}
