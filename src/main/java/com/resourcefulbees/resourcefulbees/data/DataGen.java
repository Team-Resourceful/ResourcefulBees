package com.resourcefulbees.resourcefulbees.data;

import com.resourcefulbees.resourcefulbees.api.IBeeRegistry;
import com.resourcefulbees.resourcefulbees.api.beedata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.init.BeeSetup;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static com.resourcefulbees.resourcefulbees.ResourcefulBees.LOGGER;

public class DataGen {

    private static final IBeeRegistry BEE_REGISTRY = BeeRegistry.getRegistry();

    public static void generateClientData() {
        if (Config.GENERATE_ENGLISH_LANG.get()) GenerateEnglishLang();
    }

    public static void generateCommonData() {
        generateBeeTags();
        generateCombBlockItemTags();
        generateCombBlockTags();
        generateCombItemTags();

        //custom honey data
        generateHoneyBottleTags();
        if (Config.HONEY_GENERATE_BLOCKS.get()) {
            generateHoneyBlockTags();
            generateHoneyBlockItemTags();
        }
        if (Config.HONEY_GENERATE_FLUIDS.get()) {
            generateHoneyTags();
        }
    }

    private static void writeFile(String path, String file, String data) throws IOException {
        Files.createDirectories(Paths.get(path));
        FileWriter writer = new FileWriter(Paths.get(path, file).toFile());
        writer.write(data);
        writer.close();
    }

    private static void GenerateEnglishLang() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        BEE_REGISTRY.getBees().forEach(((name, customBeeData) -> {
            String displayName = StringUtils.replace(name, "_", " ");
            displayName = WordUtils.capitalizeFully(displayName);

            //block
            builder.append("\"block.resourcefulbees.");
            builder.append(name);
            builder.append("_honeycomb_block\" : \"");
            builder.append(displayName);
            builder.append(" Honeycomb Block\",\n");
            //comb
            builder.append("\"item.resourcefulbees.");
            builder.append(name);
            builder.append("_honeycomb\" : \"");
            builder.append(displayName);
            builder.append(" Honeycomb\",\n");
            //spawn egg
            builder.append("\"item.resourcefulbees.");
            builder.append(name);
            builder.append("_bee_spawn_egg\" : \"");
            builder.append(displayName);
            builder.append(" Bee Spawn Egg\",\n");
            //entity
            builder.append("\"entity.resourcefulbees.");
            builder.append(name);
            builder.append("_bee\" : \"");
            builder.append(displayName);
            builder.append(" Bee\",\n");
        }));
        BEE_REGISTRY.getHoneyBottles().forEach((name, honeyData) -> {
            String displayName = StringUtils.replace(name, "_", " ");
            displayName = WordUtils.capitalizeFully(displayName);

            //honey bottle
            builder.append("\"item.resourcefulbees.");
            builder.append(name);
            builder.append("_honey_bottle\": \"");
            builder.append(displayName);
            builder.append(" Honey Bottle\",\n");

            //honey block
            if (Config.HONEY_GENERATE_BLOCKS.get() && honeyData.doGenerateHoneyBlock()) {
                builder.append("\"block.resourcefulbees.");
                builder.append(name);
                builder.append("_honey_block\" : \"");
                builder.append(displayName);
                builder.append(" Honey Block\",\n");
            }

            if (Config.HONEY_GENERATE_FLUIDS.get() && honeyData.doGenerateHoneyFluid()) {
                //honey bucket
                builder.append("\"item.resourcefulbees.");
                builder.append(name);
                builder.append("_honey_fluid_bucket\": \"");
                builder.append(displayName);
                builder.append(" Honey Bucket\",\n");

                //honey fluid
                builder.append("\"fluid.resourcefulbees.");
                builder.append(name);
                builder.append("_honey\": \"");
                builder.append(displayName);
                builder.append(" Honey\",\n");
            }
        });
        builder.deleteCharAt(builder.lastIndexOf(","));
        builder.append("}");

