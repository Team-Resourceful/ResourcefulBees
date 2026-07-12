package com.teamresourceful.resourcefulbees.common.lib.constants;

import com.mojang.logging.LogUtils;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.common.ItemAbility;
import org.slf4j.Logger;

public final class ModConstants {

    private ModConstants() throws UtilityClassException {
        throw new UtilityClassException();
    }
    public static final String MOD_ID = "resourcefulbees";
    public static final Logger LOGGER = LogUtils.getLogger();
    //public static final MobCategory BEE_CATEGORY = MobCategory.valueOf("resourcefulbees:bee");




    //public static final MobCategory BEE_CATEGORY = ModUtils.createMobCategory("RESOURCEFUL_BEES", "resourceful_bees", 20, true, false, 128, MobCategory.CREATURE);

    //Actions
    public static final ItemAbility SCRAPE_ACTION = ItemAbility.get("scrape_hive");
//    public static final ItemAbility SHEAR_ACTION = ItemAbility.get("shears_harvest");

    public static void forceInit() {
        // This is to force the class to load.
    }

}
