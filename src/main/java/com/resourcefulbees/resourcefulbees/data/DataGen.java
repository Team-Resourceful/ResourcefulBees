package com.resourcefulbees.resourcefulbees.data;

import com.resourcefulbees.resourcefulbees.config.BeeSetup;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.resourcefulbees.resourcefulbees.ResourcefulBees.LOGGER;

public class DataGen {
    
    public static void generateClientData() {
        if (Config.GENERATE_ENGLISH_LANG.get()) GenerateEnglishLang();
    }

    public static void generateCommonData(){
        generateBeeTags();
        generateCombBlockItemTags();
        generateCombBlockTags();
        generateCombItemTags();
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
        BeeRegistry.getBees().forEach(((name, customBeeData) -> {
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
            builder.append("_spawn_egg\" : \"");
            builder.append(displayName);
            builder.append(" Bee Spawn Egg\",\n");
            //entity
            builder.append("\"entity.resourcefulbees.");
            builder.append(name);
            builder.append("_bee\" : \"");
            builder.append(displayName);
            builder.append(" Bee\",\n");
        }));

        builder.deleteCharAt(builder.lastIndexOf(","));
        builder.append("}");

        String langPath = BeeSetup.RESOURCE_PATH.toString() + "/assets/resourcefulbees/lang/";
        String langFile = "en_us.json";
        try {
            writeFile(langPath, langFile, builder.toString());
            LOGGER.info("Language File Generated!");
        } catch (IOException e) {
            LOGGER.error("Could not generate language file!");
            //e.printStackTrace();
        }
    }


    private static void generateCombItemTags(){
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("\"replace\": false,\n");
        builder.append("\"values\": [\n");
        BeeRegistry.getBees().entrySet().stream().filter((c)-> c.getValue().hasHoneycomb()).forEach(((c) -> {
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

    private static void generateCombBlockItemTags(){
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("\"replace\": false,\n");
        builder.append("\"values\": [\n");
        BeeRegistry.getBees().entrySet().stream().filter((c)-> c.getValue().hasHoneycomb()).forEach(((c) -> {
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

    private static void generateCombBlockTags(){
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("\"replace\": false,\n");
        builder.append("\"values\": [\n");
        BeeRegistry.getBees().entrySet().stream().filter((c)-> c.getValue().hasHoneycomb()).forEach(((c) -> {
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

    private static void generateBeeTags(){
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("\"replace\": false,\n");
        builder.append("\"values\": [\n");
        BeeRegistry.getBees().entrySet().stream().filter((c)-> c.getValue().hasHoneycomb()).forEach(((c) -> {
            builder.append("\"");
            builder.append(c.getValue().getEntityTypeRegistryObject().getId());
            builder.append("\",\n");
        }));
        builder.deleteCharAt(builder.lastIndexOf(","));
        builder.append("]\n}");

        String enityTagPath = BeeSetup.RESOURCE_PATH.toString() + "/data/minecraft/tags/entity_types";
        String enityTagFile = "beehive_inhabitors.json";
        try {
            writeFile(enityTagPath, enityTagFile, builder.toString());
            LOGGER.info("Beehive Inhabitor Tag Generated!");
        } catch (IOException e) {
            LOGGER.error("Could not generate beehive inhabitor tag!");
        }
    }

}
