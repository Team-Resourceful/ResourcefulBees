package com.teamresourceful.resourcefulbees.common.config;

import com.teamresourceful.resourcefulconfig.api.annotations.*;

@Category(value = "client")
@ConfigInfo(icon = "settings-2")
public final class ClientConfig {

    @ConfigEntry(
        id = "generateEnglishLang",
        translation = "Generate English Lang"
    )
    @Comment(
        """
        When set to true an en_us.json file will be generated for the bees.
        This file will be overwritten every time the mod loads.
        The generated names are based on the bee jsons.
        This is best used by pack devs as a one-time run.
        """
    )
    public static boolean generateEnglishLang = false;

}
