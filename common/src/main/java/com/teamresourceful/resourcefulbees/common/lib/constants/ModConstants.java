package com.teamresourceful.resourcefulbees.common.lib.constants;

import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.platform.common.item.ItemAction;
import com.teamresourceful.resourcefulbees.platform.common.util.ModUtils;
import net.minecraft.world.entity.MobCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ModConstants {

    private ModConstants() {
        throw new UtilityClassError();
    }

    public static final String MOD_ID = "resourcefulbees";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final MobCategory BEE_CATEGORY = ModUtils.createMobCategory("RESOURCEFUL_BEES", "resourceful_bees", 20, true, false, 128, MobCategory.CREATURE);

    //Actions
    public static final ItemAction SCRAPE_ACTION = ItemAction.get("scrape_hive");
    public static final ItemAction SHEAR_ACTION = ItemAction.get("shears_harvest");

    public static void forceInit() {
        // This is to force the class to load.
    }
}
