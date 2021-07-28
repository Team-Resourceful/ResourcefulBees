package com.teamresourceful.resourcefulbees.lib.constants;

import com.google.gson.Gson;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.text.DecimalFormat;

public class ModConstants {

    public static final String UTILITY_CLASS = "Utility Class";
    public static final Gson GSON = new Gson();
    public static final ResourceLocation SHADES_OF_BEES = new ResourceLocation("resourcefulbees:fifty_shades_of_bees");

    private ModConstants() {
        throw new IllegalStateException(UTILITY_CLASS);
    }

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##0.0");
    public static final String NAMESPACE_FORMATTING = TextFormatting.BLUE.toString() + TextFormatting.ITALIC;
    public static final int HONEY_PER_BOTTLE = 250;
    public static final EntityClassification BEE_MOB_CATEGORY = EntityClassification.create("RESOURCEFUL_BEES", "resourceful_bees", 20, true, false, 128);

    public static final String HIVE_UPGRADE_TOOLTIP = "item.resourcefulbees.hive_upgrade.tooltip.info";
}
