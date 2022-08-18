package com.teamresourceful.resourcefulbees.common.lib.constants;

import com.google.gson.Gson;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.fml.ModList;

import java.nio.file.Path;
import java.text.DecimalFormat;

public final class ModConstants {

    private ModConstants() {
        throw new IllegalAccessError(UTILITY_CLASS);
    }

    public static final String UTILITY_CLASS = "Utility Class";
    public static final Gson GSON = new Gson();
    public static final ResourceLocation SHADES_OF_BEES = new ResourceLocation(ResourcefulBees.MOD_ID, "fifty_shades_of_bees");
    public static final DecimalFormat DECIMAL_PERCENT_FORMAT = new DecimalFormat("#.#%");
    public static final int HONEY_PER_BOTTLE = 250;
    public static final MobCategory BEE_MOB_CATEGORY = MobCategory.create("RESOURCEFUL_BEES", "resourceful_bees", 20, true, false, 128);
    public static final ToolAction SCRAPE_HIVE = ToolAction.get("scrape_hive");
    public static final Path MOD_ROOT = ModList.get().getModFileById(ResourcefulBees.MOD_ID).getFile().getFilePath();
}
