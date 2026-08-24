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
    @Comment("Maximum number of bees to spawn in a generated nest")
    @ConfigOption.Range(min = 0, max = 2)
    public static int hiveMaxBees = 2;

    @ConfigOption.Separator(value = "Gold Flower Bonemeal Settings")
    @ConfigEntry(
            id = "goldFlowerBonemealTries",
            translation = "Gold Flower Bonemeal Tries"
    )
    @Comment(
            """
            When bonemealing tagged blocks, there's a default 1/5 chance to spawn a patch
            of Gold Flowers. This value determines how many possible flower placements can generate.
            A higher value means more will spawn.
            """
    )
    @ConfigOption.Range(min = 1, max = 16)
    public static int goldFlowerBonemealTries = 8;

    @ConfigEntry(
            id = "goldFlowerBonemealXZSpread",
            translation = "Gold Flower Bonemeal X/Z Spread"
    )
    @Comment(
            """
            When bonemealing tagged blocks, there's a default 1/5 chance to spawn a patch
            of Gold Flowers. This value determines the x and z spread.
            The default value, 4, sets the range as a 9x9 square centered around the
            bonemealed position.
            """
    )
    @ConfigOption.Range(min = 1, max = 8)
    public static int goldFlowerBonemealXZSpread = 4;

    @ConfigEntry(
            id = "goldFlowerBonemealChance",
            translation = "Gold Flower Bonemeal Chance"
    )
    @Comment(
            """
            When bonemealing tagged blocks, there's a default 1/5 chance to spawn a patch
            of Gold Flowers. This value determines the chance as 1/x.
            """
    )
    @ConfigOption.Range(min = 1, max = 100)
    public static int goldFlowerBonemealChance = 5;
}
