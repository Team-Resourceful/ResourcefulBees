package com.dungeonderps.resourcefulbees.utils;

import com.dungeonderps.resourcefulbees.data.BeeData;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.lib.MutationTypes;
import com.google.common.base.Splitter;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import static com.dungeonderps.resourcefulbees.ResourcefulBees.LOGGER;
import static com.dungeonderps.resourcefulbees.config.BeeInfo.*;

public class BeeInfoUtils {

    public static final Pattern SINGLE_RESOURCE_PATTERN = Pattern.compile("^(\\w+):(\\w+)$", Pattern.CASE_INSENSITIVE);
    public static final Pattern TAG_RESOURCE_PATTERN = Pattern.compile("^(tag:)(\\w+):(\\w+/\\w+|\\w+)$", Pattern.CASE_INSENSITIVE);

    public static void buildFamilyTree(BeeData bee){
        String parent1 = bee.getParent1();
        String parent2 = bee.getParent2();
        int hash = getHashcode(parent1, parent2);
        FAMILY_TREE.put(hash, bee.getName());
    }

    public static int getHashcode(String parent1, String parent2){
        return parent1.compareTo(parent2) > 0 ? Objects.hash(parent1,parent2) : Objects.hash(parent2,parent1);
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
        LOGGER.warn(name + " Bee " + dataCheckType + " Check Failed! Please check JSON!" +
                "\n\tCurrent value: \"" + data + "\" is not a valid " + dataType + " - Bee may not function properly!");
        return true;
    }

    private static boolean dataCheckPassed(String name, String dataCheckType){
        LOGGER.debug(name + " Bee " + dataCheckType + " Check Passed!");
        return true;
    }

    public static boolean validate(BeeData bee) {
        boolean isValid;

        isValid = validateColor(bee);
        isValid = isValid && validateFlower(bee);
        isValid = isValid && validateMutation(bee);
        isValid = isValid && validateCentrifugeMainOutput(bee);
        isValid = isValid && validateCentrifugeSecondaryOutput(bee);
        isValid = isValid && validateCentrifugeBottleOutput(bee);
        isValid = isValid && validateMaxTimeInHive(bee);
        if (bee.isBreedable()) isValid = isValid && validateBreeding(bee);

        return isValid;
    }

    private static boolean validateMutation(BeeData bee) {
        if (!beeHasMutation(bee)) {
            return true;
        }

        if (TAG_RESOURCE_PATTERN.matcher(bee.getBaseBlock()).matches()) {
            LOGGER.warn("Too early to validate Block Tag for " + bee.getName() + " bee.");
            return true;
        }

        int mutation = -1;

        //validate base block
        Block inputBlock = ForgeRegistries.BLOCKS.getValue(getResource(bee.getBaseBlock()));
        if (isValidBlock(inputBlock)) {
            Fluid inputFluid = ForgeRegistries.FLUIDS.getValue(getResource(bee.getBaseBlock()));
            Item inputItem = ForgeRegistries.ITEMS.getValue(getResource(bee.getBaseBlock()));
            if (isValidFluid(inputFluid)) {
                mutation++;
            } else if (isValidItem(inputItem)) {
                mutation += 2;
            } else return logError(bee.getName(), "Base Block", bee.getBaseBlock(), "block");
        } else return logError(bee.getName(), "Base Block", bee.getBaseBlock(), "block");

        //validate mutation block
        Block outputBlock = ForgeRegistries.BLOCKS.getValue(getResource(bee.getMutationBlock()));
        if (isValidBlock(outputBlock)) {
            Fluid outputFluid = ForgeRegistries.FLUIDS.getValue(getResource(bee.getMutationBlock()));
            Item outputItem = ForgeRegistries.ITEMS.getValue(getResource(bee.getMutationBlock()));
            if (isValidFluid(outputFluid)) { }
            else if (isValidItem(outputItem)) {
                mutation += 2;
            } else return logError(bee.getName(), "Mutation Block", bee.getMutationBlock(), "block");
        } else return logError(bee.getName(), "Mutation Block", bee.getMutationBlock(), "block");

        switch (mutation) {
            case (0) :
                bee.setMutationType(MutationTypes.FLUID_TO_FLUID);
                break;
            case (1) :
                bee.setMutationType(MutationTypes.BLOCK_TO_FLUID);
                break;
            case (2) :
                bee.setMutationType(MutationTypes.FLUID_TO_BLOCK);
                break;
            case (3) :
                bee.setMutationType(MutationTypes.BLOCK_TO_BLOCK);
        }
        return true;
    }

