package com.teamresourceful.resourcefulbees.common.config;

import com.teamresourceful.resourcefulconfig.api.annotations.*;

@Category(value = "worldgen")
@ConfigInfo(icon = "mountain-snow")
public final class WorldGenConfig {

    @ConfigEntry(
        id = "generateBeeNests",
        translation = "Generate Bee Nests"
    )
    @Comment(
        """
        Should bee nests generate in world?
        Note: They will only generate in biomes where bees can spawn
        """
    )
    public static boolean generateBeeNests = true;

    @ConfigEntry(
        id = "hiveMaxBees",
        translation = "Hive Max Bees"
    )
    @Comment("Maximum number of bees in the base tier hive.")
    @ConfigOption.Range(min = 1, max = 4)
    public static int hiveMaxBees = 4;

    @ConfigOption.Separator(value = "Nest Generation Chances")
    @ConfigEntry(
        id = "overworldNestGenerationChance",
        translation = "Overworld Nest Generation Chance"
    )
    @Comment(
        """
        Chance for nest to spawn when generating chunks in overworld category biomes. [1/x]
        A higher value means the nest is less likely to spawn.
        """
    )
    @ConfigOption.Range(min = 4, max = 100)
    public static int overworldNestGenerationChance = 48;

    @ConfigEntry(
        id = "netherNestGenerationChance",
        translation = "Nether Nest Generation Chance"
    )
    @Comment(
        """
        Chance for nest to spawn when generating chunks in nether category biomes. [1/x]
        A higher value means the nest is less likely to spawn.
        """
    )
    @ConfigOption.Range(min = 4, max = 100)
    public static int netherNestGenerationChance = 8;

    @ConfigEntry(
        id = "endNestGenerationChance",
        translation = "End Nest Generation Chance"
    )
    @Comment(
        """
        Chance for nest to spawn when generating chunks in end category biomes. [1/x]
        A higher value means the nest is less likely to spawn.
        """
    )
    @ConfigOption.Range(min = 4, max = 100)
    public static int endNestGenerationChance = 32;





}
