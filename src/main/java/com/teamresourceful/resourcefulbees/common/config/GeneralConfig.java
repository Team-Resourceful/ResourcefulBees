package com.teamresourceful.resourcefulbees.common.config;

import com.teamresourceful.resourcefulconfig.common.annotations.*;
import com.teamresourceful.resourcefulconfig.common.config.EntryType;
import com.teamresourceful.resourcefulconfig.web.annotations.Link;
import com.teamresourceful.resourcefulconfig.web.annotations.WebInfo;

@Config("resourcefulbees/config")
@WebInfo(
        icon = "honeycomb",
        title = "Resourceful Bees",
        description = "Create bees the way you want!",
        color = "#EF5552",
        links = {
                @Link(
                        value = "https://modrinth.com/mod/resourceful-bees",
                        icon = "modrinth",
                        title = "Modrinth"
                ),
                @Link(
                        value = "https://www.curseforge.com/minecraft/mc-mods/resourcefulbees",
                        icon = "curseforge",
                        title = "Curseforge"
                ),
                @Link(
                        value = "https://github.com/Team-Resourceful/ResourcefulBees",
                        icon = "github",
                        title = "Github"
                )
        }
)
public final class GeneralConfig {

    @ConfigSeparator(translation = "General")
    @ConfigEntry(
            id = "generateDefaults",
            type = EntryType.BOOLEAN,
            translation = "Generate Defaults"
    )
    @Comment(
            value = """
        Set this to false when you want to overwrite the default bee files.
        This should be run at least once for initial generation.
        """
    )
    public static boolean generateDefaults = true;

    @ConfigEntry(
            id = "enableDevBees",
            type = EntryType.BOOLEAN,
            translation = "Enable Dev Bees"
    )
    @Comment(
            value = "Set to true if you want dev bees to generate."
    )
    public static boolean enableDevBees = true;

    @ConfigEntry(
            id = "enableSupporterBees",
            type = EntryType.BOOLEAN,
            translation = "Enable Supporter Bees"
    )
    @Comment(
            value = "Set to true if you want supporter bees to generate."
    )
    public static boolean enableSupporterBees = true;

    @ConfigEntry(
            id = "showDebugInfo",
            type = EntryType.BOOLEAN,
            translation = "Show Debug Info"
    )
    @Comment(
            value = "When set to true will display some debug info in console."
    )
    public static boolean showDebugInfo = false;

    @ConfigSeparator(translation = "Tools")
    @ConfigEntry(
            id = "allowShears",
            type = EntryType.BOOLEAN,
            translation = "Allow Shears"
    )
    @Comment(
            value = "Set to false if you want the player to only be able to get honeycombs from the beehive using the scraper"
    )
    public static boolean allowShears = true;

    @ConfigEntry(
            id = "smokerDurability",
            type = EntryType.INTEGER,
            translation = "Smoker Durability"
    )
    @Comment(
            value = "Sets the max durability for the smoker"
    )
    public static int smokerDurability = 1000;

    @ConfigEntry(
            id = "consumeHiveUpgrade",
            type = EntryType.BOOLEAN,
            translation = "Consume Hive Upgrade"
    )
    @Comment(
            value = "Set to false if you want hive upgrades to be reusable."
    )
    @IntRange(min = 100, max = 5000)
    public static boolean consumeHiveUpgrade = true;

    @InlineCategory
    public static ClientConfig clientConfig;

    @InlineCategory
    public static BeeConfig beeConfig;

    @InlineCategory
    public static RecipeConfig recipeConfig;

    @InlineCategory
    public static CentrifugeConfig centrifugeConfig;

    @InlineCategory
    public static WorldGenConfig worldGenConfig;

    @InlineCategory
    public static ApiaryConfig apiaryConfig;

    @InlineCategory
    public static EnderBeeconConfig enderBeeconConfig;

    @InlineCategory
    public static HoneycombConfig honeycombConfig;

    @InlineCategory
    public static HoneyGenConfig honeyGenConfig;

    @InlineCategory
    public static SolidficationConfig solidficationConfig;

}
