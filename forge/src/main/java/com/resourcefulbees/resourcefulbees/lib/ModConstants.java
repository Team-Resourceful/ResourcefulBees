package com.resourcefulbees.resourcefulbees.lib;

import net.minecraft.ChatFormatting;

import java.text.DecimalFormat;

public class ModConstants {

    public static final String UTILITY_CLASS = "Utility Class";

    private ModConstants() {
        throw new IllegalStateException(UTILITY_CLASS);
    }

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##0.0");
    public static final String NAMESPACE_FORMATTING = ChatFormatting.BLUE.toString() + ChatFormatting.ITALIC;
    public static final int HONEY_PER_BOTTLE = 250;

    public static final String HIVE_UPGRADE_TOOLTIP = "item.resourcefulbees.hive_upgrade.tooltip.info";
}
