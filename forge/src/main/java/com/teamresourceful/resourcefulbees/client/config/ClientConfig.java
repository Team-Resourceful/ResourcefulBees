package com.teamresourceful.resourcefulbees.client.config;

import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraftforge.common.ForgeConfigSpec;

public final class ClientConfig {

    public static ForgeConfigSpec.BooleanValue GENERATE_ENGLISH_LANG;
    public static ForgeConfigSpec.BooleanValue SHOW_TIERS_IN_JEI;

    private ClientConfig() {
        throw new UtilityClassError();
    }

    public static final ForgeConfigSpec CLIENT_CONFIG;

    static {
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

        CLIENT_BUILDER.push("General Options");
        GENERATE_ENGLISH_LANG = CLIENT_BUILDER.comment("\nWhen set to true an en_us.json file will be generated for the bees. [true/false] \n This file will be overwritten every time the mod loads. \n The generated names are based on the bee jsons. \nThis is best used by pack devs as a one-time run.")
                .define("generateEnglishLang", false);
        SHOW_TIERS_IN_JEI = CLIENT_BUILDER.comment("\nWhen set to true JEI will show all the nest tiers in the item list. [true/false]")
                .define("showTiersInJei", true);
        CLIENT_BUILDER.pop();

        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }
}
