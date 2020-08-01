package com.dungeonderps.resourcefulbees.utils;

import com.dungeonderps.resourcefulbees.data.BeeData;
import com.dungeonderps.resourcefulbees.lib.BeeConstants;
import com.dungeonderps.resourcefulbees.lib.MutationTypes;
import com.google.common.base.Splitter;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
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
        FAMILY_TREE.computeIfAbsent(hash, k -> new HashSet<>()).add(bee.getName());
    }

    public static int getHashcode(String parent1, String parent2){
        return parent1.compareTo(parent2) > 0 ? Objects.hash(parent1,parent2) : Objects.hash(parent2,parent1);
    }

    public static void parseBiomes(BeeData bee){
        if (!bee.getBiomeWhitelist().isEmpty()){
            Set<Biome> whitelist = new HashSet<>(getBiomeSet(bee.getBiomeWhitelist()));
            Set<Biome> blacklist = new HashSet<>();
            if (!bee.getBiomeBlacklist().isEmpty())
                blacklist = getBiomeSet(bee.getBiomeBlacklist());
            updateSpawnableBiomes(whitelist, blacklist,bee);
        }
    }

    private static Set<Biome> getBiomeSet(String list){
        Set<Biome> set = new HashSet<>();
        if (list.contains(BeeConstants.TAG_PREFIX))
            set.addAll(parseBiomeListWithTag(list));
        else
            set.addAll(parseBiomeList(list));
        return set;
    }

    private static Set<Biome> parseBiomeListWithTag(String list){
        List<String> biomeList = Splitter.on(',').splitToList(list.replace(BeeConstants.TAG_PREFIX,""));
        Set<Biome> biomeSet = new HashSet<>();

        for(String type : biomeList){
            biomeSet.addAll(BiomeDictionary.getBiomes(BiomeDictionary.Type.getType(type)));
        }
        return biomeSet;
    }

    private static Set<Biome> parseBiomeList(String list){
        List<String> biomeList = Splitter.on(',').splitToList(list);
        Set<Biome> biomeSet = new HashSet<>();
        for(String biomeName : biomeList){
            Biome biome = getBiome(biomeName);
            if (biome != null)
                biomeSet.add(biome);
            else
                LOGGER.error(biomeName +  " - Biome check failed! Please check JSON! Bee MAY NOT SPAWN properly...");
        }
        return biomeSet;
    }

    private static void updateSpawnableBiomes(Set<Biome> whitelist, Set<Biome> blacklist, BeeData bee){
        for(Biome biome : whitelist){
            if(!blacklist.contains(biome)){
                SPAWNABLE_BIOMES.computeIfAbsent(biome,k -> new HashSet<>()).add(bee.getName());
            }
        }
    }

    public static void genDefaultBee(){
        BeeData defaultBee = new BeeData();
        defaultBee.setName(BeeConstants.DEFAULT_BEE_TYPE);
        defaultBee.setHoneycombColor(String.valueOf(BeeConstants.DEFAULT_ITEM_COLOR));
        defaultBee.setFlower("minecraft:poppy");
        defaultBee.setSpawnInWorld(false);
        BEE_INFO.put(BeeConstants.DEFAULT_BEE_TYPE, defaultBee);
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

        if (TAG_RESOURCE_PATTERN.matcher(bee.getMutationInput()).matches()) {
            LOGGER.warn("Too early to validate Block Tag for " + bee.getName() + " bee.");
            return true;
        }

        int mutation = -1;

        //validate base block
        Block inputBlock = getBlock(bee.getMutationInput());
        if (isValidBlock(inputBlock)) {
            Fluid inputFluid = getFluid(bee.getMutationInput());
            Item inputItem = getItem(bee.getMutationInput());
            if (isValidFluid(inputFluid)) {
                mutation++;
            } else if (isValidItem(inputItem)) {
                mutation += 2;
            } else return logError(bee.getName(), "Base Block", bee.getMutationInput(), "block");
        } else return logError(bee.getName(), "Base Block", bee.getMutationInput(), "block");

        //validate mutation block
        Block outputBlock = getBlock(bee.getMutationOutput());
        if (isValidBlock(outputBlock)) {
            Fluid outputFluid = getFluid(bee.getMutationOutput());
            Item outputItem = getItem(bee.getMutationOutput());
            if (isValidFluid(outputFluid)) { }
            else if (isValidItem(outputItem)) {
                mutation += 2;
            } else return logError(bee.getName(), "Mutation Block", bee.getMutationOutput(), "block");
        } else return logError(bee.getName(), "Mutation Block", bee.getMutationOutput(), "block");

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
        if (TAG_RESOURCE_PATTERN.matcher(bee.getMutationInput()).matches() ||
                SINGLE_RESOURCE_PATTERN.matcher(bee.getMutationInput()).matches() &&
                SINGLE_RESOURCE_PATTERN.matcher(bee.getMutationOutput()).matches()) {
            bee.setMutation(true);
            return true;
        }
        LOGGER.warn(StringUtils.capitalize(bee.getName()) + " - Bee has no mutations or the patterns don't match.");
        return false;
    }

    private static boolean validateBreeding(BeeData bee) {
        return !bee.getParent1().equals(bee.getParent2()) ||
                logWarn(bee.getName(), "breeding", (bee.getParent1() + " and " + bee.getParent2()),
                "are the same parents. Child bee will not spawn from breeding.");
    }

    private static boolean validateMaxTimeInHive(BeeData bee) {
        double time = bee.getMaxTimeInHive();
        return time >= BeeConstants.MIN_HIVE_TIME && time == Math.floor(time) && !Double.isInfinite(time) ||
                logWarn(bee.getName(), "Time In Hive", String.valueOf(bee.getMaxTimeInHive()),
                "time. Value must be greater than or equal to " + BeeConstants.MIN_HIVE_TIME);
    }

    private static boolean validateCentrifugeMainOutput(BeeData bee) {
        if (!bee.getMainOutput().isEmpty()) {
            Item item = getItem(bee.getMainOutput());
            return SINGLE_RESOURCE_PATTERN.matcher(bee.getMainOutput()).matches() && isValidItem(item) ||
                    logError(bee.getName(), "Centrifuge Output", bee.getMainOutput(), "item");
        }
        return true;
    }

    private static boolean validateCentrifugeSecondaryOutput(BeeData bee) {
        if (!bee.getMainOutput().isEmpty()) {
            Item item = getItem(bee.getSecondaryOutput());
            return SINGLE_RESOURCE_PATTERN.matcher(bee.getSecondaryOutput()).matches() && isValidItem(item) ||
                    logError(bee.getName(), "Centrifuge Output", bee.getSecondaryOutput(), "item");
        }
        return true;
    }

    private static boolean validateCentrifugeBottleOutput(BeeData bee) {
        if (!bee.getMainOutput().isEmpty()) {
            Item item = getItem(bee.getBottleOutput());
            return SINGLE_RESOURCE_PATTERN.matcher(bee.getBottleOutput()).matches() && isValidItem(item) ||
                    logError(bee.getName(), "Centrifuge Output", bee.getBottleOutput(), "item");
        }
        return true;
    }

    private static boolean validateFlower(BeeData bee) {
        if(TAG_RESOURCE_PATTERN.matcher(bee.getFlower()).matches())
            return true;
        else {
            Block flower = getBlock(bee.getFlower());
            return (bee.getFlower().equals(BeeConstants.FLOWER_TAG_ALL) ||
                    bee.getFlower().equals(BeeConstants.FLOWER_TAG_SMALL) ||
                    bee.getFlower().equals(BeeConstants.FLOWER_TAG_TALL) ||
                    isValidBlock(flower)) ||
                    logError(bee.getName(), "Flower", bee.getFlower(), "flower");
        }
    }

    private static boolean validateColor(BeeData bee) {
        return Color.validate(bee.getHoneycombColor()) ||
                Color.validate(bee.getPrimaryColor()) ||
                Color.validate(bee.getSecondaryColor()) ||
                logError(bee.getName(), "Color", "Color" , "color");
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

    public static Item getItem(String itemName) { return ForgeRegistries.ITEMS.getValue(getResource(itemName));}

    public static Block getBlock(String blockName) { return ForgeRegistries.BLOCKS.getValue(getResource(blockName));}

    public static Fluid getFluid(String fluidName) { return ForgeRegistries.FLUIDS.getValue(getResource(fluidName));}

    public static Biome getBiome(String biomeName) { return ForgeRegistries.BIOMES.getValue(getResource(biomeName));}

    public static ITag<Item> getItemTag(String itemTag) { return ItemTags.getCollection().get(getResource(itemTag));}

    public static ITag<Fluid> getFluidTag(String fluidTag) { return FluidTags.getCollection().get(getResource(fluidTag));}

    public static ITag<Block> getBlockTag(String blockTag) { return BlockTags.getCollection().get(getResource(blockTag));}

    public static ITag<Block> getValidApiaryTag() {
        return BlockTags.makeWrapperTag("resourcefulbees:valid_apiary");
    }
}
