package com.resourcefulbees.resourcefulbees.data;

import com.resourcefulbees.resourcefulbees.api.IBeeRegistry;
import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.init.BeeSetup;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModEntities;
import com.resourcefulbees.resourcefulbees.registry.TraitRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

import static com.resourcefulbees.resourcefulbees.ResourcefulBees.LOGGER;

@SuppressWarnings("deprecation")
public class DataGen {

    private DataGen() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    private static final IBeeRegistry BEE_REGISTRY = BeeRegistry.getRegistry();

    private static final String ITEM_RESOURCEFULBEES = "item.resourcefulbees.";
    private static final String DATA_RESOURCEFULBEES_TAGS_ITEMS = "/data/resourcefulbees/tags/items";
    private static final String REPLACE_FALSE = "\"replace\": false,\n";
    private static final String VALUES = "\"values\": [\n";
    private static final String FINAL_COMMA = "\",\n";

    public static void generateClientData() {
        if (Config.GENERATE_ENGLISH_LANG.get().equals(Boolean.TRUE)) generateEnglishLang();
    }

    public static void generateCommonData() {
        generateBeeTags();
        generateCombBlockItemTags();
        generateCombBlockTags();
        generateCombItemTags();

        //custom honey data
        generateHoneyBottleTags();
        if (Config.HONEY_GENERATE_BLOCKS.get().equals(Boolean.TRUE)) {
            generateHoneyBlockTags();
            generateHoneyBlockItemTags();
        }
        if (Config.HONEY_GENERATE_FLUIDS.get().equals(Boolean.TRUE)) {
            generateHoneyTags();
        }
    }

    private static void writeFile(String path, String file, String data) throws IOException {
        Files.createDirectories(Paths.get(path));
        try (FileWriter writer = new FileWriter(Paths.get(path, file).toFile())) {
            writer.write(data);
        } catch (IOException e) {
            LOGGER.error("context", e);
        }
    }

    private static void generateEnglishLang() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        BEE_REGISTRY.getBees().forEach(((name, customBeeData) -> {
            String displayName = StringUtils.replace(name, "_", " ");
            displayName = WordUtils.capitalizeFully(displayName);

            //block
            generateLangEntry(builder, "block.resourcefulbees.", name, "_honeycomb_block", displayName, "Honeycomb Block");

            //comb
            generateLangEntry(builder, ITEM_RESOURCEFULBEES, name, "_honeycomb", displayName, "Honeycomb");

            //spawn egg
            generateLangEntry(builder, ITEM_RESOURCEFULBEES, name, "_bee_spawn_egg", displayName, "Bee Spawn Egg");

            //entity
            generateLangEntry(builder, "entity.resourcefulbees.", name, "_bee", displayName, "Bee");

        }));
        BEE_REGISTRY.getHoneyBottles().forEach((name, honeyData) -> {
            String displayName = StringUtils.replace(name, "_", " ");
            displayName = WordUtils.capitalizeFully(displayName);

            //honey bottle
            generateLangEntry(builder, ITEM_RESOURCEFULBEES, name, "_honey_bottle", displayName, "Honey Bottle");

            //honey block
            if (Boolean.TRUE.equals(Config.HONEY_GENERATE_BLOCKS.get()) && honeyData.doGenerateHoneyBlock()) {
                generateLangEntry(builder, "block.resourcefulbees.", name, "_honey_block", displayName, "Honey Block");
            }

            if (Boolean.TRUE.equals(Config.HONEY_GENERATE_FLUIDS.get()) && honeyData.doGenerateHoneyFluid()) {
                //honey bucket
                generateLangEntry(builder, ITEM_RESOURCEFULBEES, name, "_honey_fluid_bucket", displayName, "Honey Bucket");

                //honey fluid
                generateLangEntry(builder, "fluid.resourcefulbees.", name, "_honey", displayName, "Honey");
            }
        });
        TraitRegistry.getRegistry().getTraits().forEach((name, trait) -> {
            String displayName = StringUtils.replace(name, "_", " ");
            displayName = WordUtils.capitalizeFully(displayName);
            builder.append(String.format("\"%s\" : \"%s\",%n", trait.getTranslationKey(), displayName));
        });
        builder.deleteCharAt(builder.lastIndexOf(","));
        builder.append("}");

