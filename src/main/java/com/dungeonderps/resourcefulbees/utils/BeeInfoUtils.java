package com.dungeonderps.resourcefulbees.utils;

import com.dungeonderps.resourcefulbees.data.BeeData;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.google.common.base.Splitter;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import static com.dungeonderps.resourcefulbees.ResourcefulBees.LOGGER;
import static com.dungeonderps.resourcefulbees.config.BeeInfo.*;

public class BeeInfoUtils {

    public static final Pattern RESOURCE_PATTERN = Pattern.compile("(tag:)?(\\w+):(\\w+/\\w+|\\w+)", Pattern.CASE_INSENSITIVE);
    public static final Pattern TAG_RESOURCE_PATTERN = Pattern.compile("(tag:)(\\w+):(\\w+/\\w+|\\w+)", Pattern.CASE_INSENSITIVE);

    public static void buildFamilyTree(BeeData bee){
        String parent1 = bee.getParent1();
        String parent2 = bee.getParent2();
        int hash = getHashcode(parent1, parent2);
        FAMILY_TREE.put(hash, bee.getName());
    }

    public static int getHashcode(String parent1, String parent2){
        return parent1.compareToIgnoreCase(parent2) > 0 ? Objects.hash(parent1,parent2) : Objects.hash(parent2,parent1);
    }

    public static void parseBiomeList(BeeData bee){
        if (bee.getBiomeList().contains(BeeConst.TAG_PREFIX)){
            parseBiomeTag(bee);
        } else {
            parseBiome(bee);
        }
    }

    private static void parseBiomeTag(BeeData bee){
        List<String> biomeList = Splitter.on(',').splitToList(bee.getBiomeList().replace(BeeConst.TAG_PREFIX,""));
        for(String type : biomeList){
            Set<Biome> biomeSet = BiomeDictionary.getBiomes(BiomeDictionary.Type.getType(type));
            updateSpawnableBiomes(biomeSet,bee);
        }
    }

    private static void parseBiome(BeeData bee){
        List<String> biomeList = Splitter.on(',').splitToList(bee.getBiomeList());
        Set<Biome> biomeSet = new HashSet<>();
        for(String biome : biomeList){
            biomeSet.add(ForgeRegistries.BIOMES.getValue(new ResourceLocation(biome)));
        }
        updateSpawnableBiomes(biomeSet,bee);
    }

    private static void updateSpawnableBiomes(Set<Biome> biomeSet, BeeData bee){
        for(Biome biome : biomeSet){
            if(biome != null){
                SPAWNABLE_BIOMES.computeIfAbsent(biome,k -> new HashSet<>()).add(bee.getName());
            } else {
                LOGGER.error(bee.getName() + " Bee Biome Check Failed! Please check JSON! Bee MAY NOT SPAWN properly...");
            }
        }
    }

    public static void genDefaultBee(){
        BeeData defaultBee = new BeeData();
        defaultBee.setName(BeeConst.DEFAULT_BEE_TYPE);
        defaultBee.setColor(String.valueOf(BeeConst.DEFAULT_COLOR));
        defaultBee.setFlower("minecraft:poppy");
        defaultBee.setBaseBlock("minecraft:stone");
        defaultBee.setMutationBlock("minecraft:stone");
        defaultBee.setBiomeList("test");
        defaultBee.setSpawnInWorld(false);
        BEE_INFO.put(BeeConst.DEFAULT_BEE_TYPE, defaultBee);
    }

    private static boolean logError(String name, String dataCheckType, String data, String dataType){
        LOGGER.error(name + " Bee " + dataCheckType + " Check Failed! Please check JSON!" +
                "\n\tCurrent value: \"" + data + "\" is not a valid " + dataType + " - Bee will not be used!");
        return false;
    }

    private static boolean logWarn(String name, String dataCheckType, String data, String dataType){
        LOGGER.error(name + " Bee " + dataCheckType + " Check Failed! Please check JSON!" +
                "\n\tCurrent value: \"" + data + "\" is not a valid " + dataType + " - Bee may not function properly!");
        return true;
    }

    private static boolean dataCheckPassed(String name, String dataCheckType){
        LOGGER.debug(name + " Bee " + dataCheckType + " Check Passed!");
        return true;
    }

    public static boolean validate(BeeData bee) {
        boolean isValid = true;

        isValid = isValid && validateColor(bee);
        isValid = isValid && validateFlower(bee);
        isValid = isValid && validateBaseBlock(bee);
        isValid = isValid && validateMutationBlock(bee);
        isValid = isValid && validateCentrifugeMainOutput(bee);
        isValid = isValid && validateCentrifugeSecondaryOutput(bee);
        isValid = isValid && validateCentrifugeBottleOutput(bee);
        isValid = isValid && validateMaxTimeInHive(bee);

        return isValid;
    }

