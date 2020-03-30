package com.dungeonderps.resourcefulbees.config;

import com.dungeonderps.resourcefulbees.entity.BeeBuilderEntity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import java.util.Map;

public class ResourcefulBeesConfig {
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
//        String[] drops
//        try {
//
//        } catch (FileAlreadyExistsException e) {
//            // do nothing
//        } catch (IOException e) {
//            // log thing
//        }
    }

    // parse bees here. to be used where bees are registered.
    public BeeBuilderEntity parseBee(File file) throws FileNotFoundException {
        Gson gson = new Gson();

        try {
            // log the reading of the file here
            BufferedReader reader = new BufferedReader(new FileReader(file));
            return gson.fromJson(reader, BeeBuilderEntity.class);
        } catch (IOException e) {
            // log an error happened
        }
        return null;
    }
}
