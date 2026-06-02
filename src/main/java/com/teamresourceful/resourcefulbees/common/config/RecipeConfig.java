package com.teamresourceful.resourcefulbees.common.config;

import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.Comment;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;

@Category(value = "recipes")
@ConfigInfo(icon = "clipboard-list")
public final class RecipeConfig {

    @ConfigEntry(
            id = "generateDefaultRecipes",
            translation = "Generate Default Recipes"
    )
    @Comment(
            value = "Set this to false when you want to overwrite the default bees recipes. [true/false]"
    )
    public static boolean generateDefaultRecipes = true;

    @ConfigEntry(
            id = "honeycombBlockRecipes",
            translation = "Honeycomb Block Recipes"
    )
    @Comment(
            value = "Set to false if you don't want the honeycomb block recipes to be auto generated [true/false]"
    )
    public static boolean honeycombBlockRecipes = true;

    @ConfigEntry(
            id = "honeyBlockRecipes",
            translation = "Honey Block Recipes"
    )
    @Comment(
            value = "Should honey block recipes be generated? [true/false]"
    )
    public static boolean honeyBlockRecipes = true;

}
