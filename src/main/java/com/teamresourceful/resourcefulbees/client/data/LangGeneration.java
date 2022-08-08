package com.teamresourceful.resourcefulbees.client.data;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.api.beedata.CoreData;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.config.ClientConfig;
import com.teamresourceful.resourcefulbees.common.lib.ModPaths;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.custom.TraitRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModFluids;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.StringUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;

public final class LangGeneration {

    public static final String ITEM_RESOURCEFULBEES = "item.resourcefulbees.";
    public static final String BLOCK_RESOURCEFULBEES = "block.resourcefulbees.";
    public static final String ENTITY_RESOURCEFULBEES = "entity.resourcefulbees.";
    public static final String FLUID_TYPE_RESOURCEFULBEES = "fluid_type.resourcefulbees.";
    public static final String FLUID_RESOURCEFULBEES = "fluid.resourcefulbees.";

    private LangGeneration() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void generateEnglishLang() {
        if (Boolean.FALSE.equals(ClientConfig.GENERATE_ENGLISH_LANG.get())) return;
        LOGGER.info("Generating English Lang...");

        JsonObject object = new JsonObject();

        BeeRegistry.getRegistry().getSetOfBees().stream()
                .map(CustomBeeData::coreData)
                .map(CoreData::name)
                .forEach(name -> object.addProperty(ENTITY_RESOURCEFULBEES + name + "_bee", replaceAndCapitalize(name) + " Bee"));

        generateLang(ModItems.SPAWN_EGG_ITEMS, ITEM_RESOURCEFULBEES, object);
        generateLang(ModItems.HONEYCOMB_ITEMS, ITEM_RESOURCEFULBEES, object);
        generateLang(ModItems.HONEY_BOTTLE_ITEMS, ITEM_RESOURCEFULBEES, object);
        generateLang(ModItems.HONEY_BUCKET_ITEMS, ITEM_RESOURCEFULBEES, object);
        generateLang(ModBlocks.HONEYCOMB_BLOCKS, BLOCK_RESOURCEFULBEES, object);
        generateLang(ModBlocks.HONEY_FLUID_BLOCKS, BLOCK_RESOURCEFULBEES, object);
        generateLang(ModFluids.STILL_HONEY_FLUIDS, FLUID_RESOURCEFULBEES, object);
        generateLang(ModFluids.FLUID_TYPES, FLUID_TYPE_RESOURCEFULBEES, object);

        TraitRegistry.getRegistry().getTraits().forEach((name, trait) -> object.addProperty(trait.getTranslationKey(), replaceAndCapitalize(name)));

        String langPath = ModPaths.RESOURCES + "/assets/resourcefulbees/lang/";
        try {
            Files.createDirectories(Paths.get(langPath));
            try (FileWriter writer = new FileWriter(Paths.get(langPath, "en_us.json").toFile())) {
                writer.write(object.toString());
            }
            LOGGER.info("Language File Generated!");
        } catch (IOException e) {
            LOGGER.error("Could not generate language file!");
            e.printStackTrace();
        }
    }

    private static void generateLang(DeferredRegister<?> register, String prefix, JsonObject object){
        register.getEntries().stream()
                .filter(RegistryObject::isPresent)
                .forEach(registryObject -> object.addProperty(prefix + registryObject.getId().getPath(), replaceAndCapitalize(registryObject.getId().getPath())));
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
