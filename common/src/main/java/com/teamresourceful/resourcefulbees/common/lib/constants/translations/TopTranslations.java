package com.teamresourceful.resourcefulbees.common.lib.constants.translations;

import com.teamresourceful.resourcefulbees.common.lib.tools.Translate;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class TopTranslations {

    private TopTranslations() {
        throw new UtilityClassError();
    }

    @Translate("Smoke Time:")
    public static final MutableComponent SMOKE_TIME = Component.translatable("gui.resourcefulbees.top.smoke_time");

    @Translate("Tier: %s")
    public static final String TIER = "gui.resourcefulbees.top.tier";

    @Translate("Bees: %s / %s")
    public static final String BEES = "gui.resourcefulbees.top.bees";

    @Translate("Smoked: %s")
    public static final String SMOKED = "gui.resourcefulbees.top.smoked";

    @Translate("Honey Level: %s")
    public static final String HONEY_LEVEL = "gui.resourcefulbees.top.honeylevel";

    @Translate("Flower Pos: %s")
    public static final String FLOWER_POS = "gui.resourcefulbees.top.flower_pos";

    @Translate("Hive Pos: %s")
    public static final String HIVE_POS = "gui.resourcefulbees.top.hive_pos";

    @Translate("Flower Pos: none")
    public static final MutableComponent FLOWER_POS_NONE = Component.translatable("gui.resourcefulbees.top.flower_pos.none");

    @Translate("Hive Pos: none")
    public static final MutableComponent HIVE_POS_NONE = Component.translatable("gui.resourcefulbees.top.hive_pos.none");
}
