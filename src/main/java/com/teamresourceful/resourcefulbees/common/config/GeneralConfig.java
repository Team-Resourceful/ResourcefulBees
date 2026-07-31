package com.teamresourceful.resourcefulbees.common.config;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulconfig.api.annotations.*;

@Config(
        value = ModConstants.MOD_ID,
        categories = {
                ClientConfig.class,
                BeeConfig.class,
                RecipeConfig.class,
                CentrifugeConfig.class,
                WorldGenConfig.class,
                ApiaryConfig.class,
                EnderBeeconConfig.class,
                HoneycombConfig.class,
                SolidificationConfig.class
        }
)
@ConfigInfo(
        icon = "honeycomb",
        title = "Resourceful Bees",
        description = "Create bees the way you want!",
        links = {
                @ConfigInfo.Link(
                        value = "https://modrinth.com/mod/resourceful-bees",
                        icon = "modrinth",
                        text = "Modrinth"
                ),
                @ConfigInfo.Link(
                        value = "https://www.curseforge.com/minecraft/mc-mods/resourcefulbees",
                        icon = "curseforge",
                        text = "Curseforge"
                ),
                @ConfigInfo.Link(
                        value = "https://github.com/Team-Resourceful/ResourcefulBees",
                        icon = "github",
                        text = "Github"
                )
        }
)
public final class GeneralConfig {

    @ConfigOption.Separator(value = "General")
    @ConfigEntry(
            id = "generateDefaults",
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
            translation = "Enable Dev Bees"
    )
    @Comment(
            value = "Set to true if you want dev bees to generate."
    )
    public static boolean enableDevBees = true;

    @ConfigEntry(
            id = "enableSupporterBees",
            translation = "Enable Supporter Bees"
    )
    @Comment(
            value = "Set to true if you want supporter bees to generate."
    )
    public static boolean enableSupporterBees = true;

    @ConfigEntry(
            id = "showDebugInfo",
            translation = "Show Debug Info"
    )
    @Comment(
            value = "When set to true will display some debug info in console."
    )
    public static boolean showDebugInfo = false;

    @ConfigOption.Separator(value = "Tools")
    @ConfigEntry(
            id = "allowShears",
            translation = "Allow Shears"
    )
    @Comment(
            value = "Set to false if you want the player to only be able to get honeycombs from the beehive using the scraper"
    )
    public static boolean allowShears = true;

    @ConfigEntry(
            id = "smokerDurability",
            translation = "Smoker Durability"
    )
    @Comment(
            value = "Sets the max durability for the smoker"
    )
    @ConfigOption.Range(min = 128, max = 4096)
    public static int smokerDurability = 256;

    @ConfigEntry(
            id = "consumeHiveUpgrade",
            translation = "Consume Hive Upgrade"
    )
    @Comment(
            value = "Set to false if you want hive upgrades to be reusable."
    )
    public static boolean consumeHiveUpgrade = true;



}
