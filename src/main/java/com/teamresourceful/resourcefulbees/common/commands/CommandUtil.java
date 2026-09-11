package com.teamresourceful.resourcefulbees.common.commands;

import com.google.gson.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.trading.VillagerTrade;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CommandUtil {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    static final Path GENERATED_DATA_PATH = FMLPaths.CONFIGDIR.get()
            .resolve("resourcefulbees")
            .resolve("resources")
            .resolve("data")
            .resolve("resourcefulbees");

    // -------------------------------------------------------------------------
    // Serialization
    // -------------------------------------------------------------------------
    static void writeVillagerTrade(RegistryOps<JsonElement> registryOps, VillagerTrade trade, Path path) {
        VillagerTrade.CODEC.encodeStart(registryOps, trade).resultOrPartial(error -> {
            throw new IllegalStateException("Failed to encode villager trade " + path.toAbsolutePath() + ": " + error);
        }).ifPresent(json -> writeJsonFile(json.getAsJsonObject(), path));
    }

    static void writeTradeTag(JsonArray values, Path path) {
        JsonObject tag = new JsonObject();
        tag.addProperty("replace", false);
        tag.add("values", values);
        writeJsonFile(tag, path);
    }

    static void writeJsonFile(JsonObject jsonObject, Path path) {
        try {
            Path parent = path.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                GSON.toJson(jsonObject, writer);
            }
        } catch (IOException exception) {
            throw new RuntimeException("Failed to write JSON file: " + path.toAbsolutePath(), exception);
        }
    }

    // -------------------------------------------------------------------------
    // Utilities
    // -------------------------------------------------------------------------
    static JsonObject createShapedRecipe(String[] pattern, char symbol, Item ingredient, Item result, int count) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "minecraft:crafting_shaped");
        json.addProperty("category", "misc");

        JsonArray patternJson = new JsonArray();

        for (String row : pattern) {
            patternJson.add(row);
        }

        json.add("pattern", patternJson);

        JsonObject key = new JsonObject();
        key.addProperty(String.valueOf(symbol), itemId(ingredient));
        json.add("key", key);

        json.add("result", createResult(result, count));

        return json;
    }

    static JsonObject createShapelessRecipe(List<Item> ingredients, Item result, int count) {
        JsonObject json = new JsonObject();

        json.addProperty("type", "minecraft:crafting_shapeless");
        json.addProperty("category", "misc");

        JsonArray ingredientsJson = new JsonArray();

        for (Item ingredient : ingredients) {
            ingredientsJson.add(itemId(ingredient));
        }

        json.add("ingredients", ingredientsJson);
        json.add("result", createResult(result, count));

        return json;
    }

    private static JsonObject createResult(Item item, int count) {
        JsonObject result = new JsonObject();

        result.addProperty("id", itemId(item));

        if (count != 1) {
            result.addProperty("count", count);
        }

        return result;
    }

    private static String itemId(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).toString();
    }

    static String itemPath(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).getPath();
    }
}
