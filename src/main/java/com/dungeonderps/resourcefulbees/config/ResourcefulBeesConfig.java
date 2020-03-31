package com.dungeonderps.resourcefulbees.config;

import com.dungeonderps.resourcefulbees.entity.BeeBuilderEntity;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jdk.nashorn.internal.parser.JSONParser;
import net.minecraft.client.renderer.debug.BeeDebugRenderer;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.lang3.tuple.Pair;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.core.Config;

import java.awt.*;
import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

public class ResourcefulBeesConfig {
    public static Path BEE_PATH;
    public static class CommonConfig {
        public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        public static final ForgeConfigSpec SPEC;

        static {
            BUILDER.comment("Mod options");
            SPEC = BUILDER.build();
        }
    }
    // might delete
    public static class BeeConfig {
        private ForgeConfigSpec.BooleanValue enabled;
        private ForgeConfigSpec.ConfigValue<ArrayList<Integer>> beeColor;
        private ForgeConfigSpec.ConfigValue<ArrayList<Integer>> hiveColor1;
        private ForgeConfigSpec.ConfigValue<ArrayList<Integer>> hiveColor2;
        private ForgeConfigSpec.ConfigValue<String> name;
        private ForgeConfigSpec.ConfigValue<String> resource;

    }

    // setup the mod config folder
    public static void setup() {
        Path configPath = FMLPaths.CONFIGDIR.get();
        Path rbConfigPath = Paths.get(configPath.toAbsolutePath().toString(), "resourcefulbees");
        // subfolder for bees
        Path rbBeesPath = Paths.get(rbConfigPath.toAbsolutePath().toString(), "bees");
        BEE_PATH = rbBeesPath;
        try {
            Files.createDirectory(rbConfigPath);
            Files.createDirectory(rbBeesPath);
        } catch (FileAlreadyExistsException e) {
            // do nothing
        } catch (IOException e) {
            //ResourcefulBeeLogger.logger.error("Failed to create resourcefulbees config directory");
        }
        writeDefault();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC, "resourcefulbees/common.toml");
    }

    // write default bees
    public static void writeDefault() {
        // example resource array will add to this
        String[] name = new String[]{"iron", "gold", "diamond"};
        String[] drops = new String[]{"minecraft:iron_ingot", "minecraft:gold_ingot", "minecraft:diamond"};
        int[][] beeColors = new int[3][3], nestColors= new int[3][3];
        // REPLACE THIS WITH ACTUAL NUMBERS
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                nestColors[i][j] = 100;
                beeColors[i][j] = 100;
            }
        }
        String[] flowers = new String[]{"minecraft:poppy", "minecraft:dandelion", "minecraft:orchid"};
        String[] baseBlocks = new String[]{"minecraft:stone", "minecraft:stone", "minecraft:stone"};
        String[] mutBlocks = new String[]{"minecraft:iron_ore","minecraft:gold_ore", "minecraft:diamond_ore"};

        try {
            for (int i = 0; i < drops.length; i++) {
                File file = new File(BEE_PATH + name[i] + ".json");
                file.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                // begin
                writer.write("{\n");
                // drop
                writer.write("  \"drop\":\"" + drops[i] +"\",\n");
                // bee color
                writer.write("  \"beeColor\":" + beeColors[i].toString() + ",\n");
                // nest color
                writer.write("  \"nestColor\":" + nestColors[i] + ",\n");
                // flower
                writer.write("  \"flower\":\"" + flowers[i] + "\",\n");
                // base block
                writer.write("  \"base_block\":\"" + baseBlocks[i] + "\",\n");
                // mutated block
                writer.write("  \"mutated_block\":\"" + mutBlocks[i] + "\"\n");
                // end
                writer.write("}");
                writer.close();
            }
        } catch (FileAlreadyExistsException e) {
            // do nothing
        } catch (IOException e) {
            // log thing
        }
    }

    // parse bees here. to be used where bees are registered.
    public BeeBuilderEntity parseBee(File file) throws FileNotFoundException {
        String name = file.getName(); // find good way to cut the file name
        int end = name.indexOf('.');
        name = name.substring(0, end);
        try {
            // log the reading of the file here
            BufferedReader reader = new BufferedReader(new FileReader(file));
            JsonElement jsonElement = new JsonParser().parse(reader);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            return new BeeBuilderEntity(name, jsonObject.get("drop"), jsonObject.get("beeColor"), jsonObject.get("nestColor"), jsonObject.get("flower"), jsonObject.get("base_block"), jsonObject.get("mutated_block")); // will pass in name,
        } catch (IOException e) {
            // log an error happened
        }
        return null;
    }
}
