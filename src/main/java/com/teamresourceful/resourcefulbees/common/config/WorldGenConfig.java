package com.teamresourceful.resourcefulbees.common.config;

import com.teamresourceful.resourcefulconfig.common.annotations.*;
import com.teamresourceful.resourcefulconfig.common.config.EntryType;
import com.teamresourceful.resourcefulconfig.web.annotations.WebInfo;

@Category(id = "worldgen", translation = "World Generation")
@WebInfo(icon = "mountain-snow")
public final class WorldGenConfig {

    @ConfigEntry(
        id = "generateBeeNests",
        type = EntryType.BOOLEAN,
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
        type = EntryType.INTEGER,
        translation = "Hive Max Bees"
    )
    @Comment("Maximum number of bees in the base tier hive.")
    @IntRange(min = 1, max = 4)
    public static int hiveMaxBees = 4;

    @ConfigSeparator(translation = "Nest Generation Chances")
    @ConfigEntry(
        id = "overworldNestGenerationChance",
        type = EntryType.INTEGER,
        translation = "Overworld Nest Generation Chance"
    )
    @Comment(
        """
        Chance for nest to spawn when generating chunks in overworld category biomes. [1/x]
        A higher value means the nest is less likely to spawn.
        """
    )
    @IntRange(min = 4, max = 100)
    public static int overworldNestGenerationChance = 48;

    @ConfigEntry(
        id = "netherNestGenerationChance",
        type = EntryType.INTEGER,
        translation = "Nether Nest Generation Chance"
    )
    @Comment(
        """
        Chance for nest to spawn when generating chunks in nether category biomes. [1/x]
        A higher value means the nest is less likely to spawn.
        """
    )
    @IntRange(min = 4, max = 100)
    public static int netherNestGenerationChance = 8;

    @ConfigEntry(
        id = "endNestGenerationChance",
        type = EntryType.INTEGER,
        translation = "End Nest Generation Chance"
    )
    @Comment(
        """
        Chance for nest to spawn when generating chunks in end category biomes. [1/x]
        A higher value means the nest is less likely to spawn.
        """
    )
    @IntRange(min = 4, max = 100)
    public static int endNestGenerationChance = 32;





}
