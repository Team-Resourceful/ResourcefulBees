package com.resourcefulbees.resourcefulbees.utils;

import com.resourcefulbees.resourcefulbees.data.BeeData;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.google.common.base.Splitter;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.resourcefulbees.resourcefulbees.ResourcefulBees.LOGGER;
import static com.resourcefulbees.resourcefulbees.config.BeeInfo.*;

public class BeeInfoUtils {

    public static void buildFamilyTree(BeeData bee){
        String parent1 = bee.getParent1();
        String parent2 = bee.getParent2();
        FAMILY_TREE.computeIfAbsent(sortParents(parent1, parent2), k -> new RandomCollection<>()).add(bee.getBreedWeight(), bee.getName());
    }

    public static Pair<String, String> sortParents(String parent1, String parent2){
        return parent1.compareTo(parent2) > 0 ? Pair.of(parent1, parent2) : Pair.of(parent2, parent1);
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
                SPAWNABLE_BIOMES.computeIfAbsent(biome, k -> new RandomCollection<>()).add(bee.getSpawnWeight(), bee.getName());
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

    public static Tag<Item> getItemTag(String itemTag) { return ItemTags.getCollection().get(getResource(itemTag));}

    public static Tag<Fluid> getFluidTag(String fluidTag) { return FluidTags.getCollection().get(getResource(fluidTag));}

    public static Tag<Block> getBlockTag(String blockTag) { return BlockTags.getCollection().get(getResource(blockTag));}

    public static Tag<Block> getValidApiaryTag() {
        return BlockTags.getCollection().get(getResource("resourcefulbees:valid_apiary"));
    }
}
