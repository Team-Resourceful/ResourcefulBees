package com.dungeonderps.resourcefulbees.data;

import com.dungeonderps.resourcefulbees.config.ResourcefulBeesConfig;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.dungeonderps.resourcefulbees.ResourcefulBees.LOGGER;
public class RecipeBuilder {

    public static void buildRecipe(String color, String BeeType) {
        try {
            Path recipeFile = Paths.get(ResourcefulBeesConfig.RECIPE_PATH.toString(),"/" + BeeType.toLowerCase() + ".json");
            LOGGER.info(recipeFile.toString());
            FileWriter file = new FileWriter(recipeFile.toFile());
            String honeyCombBlock = String.format("{\"type\": \"minecraft:crafting_shaped\",\"pattern\": [\"CCC\",\"CCC\",\"CCC\"],\"key\": { \"C\": { \"type\": \"forge:nbt\", \"item\": \"resourcefulbees:resourceful_honeycomb\", \"nbt\":{ \"ResourcefulBees\":{\"Color\": \"%1$s\",\"BeeType\":\"%2$s\"}}}},\"result\":{\"item\":\"resourcefulbees:resourceful_honeycomb\",\"nbt\":{\"ResourcefulBees\":{\"Color\":\"%1$s\",\"BeeType\":\"%2$s\"}}}}", color, BeeType);
            LOGGER.info(honeyCombBlock);
            file.write(honeyCombBlock);
            file.close();
        } catch (IOException e) {
            LOGGER.error("Failed to create resourcefulbees recipes.");
        }
    }
}