    private static boolean validateMaxTimeInHive(BeeData bee) {
        double time = bee.getMaxTimeInHive();
        return time >= BeeConst.MIN_HIVE_TIME && time == Math.floor(time) && !Double.isInfinite(time)
                ? dataCheckPassed(bee.getName(), "Time In Hive")
                : logWarn(bee.getName(), "Time In Hive", String.valueOf(bee.getMaxTimeInHive()),
                "time. Value must be greater than or equal to " + BeeConst.MIN_HIVE_TIME);
    }

    private static boolean validateCentrifugeMainOutput(BeeData bee) {
        return RESOURCE_PATTERN.matcher(bee.getMainOutput()).matches() && !ForgeRegistries.ITEMS.getValue(getResource(bee.getMainOutput())).equals(Items.AIR)
                ? dataCheckPassed(bee.getName(), "Centrifuge Output")
                : logError(bee.getName(), "Centrifuge Output", bee.getMainOutput(), "item");
    }

    private static boolean validateCentrifugeSecondaryOutput(BeeData bee) {
        return RESOURCE_PATTERN.matcher(bee.getSecondaryOutput()).matches() && !ForgeRegistries.ITEMS.getValue(getResource(bee.getSecondaryOutput())).equals(Items.AIR)
                ? dataCheckPassed(bee.getName(), "Centrifuge Output")
                : logError(bee.getName(), "Centrifuge Output", bee.getMainOutput(), "item");
    }

    private static boolean validateCentrifugeBottleOutput(BeeData bee) {
        return RESOURCE_PATTERN.matcher(bee.getBottleOutput()).matches() && !ForgeRegistries.ITEMS.getValue(getResource(bee.getBottleOutput())).equals(Items.AIR)
                ? dataCheckPassed(bee.getName(), "Centrifuge Output")
                : logError(bee.getName(), "Centrifuge Output", bee.getMainOutput(), "item");
    }

    private static boolean validateMutationBlock(BeeData bee) {
        if (!bee.getMutationBlock().isEmpty()) {
            return RESOURCE_PATTERN.matcher(bee.getMutationBlock()).matches() && !ForgeRegistries.BLOCKS.getValue(getResource(bee.getMutationBlock())).equals(Blocks.AIR)
                    ? dataCheckPassed(bee.getName(), "Mutation Block")
                    : logError(bee.getName(), "Mutation Block", bee.getMutationBlock(), "block");
        }
        return true;
    }

    private static boolean validateBaseBlock(BeeData bee) {
        if (!bee.getBaseBlock().isEmpty()) {
            if (RESOURCE_PATTERN.matcher(bee.getBaseBlock()).matches() && TAG_RESOURCE_PATTERN.matcher(bee.getBaseBlock()).matches() && !bee.getMutationBlock().isEmpty()) {
                LOGGER.warn("Too early to validate Block Tag for " + bee.getName() + " bee.");
                return true;
            }
            return RESOURCE_PATTERN.matcher(bee.getBaseBlock()).matches() && !ForgeRegistries.BLOCKS.getValue(getResource(bee.getBaseBlock())).equals(Blocks.AIR)
                    ? dataCheckPassed(bee.getName(), "Base Block")
                    : logError(bee.getName(), "Base Block", bee.getBaseBlock(), "block");
        }
        return true;
    }

    private static boolean validateFlower(BeeData bee) {
        return (bee.getFlower().equals(BeeConst.FLOWER_TAG_ALL) || bee.getFlower().equals(BeeConst.FLOWER_TAG_SMALL) || bee.getFlower().equals(BeeConst.FLOWER_TAG_TALL) || !ForgeRegistries.BLOCKS.getValue(getResource(bee.getFlower())).equals(Blocks.AIR))
                ? dataCheckPassed(bee.getName(), "Flower")
                : logError(bee.getName(), "Flower", bee.getFlower(), "flower");
    }

    private static boolean validateColor(BeeData bee) {
        return Color.validate(bee.getColor())
                ? dataCheckPassed(bee.getName(), "Color")
                : logError(bee.getName(), "Color", bee.getColor(), "color");
    }

    /**
     * Returns new Resource Location with given input.
     *
     * @param resource Resource input as String in the form of "mod_id:item_or_block_id".
     * @return Returns New Resource Location for given input.
     */
    public static ResourceLocation getResource(String resource){
        return new ResourceLocation(resource);
    }
}