    private static boolean beeHasMutation(BeeData bee) {
        if (TAG_RESOURCE_PATTERN.matcher(bee.getBaseBlock()).matches() ||
                SINGLE_RESOURCE_PATTERN.matcher(bee.getBaseBlock()).matches() &&
                SINGLE_RESOURCE_PATTERN.matcher(bee.getMutationBlock()).matches()) {
            bee.setMutation(true);
            return true;
        }
        LOGGER.warn(StringUtils.capitalize(bee.getName()) + " - Bee has no mutations or the patterns don't match.");
        return false;
    }

    private static boolean validateBreeding(BeeData bee) {
        return !bee.getParent1().equals(bee.getParent2())
                ? dataCheckPassed(bee.getName(), "breeding")
                : logWarn(bee.getName(), "breeding", (bee.getParent1() + " and " + bee.getParent2()),
                "are the same parents. Child bee will not spawn from breeding.");
    }

    private static boolean validateMaxTimeInHive(BeeData bee) {
        double time = bee.getMaxTimeInHive();
        return time >= BeeConst.MIN_HIVE_TIME && time == Math.floor(time) && !Double.isInfinite(time)
                ? dataCheckPassed(bee.getName(), "Time In Hive")
                : logWarn(bee.getName(), "Time In Hive", String.valueOf(bee.getMaxTimeInHive()),
                "time. Value must be greater than or equal to " + BeeConst.MIN_HIVE_TIME);
    }

    private static boolean validateCentrifugeMainOutput(BeeData bee) {
        Item item = ForgeRegistries.ITEMS.getValue(getResource(bee.getMainOutput()));
        return SINGLE_RESOURCE_PATTERN.matcher(bee.getMainOutput()).matches() && isValidItem(item)
                ? dataCheckPassed(bee.getName(), "Centrifuge Output")
                : logError(bee.getName(), "Centrifuge Output", bee.getMainOutput(), "item");
    }

    private static boolean validateCentrifugeSecondaryOutput(BeeData bee) {
        Item item = ForgeRegistries.ITEMS.getValue(getResource(bee.getSecondaryOutput()));
        return SINGLE_RESOURCE_PATTERN.matcher(bee.getSecondaryOutput()).matches() && isValidItem(item)
                ? dataCheckPassed(bee.getName(), "Centrifuge Output")
                : logError(bee.getName(), "Centrifuge Output", bee.getMainOutput(), "item");
    }

    private static boolean validateCentrifugeBottleOutput(BeeData bee) {
        Item item = ForgeRegistries.ITEMS.getValue(getResource(bee.getBottleOutput()));
        return SINGLE_RESOURCE_PATTERN.matcher(bee.getBottleOutput()).matches() && isValidItem(item)
                ? dataCheckPassed(bee.getName(), "Centrifuge Output")
                : logError(bee.getName(), "Centrifuge Output", bee.getMainOutput(), "item");
    }

    private static boolean validateFlower(BeeData bee) {
        Block flower = ForgeRegistries.BLOCKS.getValue(getResource(bee.getFlower()));
        return (bee.getFlower().equals(BeeConst.FLOWER_TAG_ALL) ||
                bee.getFlower().equals(BeeConst.FLOWER_TAG_SMALL) ||
                bee.getFlower().equals(BeeConst.FLOWER_TAG_TALL) ||
                isValidBlock(flower))
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
    
    public static boolean isValidBlock(Block block){
        return block != null && block != Blocks.AIR;
    }

    public static boolean isValidFluid(Fluid fluid){
        return fluid != null && fluid != Fluids.EMPTY;
    }

    public static boolean isValidItem(Item item){
        return item != null && item != Items.AIR;
    }
}
