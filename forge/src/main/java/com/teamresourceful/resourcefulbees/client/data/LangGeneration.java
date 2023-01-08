package com.teamresourceful.resourcefulbees.client.data;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.honey.CustomHoneyData;
import com.teamresourceful.resourcefulbees.api.data.honeycomb.OutputVariation;
import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.api.registry.HoneyRegistry;
import com.teamresourceful.resourcefulbees.api.registry.HoneycombRegistry;
import com.teamresourceful.resourcefulbees.api.registry.TraitRegistry;
import com.teamresourceful.resourcefulbees.common.config.ClientConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModPaths;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModFluids;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraftforge.registries.DeferredRegister;
import org.apache.commons.lang3.StringUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public final class LangGeneration {

    public static final String ITEM_RESOURCEFULBEES = "item.resourcefulbees.";
    public static final String BLOCK_RESOURCEFULBEES = "block.resourcefulbees.";
    public static final String ENTITY_RESOURCEFULBEES = "entity.resourcefulbees.";
    public static final String FLUID_TYPE_RESOURCEFULBEES = "fluid_type.resourcefulbees.";
    public static final String FLUID_RESOURCEFULBEES = "fluid.resourcefulbees.";
    public static final String BEE_RESOURCEFULBEES = "bee_type.resourcefulbees.";
    public static final String HONEY_RESOURCEFULBEES = "honey_type.resourcefulbees.";
    public static final String COMB_RESOURCEFULBEES = "comb_type.resourcefulbees.";
    public static final String TRAIT_RESOURCEFULBEES = "trait_type.resourcefulbees.";

    private LangGeneration() {
        throw new UtilityClassError();
    }

    public static void generateEnglishLang() {
        if (!ClientConfig.generateEnglishLang) return;
        ModConstants.LOGGER.info("Generating English Lang...");

        JsonObject object = new JsonObject();

        BeeRegistry.get()
                .getStreamOfBees()
                .map(CustomBeeData::name)
                .forEach(name -> object.addProperty(ENTITY_RESOURCEFULBEES + name + "_bee", replaceAndCapitalize(name) + " Bee"));

        generateLang(BeeRegistry.get().getStreamOfBees().map(CustomBeeData::name), BEE_RESOURCEFULBEES, object);
        generateLang(HoneyRegistry.get().getStreamOfHoney().map(CustomHoneyData::name), HONEY_RESOURCEFULBEES, object);
        generateLang(HoneycombRegistry.get().getStreamOfHoneycombs().map(OutputVariation::id), COMB_RESOURCEFULBEES, object);
        generateLang(TraitRegistry.get().getStreamOfTraits().map(Trait::name), TRAIT_RESOURCEFULBEES, object);

        generateLang(ModItems.SPAWN_EGG_ITEMS, ITEM_RESOURCEFULBEES, object);
        generateLang(ModItems.HONEYCOMB_ITEMS, ITEM_RESOURCEFULBEES, object);
        generateLang(ModItems.HONEY_BOTTLE_ITEMS, ITEM_RESOURCEFULBEES, object);
        generateLang(ModItems.HONEY_BUCKET_ITEMS, ITEM_RESOURCEFULBEES, object);
        generateLang(ModBlocks.HONEYCOMB_BLOCKS, BLOCK_RESOURCEFULBEES, object);
        generateLang(ModBlocks.HONEY_FLUID_BLOCKS, BLOCK_RESOURCEFULBEES, object);
        generateLang(ModFluids.STILL_HONEY_FLUIDS, FLUID_RESOURCEFULBEES, object);
        generateLang(ModFluids.FLUID_TYPES, FLUID_TYPE_RESOURCEFULBEES, object);

        String langPath = ModPaths.RESOURCES + "/assets/resourcefulbees/lang/";
        try {
            Files.createDirectories(Paths.get(langPath));
            try (FileWriter writer = new FileWriter(Paths.get(langPath, "en_us.json").toFile())) {
                writer.write(object.toString());
            }
            ModConstants.LOGGER.info("Language File Generated!");
        } catch (IOException e) {
            ModConstants.LOGGER.error("Could not generate language file!");
            e.printStackTrace();
        }
    }

    private static void generateLang(DeferredRegister<?> register, String prefix, JsonObject object){
        register.getEntries()
                .forEach(registryObject -> object.addProperty(prefix + registryObject.getId().getPath(), replaceAndCapitalize(registryObject.getId().getPath())));
    }

    private static void generateLang(ResourcefulRegistry<?> register, String prefix, JsonObject object){
        register.getEntries()
                .forEach(registryObject -> object.addProperty(prefix + registryObject.getId().getPath(), replaceAndCapitalize(registryObject.getId().getPath())));
    }

    private static void generateLang(Stream<String> register, String prefix, JsonObject object){
        register.forEach(name -> object.addProperty(prefix + name, replaceAndCapitalize(name)));
    }

    private static String replaceAndCapitalize(String input) {
        return fullyCapitalize(StringUtils.replace(input, "_", " "));
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
