package com.dungeonderps.resourcefulbees.config;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.google.gson.Gson;
import net.minecraft.entity.passive.CustomBeeEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.*;

public class ResourcefulBeesConfig {

    public static final Logger LOGGER = ResourcefulBees.LOGGER;
    public static final String MOD_OPTIONS = "Mod Options";


    public static Path BEE_PATH;

    // CONFIGS
    public static ForgeConfigSpec.BooleanValue GENERATE_DEFAULTS;
    public static ForgeConfigSpec.BooleanValue ENABLE_EASTER_EGG_BEES;
    public static ForgeConfigSpec.BooleanValue DEBUG_MODE;
    public static ForgeConfigSpec.DoubleValue HIVE_OUTPUT_MODIFIER;
    public static ForgeConfigSpec.IntValue HIVE_MAX_BEES;

    public static class CommonConfig {

        public static ForgeConfigSpec COMMON_CONFIG;

        static {
            ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

            COMMON_BUILDER.comment("Mod options").push(MOD_OPTIONS);
            COMMON_BUILDER.pop();

            GENERATE_DEFAULTS = COMMON_BUILDER.comment("Set to true if you want our default bees to populate the bees folder [true/false]")
                    .define("generateDefaults",true);
            ENABLE_EASTER_EGG_BEES = COMMON_BUILDER.comment("Set to true if you want easter egg bees to generate [true/false]")
                    .define("enableEasterEggBees", true);
            DEBUG_MODE = COMMON_BUILDER.comment("Extra logger info [true/false]")
                    .define("debugMode", false);
            HIVE_OUTPUT_MODIFIER = COMMON_BUILDER.comment("Output modifier for the haves when ready to be harvested[range 0.0 - 8.0]")
                    .defineInRange("hiveOutputModifier", 1.0,0.0,8.0);
            HIVE_MAX_BEES = COMMON_BUILDER.comment("Maximum amount of bees in the hive at any given time[range 0 - 16")
                    .defineInRange("hiveMaxBees", 4, 0, 16);

            COMMON_CONFIG = COMMON_BUILDER.build();

            setupBees();
        }
    }

    public static void setupBees() {// CONFIG FOLDER AND FILES MUST BE RUN BEFORE THIS
        // check config for hardcoded bee flag
        /*
        if (GENERATE_DEFAULTS.get()) {
            // get path of this folder
            File f = new File(".");//.toString());
            LOGGER.info("f - Filepath - " + f.getPath());
            LOGGER.info("f - FilepathABS - " + f.getAbsolutePath());

            FilenameFilter filter = (f1, name) -> {
                LOGGER.info("printing inside Filter");
                return name.endsWith(".json");
            };
            LOGGER.info("printing after Filter");
            File[] files = f.listFiles(filter);
            LOGGER.info("Filtered Files Array = " + files);
            for (File file : files) {
                try {
                    Files.copy(file.toPath(), BEE_PATH, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/
        addBees();
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
            //ResourcefulBeeLogger.logger.error("Failed to create resourcefulbees config directory")
            LOGGER.error("failed to create resourcefulbees config directory");
        }
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.COMMON_CONFIG, "resourcefulbees/common.toml");

    }

    private static BeeInfoHolder parseBee(File file) throws FileNotFoundException {
        String name = file.getName(); // find good way to cut the file name
        name = name.substring(0, name.indexOf('.'));

        Gson gson = new Gson();
        Reader reader = new BufferedReader(new FileReader(file));
        BeeInfoHolder bee = gson.fromJson(reader, BeeInfoHolder.class);
        bee.setName(name);
        CustomBeeEntity.BEE_INFO.put(name, bee.getInfo());
        return bee;
    }

    public static void addBees() {
        for (File f: BEE_PATH.toFile().listFiles()) {
            String s = f.getName();
            if (s.substring(s.indexOf('.')) == ".json") {
                try {
                    parseBee(f);
                } catch (FileNotFoundException e) {
                    ResourcefulBees.LOGGER.error("File not found when parsing bees");
                }
            }
        }
    }
}
