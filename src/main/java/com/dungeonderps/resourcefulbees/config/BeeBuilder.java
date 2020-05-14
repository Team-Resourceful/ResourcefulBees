package com.dungeonderps.resourcefulbees.config;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.utils.BeeInfoUtils;
import com.google.gson.Gson;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.passive.CustomBeeEntity;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.dungeonderps.resourcefulbees.ResourcefulBees.LOGGER;
import static com.dungeonderps.resourcefulbees.config.ResourcefulBeesConfig.GENERATE_DEFAULTS;

public class BeeBuilder{

    private static final String ASSETS_DIR = "/assets/resourcefulbees/default_bees/";

    private static final String[] DEFAULT_BEES = new String[]{
            "Diamond.json",
            "Emerald.json",
            "Gold.json",
            "Iron.json",
            "Coal.json",
            "Redstone.json",
            "Nether_Quartz.json",
            "Lapis_Lazuli.json",
            "Ender.json"
    };

    public static Path BEE_PATH;
    public static Path RESOURCE_PATH;

    public static void setupBees() {
        if (GENERATE_DEFAULTS.get()) setupDefaultBees();
        addBees();
        setupBeeSpawns();
    }

    private static void parseBee(File file) throws IOException {
        String name = file.getName();
        name = name.substring(0, name.indexOf('.'));

        Gson gson = new Gson();
        Reader r = new FileReader(file);
        BeeInfo bee = gson.fromJson(r, BeeInfo.class);
        bee.setName(name);
        if (BeeInfoUtils.validate(bee)){
            BeeInfo.BEE_INFO.put(name, bee);
            if(bee.canSpawnInWorld())
                BeeInfoUtils.parseBiomeList(bee);
            if(bee.isBreedable())
                BeeInfoUtils.buildFamilyTree(bee);
        }
    }
    //REMOVE
    private static void addBees() {
        BeeInfo.BEE_INFO.clear();
        BeeInfoUtils.genDefaultBee();

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

    private static void setupDefaultBees() {// CONFIG FOLDER AND FILES MUST BE RUN BEFORE THIS
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

    private static void setupBeeSpawns() {
        Iterator<Map.Entry<Biome, Set<String>>> spawnableBiomesIterator = BeeInfo.SPAWNABLE_BIOMES.entrySet().iterator();
        while (spawnableBiomesIterator.hasNext()) {
            Map.Entry<Biome, Set<String>> element = spawnableBiomesIterator.next();
            Biome biome = element.getKey();
            biome.getSpawns(EntityClassification.CREATURE).add(new Biome.SpawnListEntry(RegistryHandler.CUSTOM_BEE.get(),20,3,30));
        }

        EntitySpawnPlacementRegistry.register(RegistryHandler.CUSTOM_BEE.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, CustomBeeEntity::canBeeSpawn);
    }
}
