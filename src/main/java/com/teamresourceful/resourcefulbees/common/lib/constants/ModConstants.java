package com.teamresourceful.resourcefulbees.common.lib.constants;

import com.mojang.logging.LogUtils;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.neoforged.neoforge.common.ItemAbility;
import org.slf4j.Logger;

import java.util.UUID;

public final class ModConstants {

    private ModConstants() throws UtilityClassException {
        throw new UtilityClassException();
    }
    public static final String MOD_ID = "resourcefulbees";
    public static final Logger LOGGER = LogUtils.getLogger();
    // save for epic
    private static final UUID DEV_UUID = UUID.fromString("380df991-f603-344c-a090-369bad2a924a");
    //Actions
    public static final ItemAbility SCRAPE_ACTION = ItemAbility.get("scrape_hive");
}
