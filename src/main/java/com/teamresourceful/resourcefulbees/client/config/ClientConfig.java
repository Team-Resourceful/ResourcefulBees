package com.teamresourceful.resourcefulbees.client.config;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {

    public static ForgeConfigSpec.BooleanValue GENERATE_ENGLISH_LANG;
    public static ForgeConfigSpec.BooleanValue SHOW_DEBUG_INFO;

    private ClientConfig() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final ForgeConfigSpec CLIENT_CONFIG;

    static {
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

        CLIENT_BUILDER.push("General Options");
        GENERATE_ENGLISH_LANG = CLIENT_BUILDER.comment("\nWhen set to true an en_us.json file will be generated for the bees. [true/false] \n This file will be overwritten every time the mod loads. \n The generated names are based on the bee jsons. \nThis is best used by pack devs as a one-time run.")
                .define("generateEnglishLang", false);
        SHOW_DEBUG_INFO = CLIENT_BUILDER.comment("\nWhen set to true will display some debug info in console. [true/false]")
                .define("showDebugInfo", false);
        CLIENT_BUILDER.pop();

        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }
}
