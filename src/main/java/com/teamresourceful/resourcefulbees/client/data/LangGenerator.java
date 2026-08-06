package com.teamresourceful.resourcefulbees.client.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.honey.CustomHoneyData;
import com.teamresourceful.resourcefulbees.api.data.honeycomb.OutputVariation;
import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.api.registry.HoneyRegistry;
import com.teamresourceful.resourcefulbees.api.registry.HoneycombRegistry;
import com.teamresourceful.resourcefulbees.api.registry.TraitRegistry;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModPaths;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModFluids;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.Strings;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public final class LangGenerator {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final String ITEM_RESOURCEFULBEES = "item.resourcefulbees.";
    public static final String BLOCK_RESOURCEFULBEES = "block.resourcefulbees.";
    public static final String ENTITY_RESOURCEFULBEES = "entity.resourcefulbees.";
    public static final String ENTITY_TYPE_RESOURCEFULBEES = "entity_type.resourcefulbees.";
    public static final String FLUID_TYPE_RESOURCEFULBEES = "fluid_type.resourcefulbees.";
    public static final String FLUID_RESOURCEFULBEES = "fluid.resourcefulbees.";
    public static final String BEE_RESOURCEFULBEES = "bee_type.resourcefulbees.";
    public static final String HONEY_RESOURCEFULBEES = "honey_type.resourcefulbees.";
    public static final String COMB_RESOURCEFULBEES = "comb_type.resourcefulbees.";
    public static final String TRAIT_RESOURCEFULBEES = "trait_type.resourcefulbees.";

    private LangGenerator() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static int generateEnglishLang(CommandContext<CommandSourceStack> context) {
        ModConstants.LOGGER.info("Generating English Lang...");

        JsonObject jsonObject = new JsonObject();

        BeeRegistry.get()
                .getStreamOfBees()
                .map(CustomBeeData::id)
                .map(Identifier::getPath)
                .forEach(name -> jsonObject.addProperty(ENTITY_RESOURCEFULBEES + name, replaceAndCapitalize(name)));

        BeeRegistry.get()
                .getStreamOfBees()
                .map(CustomBeeData::id)
                .map(Identifier::getPath)
                .forEach(name -> jsonObject.addProperty(ENTITY_TYPE_RESOURCEFULBEES + name, replaceAndCapitalize(name)));

        generateLang(BeeRegistry.get().getStreamOfBees().map(CustomBeeData::id).map(Identifier::getPath), BEE_RESOURCEFULBEES, jsonObject);
        generateLang(HoneyRegistry.get().getStreamOfHoney().map(CustomHoneyData::name), HONEY_RESOURCEFULBEES, jsonObject);
        generateLang(HoneycombRegistry.get().getStreamOfHoneycombs().map(OutputVariation::id), COMB_RESOURCEFULBEES, jsonObject);
        generateLang(TraitRegistry.get().getStreamOfTraits().map(Trait::name), TRAIT_RESOURCEFULBEES, jsonObject);

        generateLang(ModItems.SPAWN_EGG_ITEMS, ITEM_RESOURCEFULBEES, jsonObject);
        generateLang(ModItems.HONEYCOMB_ITEMS, ITEM_RESOURCEFULBEES, jsonObject);
        generateLang(ModItems.HONEY_BOTTLE_ITEMS, ITEM_RESOURCEFULBEES, jsonObject);
        generateLang(ModItems.HONEY_BUCKET_ITEMS, ITEM_RESOURCEFULBEES, jsonObject);
        generateLang(ModItems.HONEY_BLOCK_ITEMS, ITEM_RESOURCEFULBEES, jsonObject);
        generateLang(ModItems.HONEYCOMB_BLOCK_ITEMS, ITEM_RESOURCEFULBEES, jsonObject);
        generateLang(ModBlocks.HONEYCOMB_BLOCKS, BLOCK_RESOURCEFULBEES, jsonObject);
        generateLang(ModBlocks.HONEY_FLUID_BLOCKS, BLOCK_RESOURCEFULBEES, jsonObject);
        generateLang(ModFluids.STILL_HONEY_FLUIDS, FLUID_RESOURCEFULBEES, jsonObject);
        generateLang(Stream.concat(Stream.of("honey"), HoneyRegistry.get().getHoneyTypes().stream()), FLUID_TYPE_RESOURCEFULBEES, jsonObject);

        return writeLangFile(jsonObject);
    }

    private static int writeLangFile(JsonObject object) {
        String langPath = ModPaths.RESOURCES + "/assets/resourcefulbees/lang/";

        try {
            Files.createDirectories(Paths.get(langPath));

            try (FileWriter writer = new FileWriter(Paths.get(langPath, "en_us.json").toFile())) {
                GSON.toJson(object, writer);
            }

            ModConstants.LOGGER.info("Language File Generated!");
            return 1;
        } catch (IOException e) {
            ModConstants.LOGGER.error("Could not generate language file!", e);
            return 0;
        }
    }

    private static void generateLang(ResourcefulRegistry<?> register, String prefix, JsonObject object) {
        register.getEntries()
                .forEach(registryObject -> object.addProperty(prefix + registryObject.getId().getPath(), replaceAndCapitalize(registryObject.getId().getPath())));
    }

    private static void generateLang(Stream<String> register, String prefix, JsonObject object) {
        register.forEach(name -> object.addProperty(prefix + name, replaceAndCapitalize(name)));
    }

    private static String replaceAndCapitalize(String input) {
        return fullyCapitalize(Strings.CS.replace(input, "_", " "));
    }

    /**
     * Modified version of WordUtils.capitalize(String) to optimize and remove useless calls as we dont need them.
     */
    private static String fullyCapitalize(String input) {
        if (input.isEmpty()) return input;

        final char[] chars = input.toCharArray();
        boolean runNext = true;
        for (int i = 0; i < chars.length; i++) {
            final char ch = chars[i];
            if (Character.isWhitespace(ch)) {
                runNext = true;
            } else if (runNext) {
                chars[i] = Character.toTitleCase(ch);
                runNext = false;
            }
        }
        return new String(chars);
    }

}
