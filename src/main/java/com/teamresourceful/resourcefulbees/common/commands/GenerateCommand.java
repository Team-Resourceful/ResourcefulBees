package com.teamresourceful.resourcefulbees.common.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.client.data.LangGenerator;
import com.teamresourceful.resourcefulbees.common.data.RecipeBuilder;
import com.teamresourceful.resourcefulbees.common.recipes.breeder.BreederRecipe;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class GenerateCommand {

    private GenerateCommand() throws UtilityClassException {
        throw new UtilityClassException();
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("generate")
                .then(registerRecipeCommand())
                .then(registerLangCommand());
    }

    private static ArgumentBuilder<CommandSourceStack, ?> registerLangCommand() {
        return Commands.literal("lang")
                .requires(stack -> stack.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .executes(LangGenerator::generateEnglishLang);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> registerRecipeCommand() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("recipe");
        builder.then(Commands.literal("breeder")
                .requires(stack -> stack.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .executes(GenerateCommand::generateBreederRecipes));

        return builder;
    }

    private static int generateBreederRecipes(CommandContext<CommandSourceStack> context) {

        Path recipePath = FMLPaths.CONFIGDIR.get()
                .resolve("resourcefulbees")
                .resolve("resources")
                .resolve("data")
                .resolve("resourcefulbees")
                .resolve("recipe")
                .resolve("breeder");

        RegistryOps<JsonElement> registryOps = RegistryOps.create(JsonOps.INSTANCE, context.getSource().registryAccess());

        ResourcefulBeesAPI.getRegistry().getBeeRegistry().getFamilyTree().values().forEach(c -> c.forEach(familyUnit -> {
            Recipe<BreederRecipe.Input> recipe = RecipeBuilder.makeBreedingRecipe(c);
            BreederRecipe.MAP_CODEC.codec().encodeStart(registryOps, (BreederRecipe) recipe)
                    .result()
                    .ifPresent(jsonElement -> {
                        var jsonObj = jsonElement.getAsJsonObject();
                        jsonObj.addProperty("type", ModRecipes.BREEDER_RECIPE_TYPE.getId().toString());
                        String fileName = familyUnit.getParents().getParent1().getPath() + "_" + familyUnit.getParents().getParent2().getPath() + "_" + familyUnit.getChild().getPath() + ".json";
                        Path finalRecipePath = recipePath.resolve(fileName);
                        writeJsonFile(jsonObj, finalRecipePath);
                    });
        }));

        return 1;
    }

    private static void writeJsonFile(JsonObject jsonObject, Path path) {
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
}
