package com.dungeonderps.resourcefulbees.config;

import com.dungeonderps.resourcefulbees.ItemGroupResourcefulBees;
import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.passive.CustomBeeEntity;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.*;
import java.nio.file.*;
import java.util.Map;

import static com.dungeonderps.resourcefulbees.ResourcefulBees.LOGGER;

public class ResourcefulBeesConfig {

    public static final String MOD_OPTIONS = "Mod Options";

    public static final String ASSETS_DIR = "/assets/resourcefulbees/default_bees/";
    public static final String[] DEFAULT_BEES = new String[]{
            "Diamond.json",
            "Emerald.json",
            "Gold.json",
            "Iron.json",
            "Coal.json",
            "Redstone.json",
            "Nether_Quartz.json",
            "Lapis_Lazuli.json"
    };


    public static Path BEE_PATH;
    public static Path RESOURCE_PATH;

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

            setupDefaultBees();
        }
    }

    public static void setupDefaultBees() {// CONFIG FOLDER AND FILES MUST BE RUN BEFORE THIS
        // check config for hardcoded bee flag
        /*
        if (GENERATE_DEFAULTS.get()) {
        }*/
        for (String bee : DEFAULT_BEES) {
            String path = ASSETS_DIR + bee;   //This allows me to dynamically change the path and later create the new path
                try (InputStream inputStream = ResourcefulBees.class.getResourceAsStream(path)){
                    Path newPath =  Paths.get(BEE_PATH.toString() + "/" + bee);
                    File targetFile = new File(String.valueOf(newPath));
                    Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        addBees();
    }

    // setup the mod config folder
    public static void setup() {
        Path configPath = FMLPaths.CONFIGDIR.get();
        //Path rbConfigPath = Paths.get(configPath.toAbsolutePath().toString(), "resourcefulbees");
        // subfolder for bees
        Path rbBeesPath = Paths.get(configPath.toAbsolutePath().toString(),ResourcefulBees.MOD_ID, "bees");
        Path rbAssetsPath = Paths.get(configPath.toAbsolutePath().toString(),ResourcefulBees.MOD_ID, "resources");
        BEE_PATH = rbBeesPath;
        RESOURCE_PATH = rbAssetsPath;

        try { Files.createDirectories(rbBeesPath);
        } catch (FileAlreadyExistsException e) { // do nothing
        } catch (IOException e) { LOGGER.error("failed to create resourcefulbees config directory");}

        try { Files.createDirectory(rbAssetsPath);
        } catch (FileAlreadyExistsException e) { // do nothing
        } catch (IOException e) { LOGGER.error("Failed to create assets directory");}

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.COMMON_CONFIG, "resourcefulbees/common.toml");

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            loadResources();
        });

    }

    public static void loadResources() {
        Minecraft.getInstance().getResourcePackList().addPackFinder(new IPackFinder() {
            @Override
            public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> map, ResourcePackInfo.IFactory<T> factory) {
                final T packInfo = ResourcePackInfo.createResourcePack(ResourcefulBees.MOD_ID, true, () -> new FolderPack(RESOURCE_PATH.toFile()), factory, ResourcePackInfo.Priority.TOP);
                if (packInfo == null) {
                    LOGGER.error("Failed to load resource pack, some things may not work.");
                    return;
                }
                map.put(ResourcefulBees.MOD_ID, packInfo);
            }
        });
    }


    private static void parseBee(File file) throws IOException {
        String name = file.getName();
        name = name.substring(0, name.indexOf('.'));

        Gson gson = new Gson();
        Reader r = new FileReader(file);
        BeeInfo bee = gson.fromJson(r, BeeInfo.class);
        bee.setName(name);
        CustomBeeEntity.BEE_INFO.put(name, bee);
    }

    public static void addBees() {
        CustomBeeEntity.BEE_INFO.clear();

        BeeInfo defaultBee = new BeeInfo();
        defaultBee.setName("Default");
        defaultBee.setColor("#FFFFFF");
        defaultBee.setFlower("minecraft:poppy");
        defaultBee.setBaseBlock("minecraft:stone");
        defaultBee.setMutationBlock("minecraft:stone");
        defaultBee.setBiomeList("test");
        CustomBeeEntity.BEE_INFO.put("Default", defaultBee);

        for (File f: BEE_PATH.toFile().listFiles()) {
            String s = f.getName();
            if (s.substring(s.indexOf('.')).equals(".json")) {
                try {
                    parseBee(f);
                } catch (IOException e) {
                    LOGGER.error("File not found when parsing bees");
                }
            }
        }
    }
}
