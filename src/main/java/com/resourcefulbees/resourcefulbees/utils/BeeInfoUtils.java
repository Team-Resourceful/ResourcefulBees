package com.resourcefulbees.resourcefulbees.utils;

import com.google.common.base.Splitter;
import com.resourcefulbees.resourcefulbees.api.CustomBee;
import com.resourcefulbees.resourcefulbees.api.beedata.*;
import com.resourcefulbees.resourcefulbees.config.BeeRegistry;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.MutationTypes;
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
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.resourcefulbees.resourcefulbees.ResourcefulBees.LOGGER;
import static com.resourcefulbees.resourcefulbees.config.BeeRegistry.FAMILY_TREE;
import static com.resourcefulbees.resourcefulbees.config.BeeRegistry.SPAWNABLE_BIOMES;

public class BeeInfoUtils {

    public static void buildFamilyTree(CustomBee bee){
        String parent1 = bee.BreedData.getParent1();
        String parent2 = bee.BreedData.getParent2();
        FAMILY_TREE.computeIfAbsent(sortParents(parent1, parent2), k -> new RandomCollection<>()).add(bee.BreedData.getBreedWeight(), bee.getName());
        FAMILY_TREE.computeIfAbsent(Pair.of(bee.getName(), bee.getName()), k -> new RandomCollection<>()).add(bee.BreedData.getBreedWeight(), bee.getName());
    }

    public static Pair<String, String> sortParents(String parent1, String parent2){
        return parent1.compareTo(parent2) > 0 ? Pair.of(parent1, parent2) : Pair.of(parent2, parent1);
    }

    public static void parseBiomes(CustomBee bee){
        if (!bee.SpawnData.getBiomeWhitelist().isEmpty()){
            Set<Biome> whitelist = new HashSet<>(getBiomeSet(bee.SpawnData.getBiomeWhitelist()));
            Set<Biome> blacklist = new HashSet<>();
            if (!bee.SpawnData.getBiomeBlacklist().isEmpty())
                blacklist = getBiomeSet(bee.SpawnData.getBiomeBlacklist());
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
            //TODO Fix when forge updates biome stuff
            //biomeSet.addAll(BiomeDictionary.getBiomes(BiomeDictionary.Type.getType(type)));
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

    private static void updateSpawnableBiomes(Set<Biome> whitelist, Set<Biome> blacklist, CustomBee bee){
        for(Biome biome : whitelist){
            if(!blacklist.contains(biome)){
                SPAWNABLE_BIOMES.computeIfAbsent(biome, k -> new RandomCollection<>()).add(bee.SpawnData.getSpawnWeight(), bee.getName());
            }
        }
    }

    public static void genDefaultBee(){
        CustomBee defaultBee = new CustomBee.Builder(BeeConstants.DEFAULT_BEE_TYPE, BeeConstants.FLOWER_TAG_ALL,
                new MutationData.Builder(false, MutationTypes.NONE).createMutationData(),
                new ColorData.Builder(false).createColorData(),
                new CentrifugeData.Builder(false).createCentrifugeData(),
                new BreedData.Builder(false).createBreedData(),
                new SpawnData.Builder(false).createSpawnData())
                .createCustomBee();
        BeeRegistry.registerBee(BeeConstants.DEFAULT_BEE_TYPE, defaultBee);
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

    public static ITag<Fluid> getFluidTag(String fluidTag) { return FluidTags.func_226157_a_().get(getResource(fluidTag));}

    public static ITag<Block> getBlockTag(String blockTag) { return BlockTags.getCollection().get(getResource(blockTag));}

    public static ITag<Block> getValidApiaryTag() {
        return BlockTags.makeWrapperTag("resourcefulbees:valid_apiary");
    }
}
