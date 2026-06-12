package com.teamresourceful.resourcefulbees.common.config;

import com.teamresourceful.resourcefulconfig.api.annotations.*;

@Category(value = "bee")
@ConfigInfo(icon = "bee")
public final class BeeConfig {

    @ConfigEntry(
            id = "beesDieFromSting",
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
            translation = "Bee Aura Range"
    )
    @Comment("The default radius that all bees will use for their auras.")
    @ConfigOption.Range(min = 3, max = 20)
    public static int defaultAuraRange = 10;

    @ConfigEntry(
            id = "auraFrequency",
            translation = "Aura Frequency"
    )
    @Comment(
        """
        The frequency, in seconds, that a bee will apply its aura effect.
        Set this to a higher value to improve performance
        """
    )
    @ConfigOption.Range(min = 5, max = 15)
    public static int auraFrequency = 6;



}
