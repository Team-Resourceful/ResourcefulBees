package com.teamresourceful.resourcefulbees.common.config;

import com.teamresourceful.resourcefulconfig.common.annotations.Category;
import com.teamresourceful.resourcefulconfig.common.annotations.Comment;
import com.teamresourceful.resourcefulconfig.common.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.common.config.EntryType;

@Category(id = "recipes", translation = "Recipes")
public final class RecipeConfig {

    @ConfigEntry(
            id = "generateDefaultRecipes",
            type = EntryType.BOOLEAN,
            translation = "Generate Default Recipes"
    )
    @Comment(
            value = "Set this to false when you want to overwrite the default bees recipes. [true/false]"
    )
    public static boolean generateDefaultRecipes = true;

    @ConfigEntry(
            id = "honeycombBlockRecipes",
            type = EntryType.BOOLEAN,
            translation = "Honeycomb Block Recipes"
    )
    @Comment(
            value = "Set to false if you don't want the honeycomb block recipes to be auto generated [true/false]"
    )
    public static boolean honeycombBlockRecipes = true;

    @ConfigEntry(
            id = "honeyBlockRecipes",
            type = EntryType.BOOLEAN,
            translation = "Honey Block Recipes"
    )
    @Comment(
            value = "Should honey block recipes be generated? [true/false]"
    )
    public static boolean honeyBlockRecipes = true;

}
