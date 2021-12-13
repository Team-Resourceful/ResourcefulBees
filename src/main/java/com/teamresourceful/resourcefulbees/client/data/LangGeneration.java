package com.teamresourceful.resourcefulbees.client.data;

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
import org.apache.commons.lang3.text.WordUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;

public class LangGeneration {

    public static final String ITEM_RESOURCEFULBEES = "item.resourcefulbees.";
    public static final String BLOCK_RESOURCEFULBEES = "block.resourcefulbees.";
    public static final String ENTITY_RESOURCEFULBEES = "entity.resourcefulbees.";

    private LangGeneration() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void generateEnglishLang() {
        if (Boolean.FALSE.equals(ClientConfig.GENERATE_ENGLISH_LANG.get())) return;
        LOGGER.info("Generating English Lang...");
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        BeeRegistry.getRegistry().getSetOfBees().forEach((customBeeData -> {
            String name = customBeeData.getCoreData().getName();
            String displayName = WordUtils.capitalizeFully(StringUtils.replace(name, "_", " "));
            //entity
            generateLangEntry(builder, ENTITY_RESOURCEFULBEES, name, "_bee", displayName, "Bee");

        }));

        generateLang(ModItems.SPAWN_EGG_ITEMS, ITEM_RESOURCEFULBEES, builder);

        generateLang(ModItems.HONEYCOMB_ITEMS, ITEM_RESOURCEFULBEES, builder);
        generateLang(ModBlocks.HONEYCOMB_BLOCKS, BLOCK_RESOURCEFULBEES, builder);


        generateLang(ModItems.HONEY_BOTTLE_ITEMS, ITEM_RESOURCEFULBEES, builder);
        generateLang(ModBlocks.HONEY_FLUID_BLOCKS, BLOCK_RESOURCEFULBEES, builder);

        generateLang(ModItems.HONEY_BUCKET_ITEMS, ITEM_RESOURCEFULBEES, builder);
        generateLang(ModFluids.STILL_HONEY_FLUIDS, "fluid.resourcefulbees.", builder);

        TraitRegistry.getRegistry().getTraits().forEach((name, trait) -> {
            String displayName = StringUtils.replace(name, "_", " ");
            displayName = WordUtils.capitalizeFully(displayName);
            builder.append(String.format("\"%s\" : \"%s\",%n", trait.getTranslationKey(), displayName));
        });

        builder.deleteCharAt(builder.lastIndexOf(","));
        builder.append("}");

        String langPath = ModPaths.RESOURCES + "/assets/resourcefulbees/lang/";
        try {
            Files.createDirectories(Paths.get(langPath));
            try (FileWriter writer = new FileWriter(Paths.get(langPath, "en_us.json").toFile())) {
                writer.write(builder.toString());
            }
            LOGGER.info("Language File Generated!");
        } catch (IOException e) {
            LOGGER.error("Could not generate language file!");
            e.printStackTrace();
        }
    }

    private static void generateLangEntry(StringBuilder builder, String prefix, String name, String suffix, String displayName, String displaySuffix){
        builder.append("\"")
                .append(prefix)
                .append(name)
                .append(suffix)
                .append("\": \"")
                .append(displayName)
                .append(" ")
                .append(displaySuffix)
                .append("\",\n");
    }

    private static void generateLangEntry(StringBuilder builder, String prefix, String name, String displayName){
        builder.append("\"")
                .append(prefix)
                .append(name)
                .append("\": \"")
                .append(displayName)
                .append("\",\n");
    }

    private static void generateLang(DeferredRegister<?> register, String prefix, StringBuilder builder){
        register.getEntries().stream()
                .filter(RegistryObject::isPresent)
                .forEach(registryObject ->
                        generateLangEntry(builder,
                                prefix,
                                registryObject.getId().getPath(),
                                WordUtils.capitalizeFully(StringUtils.replace(registryObject.getId().getPath(), "_", " "))
                        )
                );
    }

}
