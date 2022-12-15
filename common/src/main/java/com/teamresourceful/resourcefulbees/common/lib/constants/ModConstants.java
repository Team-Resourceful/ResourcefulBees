package com.teamresourceful.resourcefulbees.common.lib.constants;

import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.platform.common.util.ModUtils;
import net.minecraft.world.entity.MobCategory;

public final class ModConstants {

    private ModConstants() {
        throw new UtilityClassError();
    }

    public static final String MOD_ID = "resourcefulbees";
    public static final MobCategory BEE_CATEGORY = ModUtils.createMobCategory("RESOURCEFUL_BEES", "resourceful_bees", 20, true, false, 128, MobCategory.CREATURE);

    public static void forceInit() {
        // This is to force the class to load.
    }
}
