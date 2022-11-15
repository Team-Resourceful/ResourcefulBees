package com.teamresourceful.resourcefulbees.common.lib;

import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.world.entity.MobCategory;

public final class ModConstants {

    private ModConstants() {
        throw new UtilityClassError();
    }

    public static final MobCategory BEE_MOB_CATEGORY = MobCategory.create("RESOURCEFUL_BEES", "resourceful_bees", 20, true, false, 128);
}