        String langPath = BeeSetup.RESOURCE_PATH.toString() + "/assets/resourcefulbees/lang/";
        String langFile = "en_us.json";
        try {
            writeFile(langPath, langFile, builder.toString());
            LOGGER.info("Language File Generated!");
        } catch (IOException e) {
            LOGGER.error("Could not generate language file!");
        }
    }


    private static void generateCombItemTags() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("\"replace\": false,\n");
        builder.append("\"values\": [\n");
        BEE_REGISTRY.getBees().entrySet().stream().filter((c) -> c.getValue().hasHoneycomb() && !c.getValue().hasCustomDrop()).forEach(((c) -> {
            builder.append("\"");
            builder.append(c.getValue().getCombRegistryObject().getId());
            builder.append("\",\n");
        }));
        builder.deleteCharAt(builder.lastIndexOf(","));
        builder.append("]\n}");

        String combTagPath = BeeSetup.RESOURCE_PATH.toString() + "/data/resourcefulbees/tags/items";
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
        builder.append("{\n");
        builder.append("\"replace\": false,\n");
        builder.append("\"values\": [\n");
        BEE_REGISTRY.getBees().entrySet().stream().filter((c) -> c.getValue().hasHoneycomb() && !c.getValue().hasCustomDrop()).forEach(((c) -> {
            builder.append("\"");
            builder.append(c.getValue().getCombBlockItemRegistryObject().getId());
            builder.append("\",\n");
        }));
        builder.deleteCharAt(builder.lastIndexOf(","));
        builder.append("]\n}");

        String combTagPath = BeeSetup.RESOURCE_PATH.toString() + "/data/resourcefulbees/tags/items";
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
        builder.append("{\n");
        builder.append("\"replace\": false,\n");
        builder.append("\"values\": [\n");
        BEE_REGISTRY.getBees().entrySet().stream().filter((c) -> c.getValue().hasHoneycomb() && !c.getValue().hasCustomDrop()).forEach(((c) -> {
            builder.append("\"");
            builder.append(c.getValue().getCombBlockRegistryObject().getId());
            builder.append("\",\n");
        }));
        builder.deleteCharAt(builder.lastIndexOf(","));
        builder.append("]\n}");

        String combTagPath = BeeSetup.RESOURCE_PATH.toString() + "/data/resourcefulbees/tags/blocks";
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
        builder.append("{\n");
        builder.append("\"replace\": false,\n");
        builder.append("\"values\": [\n");
        Map<String, HoneyBottleData> honey = BEE_REGISTRY.getHoneyBottles();
        if (honey.size() != 0) {
            honey.entrySet().stream().forEach(((c) -> {
                builder.append("\"");
                builder.append(c.getValue().getHoneyBottleRegistryObject().getId());
                builder.append("\",\n");
            }));
            builder.deleteCharAt(builder.lastIndexOf(","));
        }
        builder.append("]\n}");

        String combTagPath = BeeSetup.RESOURCE_PATH.toString() + "/data/forge/tags/items";
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
        builder.append("{\n");
        builder.append("\"replace\": false,\n");
        builder.append("\"values\": [\n");
        Map<String, HoneyBottleData> honey = BEE_REGISTRY.getHoneyBottles();
        if (honey.size() != 0) {
            honey.entrySet().stream().forEach(((c) -> {
                if (c.getValue().doGenerateHoneyBlock()) {
                    builder.append("\"");
                    builder.append(c.getValue().getHoneyBlockRegistryObject().getId());
                    builder.append("\",\n");
                }
            }));
            builder.deleteCharAt(builder.lastIndexOf(","));
        }
        builder.append("]\n}");

        String combTagPath = BeeSetup.RESOURCE_PATH.toString() + "/data/resourcefulbees/tags/blocks";
        String combTagFile = "resourceful_honey_block.json";
        try {
            writeFile(combTagPath, combTagFile, builder.toString());
            LOGGER.info("Resourceful Honey Bottle Item Tag Generated!");
        } catch (IOException e) {
            LOGGER.error("Could not generate resourceful honey bottle item tag!");
        }
    }

    private static void generateHoneyBlockItemTags() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("\"replace\": false,\n");
        builder.append("\"values\": [\n");
        Map<String, HoneyBottleData> honey = BEE_REGISTRY.getHoneyBottles();
        if (honey.size() != 0) {
            honey.entrySet().stream().forEach(((c) -> {
                if (c.getValue().doGenerateHoneyBlock()) {
                    builder.append("\"");
                    builder.append(c.getValue().getHoneyBlockItemRegistryObject().getId());
                    builder.append("\",\n");
                }
            }));
            builder.deleteCharAt(builder.lastIndexOf(","));
        }
        builder.append("]\n}");

        String combTagPath = BeeSetup.RESOURCE_PATH.toString() + "/data/resourcefulbees/tags/items";
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
        builder.append("{\n");
        builder.append("\"replace\": false,\n");
        builder.append("\"values\": [\n");
        BeeRegistry.MOD_BEES.forEach(((s, entityTypeRegistryObject) -> {
            builder.append("\"");
            builder.append(entityTypeRegistryObject.getId());
            builder.append("\",\n");
        }));
        builder.deleteCharAt(builder.lastIndexOf(","));
        builder.append("]\n}");

        String entityTagPath = BeeSetup.RESOURCE_PATH.toString() + "/data/minecraft/tags/entity_types";
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
        builder.append("{\n");
        builder.append("\t\"replace\": false,\n");
        builder.append("\t\"values\": [\n");
        Map<String, HoneyBottleData> honey = BEE_REGISTRY.getHoneyBottles();
        if (honey.size() != 0) {
            honey.entrySet().stream().forEach(((c) -> {
                if (c.getValue().doGenerateHoneyFluid()) {
                    builder.append("\t\t\"");
                    builder.append(c.getValue().getHoneyStillFluidRegistryObject().getId());
                    builder.append("\",\n\t\t\"");
                    builder.append(c.getValue().getHoneyFlowingFluidRegistryObject().getId());
                    builder.append("\",\n");
                }
            }));
            builder.deleteCharAt(builder.lastIndexOf(","));
        }
        builder.append("\t]\n}");

        String honeyTagPath = BeeSetup.RESOURCE_PATH.toString() + "/data/forge/tags/fluids";
        String honeyTagFile = "honey.json";
        try {
            writeFile(honeyTagPath, honeyTagFile, builder.toString());
            LOGGER.info("Resourceful Honey Fluid Tag Generated!");
        } catch (IOException e) {
            LOGGER.error("Could not generate Honey Fluid Tag!");
        }
    }

}
