package com.dungeonderps.resourcefulbees.config;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.google.gson.Gson;
import com.sun.media.jfxmedia.logging.Logger;
import javafx.scene.chart.ScatterChart;
import net.minecraft.entity.passive.CustomBeeEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
TODO REMEMBER THIS INFORMATION!!!!!!

Epic_OreoLast Friday at 9:48 PM
in 1.15.2 say I wanted to dynamically create and register any number of entities, each with distinct properties, based on a custom mob class that's an extension of a vanilla mob class, how would I go about doing so? Is there a way, when registering the entity, to change specific custom values in the entity's class so that I only need the one class as a template?
CommobleLast Friday at 9:54 PM
yes
y'know how you can register a bunch of different blocks that all use the same Block class?
same thing
Epic_OreoLast Friday at 9:58 PM
hmm ok I'll take a look at that and see how to reapply to entities
CommobleLast Friday at 9:59 PM
one thing to keep in mind:
Epic_OreoLast Friday at 10:00 PM
trying to set the mod up so users could add their own custom mob varieties given a set of parameters
CommobleLast Friday at 10:00 PM
when you register an EntityType, it takes an entity factory of the form (entityType, world) -> entity as an argument; most entities define a constructor shaped like that and use that as the factory
ah, it's hard to register things dynamically
lemme finish my other train of thought first and then I'll get to that
IItemstackLast Friday at 10:00 PM
time for custom config
Epic_OreoLast Friday at 10:02 PM
well the goal was the users would have a template in a json file and then build custom mobs using that template, but I can't account for what quantity of mobs a pack dev might want to make custom
IItemstackLast Friday at 10:02 PM
another idea would be to use 1 type but then change up the onInitialSpawn part
CommobleLast Friday at 10:02 PM
a problem with that is that datapacks aren't loaded until after entities are registered
Epic_OreoLast Friday at 10:03 PM
well the json would be in the config folder
IItemstackLast Friday at 10:03 PM
datapacks aren't loaded until after the server starts
CommobleLast Friday at 10:03 PM
same with configs, although you can trick forge into loading your configs early
Epic_OreoLast Friday at 10:03 PM
ahh
Epic_OreoLast Friday at 10:07 PM
so without tricking forge to load a config earlier, it would likely be better to register one mob type, then supply the variation data to the entity class when the configs load, and just have the mob randomly select which data set to use when it's spawned in right?
CommobleLast Friday at 10:08 PM
yeah, and it would store its properties in its NBT data
Epic_OreoLast Friday at 10:09 PM
ok that makes sense, I'd considered that route but thought maybe I could do do multiple entities at registration.
thanks for the help :slight_smile:
 */

public class ResourcefulBeesConfig {
    public static Path BEE_PATH;
    public static final String CATEGORY_GENERAL = "general";
    //public static final String CATEGORY_POWER = "power";

    public static ForgeConfigSpec COMMON_CONFIG;

    // CONFIGS
    public static ForgeConfigSpec.BooleanValue GENERATE_DEFAULTS;
    public static ForgeConfigSpec.BooleanValue ENABLE_EASTER_EGG_BEES;
    public static ForgeConfigSpec.BooleanValue DEBUG_MODE;
    public static ForgeConfigSpec.DoubleValue HIVE_OUTPUT_MODIFIER;
    public static ForgeConfigSpec.IntValue HIVE_MAX_BEES;

    static {
        setup();
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        COMMON_BUILDER.comment("General Settings").push(CATEGORY_GENERAL);
        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();
        setupBees();
    }

    public static void setupBees() {// CONFIG FOLDER AND FILES MUST BE RUN BEFORE THIS
        // check config for hardcoded bee flag
        if (GENERATE_DEFAULTS.get()) {
            // get path of this folder
            File f = new File(Paths.get("").toString());
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {
                    return name.endsWith(".json");
                }
            };
            File[] files = f.listFiles(filter);
            for (File file : files) {
                try {
                    Files.copy(file.toPath(), BEE_PATH, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        addBees();
    }

    public static class CommonConfig {
        public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        public static final ForgeConfigSpec SPEC;

        static {
            BUILDER.comment("Mod options");
            GENERATE_DEFAULTS = BUILDER.comment("Set to true if you want our default bees to populate the bees folder [true/false]").define("generateDefaults",true);
            ENABLE_EASTER_EGG_BEES = BUILDER.comment("Set to true if you want easter egg bees to generate [true/false]").define("enableEasterEggBees", true);
            DEBUG_MODE = BUILDER.comment("Extra logger info [true/false]").define("debugMode", false);
            HIVE_OUTPUT_MODIFIER = BUILDER.comment("Output modifier for the haves when ready to be harvested[range 0.0 - 8.0]").defineInRange("hiveOutputModifier", 1.0,0.0,8.0);
            HIVE_MAX_BEES = BUILDER.comment("Maximum amount of bees in the hive at any given time[range 0 - 16").defineInRange("hiveMaxBees", 4, 0, 16);

            BUILDER.pop();
            SPEC = BUILDER.build();
        }
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
            ResourcefulBees.LOGGER.error("failed to create resourcefulbees config directory");
        }


        //writeDefault();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC, "resourcefulbees/common.toml");

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
