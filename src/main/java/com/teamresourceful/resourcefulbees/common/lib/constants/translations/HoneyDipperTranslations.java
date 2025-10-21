package com.teamresourceful.resourcefulbees.common.lib.constants.translations;

import com.teamresourceful.resourcefulbees.common.lib.tools.Translate;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class HoneyDipperTranslations {

    private HoneyDipperTranslations() throws UtilityClassException {
        throw new UtilityClassException();
    }

    @Translate("Hive position for [%s] has been set to %s")
    public static final String HIVE_SET = "items.resourcefulbees.honey_dipper.hive_set";

    @Translate("Flower position for [%s] has been set to %s")
    public static final String FLOWER_SET = "items.resourcefulbees.honey_dipper.flower_set";

    @Translate("Fake Golden Flower Position for [%s] has been set to %s")
    public static final String FAKE_FLOWER_SET = "items.resourcefulbees.honey_dipper.fake_flower_set";

    @Translate("Bee Selection Cleared!")
    public static final MutableComponent SELECTION_CLEARED = Component.translatable("items.resourcefulbees.honey_dipper.cleared");

    @Translate("[%s] has been selected!")
    public static final String BEE_SET = "items.resourcefulbees.honey_dipper.bee_set";
}
