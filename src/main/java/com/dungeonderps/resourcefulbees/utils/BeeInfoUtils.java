package com.dungeonderps.resourcefulbees.utils;

import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.google.common.base.Splitter;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.dungeonderps.resourcefulbees.ResourcefulBees.LOGGER;
import static com.dungeonderps.resourcefulbees.config.BeeInfo.BEE_INFO;
import static com.dungeonderps.resourcefulbees.config.BeeInfo.SPAWNABLE_BIOMES;

public class BeeInfoUtils {

    private static final Pattern RESOURCE_PATTERN = Pattern.compile("(tag:)?(\\w+):(\\w+/\\w+|\\w+)", Pattern.CASE_INSENSITIVE);

    public static void parseBiomeList(BeeInfo bee){
        if (bee.getBiomeList().contains("tag:")){
            parseBiomeTag(bee);
        } else {
            parseBiome(bee);
        }
    }

    private static void parseBiomeTag(BeeInfo bee){
        //list with parsed biome tags
        List<String> biomeList = Splitter.on(',').splitToList(bee.getBiomeList().replace("tag:",""));
        for(String type : biomeList){
            //creates set containing all biomes of given type
            Set<Biome> biomeSet = BiomeDictionary.getBiomes(BiomeDictionary.Type.getType(type));
            updateSpawnableBiomes(biomeSet,bee);
        }
    }

    private static void parseBiome(BeeInfo bee){
        List<String> biomeList = Splitter.on(',').splitToList(bee.getBiomeList());
        Set<Biome> biomeSet = new HashSet<>();
        for(String biome : biomeList){
            //creates set containing all biomes
            biomeSet.add(ForgeRegistries.BIOMES.getValue(new ResourceLocation(biome)));
        }
        updateSpawnableBiomes(biomeSet,bee);
    }

    private static void updateSpawnableBiomes(Set<Biome> biomeSet, BeeInfo bee){
        for(Biome biome : biomeSet){
            //checks to see if spawnable biomes map contains current biome,
            //if so then adds bee to array value, otherwise creates new key
            if(biome != null){
                SPAWNABLE_BIOMES.computeIfAbsent(biome,k -> new HashSet<>()).add(bee.getName());
            } else {
                LOGGER.error(bee.getName() + " Bee Biome Check Failed! Please check JSON! Bee MAY NOT SPAWN properly...");
            }
        }
    }

    public static void genDefaultBee(){
        BeeInfo defaultBee = new BeeInfo();
        defaultBee.setName("Default");
        defaultBee.setColor("#FFFFFF");
        defaultBee.setFlower("minecraft:poppy");
        defaultBee.setBaseBlock("minecraft:stone");
        defaultBee.setMutationBlock("minecraft:stone");
        defaultBee.setBiomeList("test");
        defaultBee.setSpawnInWorld(false);
        defaultBee.setEnderBee(false);
        BEE_INFO.put("Default", defaultBee);
    }

    private static boolean logError(String name, String dataCheckType, String data, String dataType){
        LOGGER.error(name + " Bee " + dataCheckType + " Check Failed! Please check JSON!" +
                "\nCurrent value: \"" + data + "\" is not a valid " + dataType + " - Bee will not be used!");
        return false;
    }

    private static boolean dataCheckPassed(String name, String dataCheckType){
        LOGGER.debug(name + " Bee " + dataCheckType + " Check Passed!");
        return true;
    }

    public static boolean validate(BeeInfo bee) {
        boolean isValid = true;

        isValid = isValid && validateColor(bee);
        isValid = isValid && validateFlower(bee);
        isValid = isValid && validateBaseBlock(bee);
        isValid = isValid && validateMutationBlock(bee);
        isValid = isValid && validateCentrifugeOutput(bee);

        return isValid;
    }

    private static boolean validateCentrifugeOutput(BeeInfo bee) {
        return RESOURCE_PATTERN.matcher(bee.getMutationBlock()).matches() && ForgeRegistries.ITEMS.getValue(bee.getResource(bee.getCentrifugeOutput())) != null
                ? dataCheckPassed(bee.getName(), "Centrifuge Output")
                : logError(bee.getName(), "Centrifuge Output", bee.getCentrifugeOutput(), "item");
    }

    private static boolean validateMutationBlock(BeeInfo bee) {
        return  RESOURCE_PATTERN.matcher(bee.getMutationBlock()).matches() && !ForgeRegistries.BLOCKS.getValue(bee.getResource(bee.getMutationBlock())).equals(Blocks.AIR)
                ? dataCheckPassed(bee.getName(), "Mutation Block")
                : logError(bee.getName(), "Mutation Block", bee.getMutationBlock(), "block");
    }


    private static boolean validateBaseBlock(BeeInfo bee) {
        if(RESOURCE_PATTERN.matcher(bee.getBaseBlock()).matches() && bee.getBaseBlock().contains("tag:")) {
            LOGGER.warn("Too early to validate Block Tag for " + bee.getName() + " bee.");
            return true;
            //TODO - Can tag check be fixed?
            //String cleanBaseBlock = getBaseBlock().replace("tag:", "");
            //return BlockTags.getCollection().get(getResource(cleanBaseBlock)) != null
            //        ? dataCheckPassed(getName(), "Base Block")
            //        : logError(getName(), "Base Block", cleanBaseBlock, "tag");
        }
        return RESOURCE_PATTERN.matcher(bee.getBaseBlock()).matches() && !ForgeRegistries.BLOCKS.getValue(bee.getResource(bee.getBaseBlock())).equals(Blocks.AIR)
                ? dataCheckPassed(bee.getName(), "Base Block")
                : logError(bee.getName(), "Base Block", bee.getBaseBlock(), "block");
    }

    private static boolean validateFlower(BeeInfo bee) {
        return (bee.getFlower().equals("all") || bee.getFlower().equals("small") || bee.getFlower().equals("tall") || !ForgeRegistries.BLOCKS.getValue(bee.getResource(bee.getFlower())).equals(Blocks.AIR))
                ? dataCheckPassed(bee.getName(), "Flower")
                : logError(bee.getName(), "Flower", bee.getFlower(), "flower");
    }

    private static boolean validateColor(BeeInfo bee) {
        return Color.validate(bee.getColor())
                ? dataCheckPassed(bee.getName(), "Color")
                : logError(bee.getName(), "Color", bee.getColor(), "color");
    }


}
