package com.teamresourceful.resourcefulbees.common.lib.constants.translations;

import com.teamresourceful.resourcefulbees.common.lib.tools.Translate;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class ApiaryTranslations {

    private ApiaryTranslations() throws UtilityClassException {
        throw new UtilityClassException();
    }

    @Translate("Honeycomb")
    public static final MutableComponent HONEYCOMB = Component.translatable("item.resourcefulbees.tooltip.honeycomb");

    @Translate("Honeycomb Block")
    public static final MutableComponent HONEYCOMB_BLOCK = Component.translatable("item.resourcefulbees.tooltip.honeycomb_block");

    @Translate("Output Quantity: %s")
    public static final String OUTPUT_QUANTITY = "block.resourcefulbees.apiary.tooltip.output_quantity";

    @Translate("Output Type: %s")
    public static final String OUTPUT_TYPE = "block.resourcefulbees.apiary.tooltip.output_type";

    @Translate("Ticks in Hive: %s")
    public static final String TICKS_HIVE = "gui.resourcefulbees.apiary.bee.ticks_in_hive";

    @Translate("Ticks Left: %s")
    public static final String TICKS_LEFT = "gui.resourcefulbees.apiary.bee.ticks_left";
}