        String langPath = BeeSetup.getResourcePath().toString() + "/assets/resourcefulbees/lang/";
        String langFile = "en_us.json";
        try {
            writeFile(langPath, langFile, builder.toString());
            LOGGER.info("Language File Generated!");
        } catch (IOException e) {
            LOGGER.error("Could not generate language file!");
        }
    }

    private static void generateLangEntry(StringBuilder builder, String prefix, String name, String suffix, String displayName, String displaySuffix){
        builder.append("\"");
        builder.append(prefix);
        builder.append(name);
        builder.append(suffix);
        builder.append("\": \"");
        builder.append(displayName);
        builder.append(" ");
        builder.append(displaySuffix);
        builder.append("\",\n");
    }


    private static void generateCombItemTags() {
        StringBuilder builder = new StringBuilder();

        generateTagEntry(builder, BEE_REGISTRY.getBees().values().stream()
                .filter(bee -> bee.hasHoneycomb() && !bee.hasCustomDrop())
                .map(bee -> bee.getCombRegistryObject().getId()).collect(Collectors.toSet()));

        String combTagPath = BeeSetup.getResourcePath().toString() + DATA_RESOURCEFULBEES_TAGS_ITEMS;
        String combTagFile = "resourceful_honeycomb.json";
        try {
            writeFile(combTagPath, combTagFile, builder.toString());
            LOGGER.info("Resourceful Honeycomb Tag Generated!");
        } catch (IOException e) {
            LOGGER.error("Could not generate resourceful honeycomb tag!");
        }
    }

    private static void generateCombBlockItemTags() {
        StringBuilder builder = new StringBuilder();

        generateTagEntry(builder, BEE_REGISTRY.getBees().values().stream()
                .filter(bee -> bee.hasHoneycomb() && !bee.hasCustomDrop())
                .map(bee -> bee.getCombBlockItemRegistryObject().getId()).collect(Collectors.toSet()));

        String combTagPath = BeeSetup.getResourcePath().toString() + DATA_RESOURCEFULBEES_TAGS_ITEMS;
        String combTagFile = "resourceful_honeycomb_block.json";
        try {
            writeFile(combTagPath, combTagFile, builder.toString());
            LOGGER.info("Resourceful Honeycomb Block Item Tag Generated!");
        } catch (IOException e) {
            LOGGER.error("Could not generate resourceful honeycomb block item tag!");
        }
    }

    private static void generateCombBlockTags() {
        StringBuilder builder = new StringBuilder();

        generateTagEntry(builder, BEE_REGISTRY.getBees().values().stream()
                .filter(bee -> bee.hasHoneycomb() && !bee.hasCustomDrop())
                .map(bee -> bee.getCombBlockRegistryObject().getId()).collect(Collectors.toSet()));

        String combTagPath = BeeSetup.getResourcePath().toString() + "/data/resourcefulbees/tags/blocks";
        String combTagFile = "resourceful_honeycomb_block.json";
        try {
            writeFile(combTagPath, combTagFile, builder.toString());
            LOGGER.info("Resourceful Honeycomb Block Tag Generated!");
        } catch (IOException e) {
            LOGGER.error("Could not generate resourceful honeycomb block tag!");
        }
    }

    private static void generateHoneyBottleTags() {
        StringBuilder builder = new StringBuilder();

        generateTagEntry(builder, BEE_REGISTRY.getHoneyBottles().values().stream()
                .filter(HoneyBottleData::doGenerateHoneyBlock)
                .map(h -> h.getHoneyBottleRegistryObject().getId()).collect(Collectors.toSet()));

        String combTagPath = BeeSetup.getResourcePath().toString() + "/data/forge/tags/items";
        String combTagFile = "honey_bottle.json";
        try {
            writeFile(combTagPath, combTagFile, builder.toString());
            LOGGER.info("Honey Bottle Item Tag Generated!");
        } catch (IOException e) {
            LOGGER.error("Could not generate resourceful honey bottle item tag!");
        }
    }

    private static void generateHoneyBlockTags() {
        StringBuilder builder = new StringBuilder();

        generateTagEntry(builder, BEE_REGISTRY.getHoneyBottles().values().stream()
                .filter(HoneyBottleData::doGenerateHoneyBlock)
                .map(h -> h.getHoneyBlockRegistryObject().getId()).collect(Collectors.toSet()));

        String combTagPath = BeeSetup.getResourcePath().toString() + "/data/resourcefulbees/tags/blocks";
        String combTagFile = "resourceful_honey_block.json";
        try {
            writeFile(combTagPath, combTagFile, builder.toString());
            LOGGER.info("Resourceful Honey Block Tag Generated!");
        } catch (IOException e) {
            LOGGER.error("Could not generate resourceful honey block tag!");
        }
    }

    private static void generateHoneyBlockItemTags() {
        StringBuilder builder = new StringBuilder();

        generateTagEntry(builder, BEE_REGISTRY.getHoneyBottles().values().stream()
                .filter(HoneyBottleData::doGenerateHoneyBlock)
                .map(h -> h.getHoneyBlockItemRegistryObject().getId()).collect(Collectors.toSet()));

        String combTagPath = BeeSetup.getResourcePath().toString() + DATA_RESOURCEFULBEES_TAGS_ITEMS;
        String combTagFile = "resourceful_honey_block.json";
        try {
            writeFile(combTagPath, combTagFile, builder.toString());
            LOGGER.info("Resourceful Honey Bottle Item Tag Generated!");
        } catch (IOException e) {
            LOGGER.error("Could not generate resourceful honey bottle item tag!");
        }
    }

    private static void generateBeeTags() {
        StringBuilder builder = new StringBuilder();

        generateTagEntry(builder, ModEntities.getModBees().values().stream()
                .map(RegistryObject::getId).collect(Collectors.toSet()));

        String entityTagPath = BeeSetup.getResourcePath().toString() + "/data/minecraft/tags/entity_types";
        String entityTagFile = "beehive_inhabitors.json";
        try {
            writeFile(entityTagPath, entityTagFile, builder.toString());
            LOGGER.info("Beehive Inhabitor Tag Generated!");
        } catch (IOException e) {
            LOGGER.error("Could not generate beehive inhabitor tag!");
        }
    }

    private static void generateHoneyTags() {
        StringBuilder builder = new StringBuilder();

        Set<ResourceLocation> objects = BEE_REGISTRY.getHoneyBottles().values().stream()
                .filter(HoneyBottleData::doGenerateHoneyFluid)
                .map(h -> h.getHoneyStillFluidRegistryObject().getId()).collect(Collectors.toSet());
        objects.addAll(BEE_REGISTRY.getHoneyBottles().values().stream()
                .filter(HoneyBottleData::doGenerateHoneyFluid)
                .map(h -> h.getHoneyFlowingFluidRegistryObject().getId()).collect(Collectors.toSet()));

        generateTagEntry(builder, objects);

        String honeyTagPath = BeeSetup.getResourcePath().toString() + "/data/forge/tags/fluids";
        String honeyTagFile = "honey.json";
        try {
            writeFile(honeyTagPath, honeyTagFile, builder.toString());
            LOGGER.info("Resourceful Honey Fluid Tag Generated!");
        } catch (IOException e) {
            LOGGER.error("Could not generate Honey Fluid Tag!");
        }
    }

    private static void generateTagEntry(StringBuilder builder, Set<ResourceLocation> values){
        builder.append("{\n");
        builder.append(REPLACE_FALSE);
        builder.append(VALUES);
        values.forEach(resourceLocation -> {
            builder.append("\"");
            builder.append(resourceLocation);
            builder.append(FINAL_COMMA);
        });
        if (!values.isEmpty()) builder.deleteCharAt(builder.lastIndexOf(","));
        builder.append("]\n}");
    }

}
