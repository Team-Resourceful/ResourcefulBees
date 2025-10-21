package com.teamresourceful.resourcefulbees.common.lib.constants.translations;

import com.teamresourceful.resourcefulbees.common.lib.tools.Translate;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class BeehiveTranslations {

    private BeehiveTranslations() throws UtilityClassException {
        throw new UtilityClassException();
    }

    @Translate("Max Bees: %s")
    public static final String MAX_BEES = "block.resourcefulbees.beehive.tooltip.max_bees";

    @Translate("Max Honeycombs: %s")
    public static final String MAX_COMBS = "block.resourcefulbees.beehive.tooltip.max_combs";

    @Translate("Hive Time Modification: %s%s%%")
    public static final String HIVE_TIME = "block.resourcefulbees.beehive.tooltip.hive_time";

    @Translate("Bees:")
    public static final MutableComponent BEES = Component.translatable("block.resourcefulbees.beehive.tooltip.bees");

    @Translate("Honeycombs:")
    public static final MutableComponent HONEYCOMBS = Component.translatable("block.resourcefulbees.beehive.tooltip.honeycombs");

    @Translate("You can not upgrade this nest with that upgrade.")
    public static final MutableComponent INVALID_UPGRADE = Component.translatable("block.resourcefulbees.beehive.upgrade.invalid");

    @Translate("     NONE")
    public static final MutableComponent NONE_TEXT = Component.translatable("block.resourcefulbees.beehive.tooltip.none");
}
