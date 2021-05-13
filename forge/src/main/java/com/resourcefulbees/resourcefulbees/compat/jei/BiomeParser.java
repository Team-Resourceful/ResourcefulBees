package com.resourcefulbees.resourcefulbees.compat.jei;

import com.google.common.base.Splitter;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.api.beedata.SpawnData;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.registry.BiomeDictionary;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import org.apache.commons.lang3.text.WordUtils;

import java.util.*;
import java.util.stream.Collectors;

public class BiomeParser {

    private BiomeParser() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static String parseBiomes(SpawnData bee) {
        if (!bee.getBiomeWhitelist().isEmpty()) {
            Set<ResourceLocation> whitelist = new HashSet<>(getBiomeSet(bee.getBiomeWhitelist()));
            Set<ResourceLocation> blacklist = new HashSet<>();
            if (!bee.getBiomeBlacklist().isEmpty())
                blacklist = getBiomeSet(bee.getBiomeBlacklist());
            return buildReturnString(whitelist, blacklist);
        }

        return "null";
    }


    public static List<ResourceLocation> getBiomes(CustomBeeData beeData) {
        Set<ResourceLocation> biomes = getBiomeSet(beeData.getSpawnData().getBiomeWhitelist());
        Set<ResourceLocation> blackListBiomes = getBiomeSet(beeData.getSpawnData().getBiomeBlacklist());

        biomes.removeAll(blackListBiomes);
        return new ArrayList<>(biomes);
    }

    private static Set<ResourceLocation> getBiomeSet(String list) {
        Set<ResourceLocation> set = new HashSet<>();
        if (list.contains(BeeConstants.TAG_PREFIX))
            set.addAll(parseBiomeListFromTag(list));
        else
            set.addAll(parseBiomeList(list));
        return set;
    }

    private static Set<ResourceLocation> parseBiomeListFromTag(String list) {
        Set<ResourceLocation> biomeSet = new HashSet<>();
        Splitter.on(",").trimResults().split(list.replace(BeeConstants.TAG_PREFIX, "")).forEach(s -> {
            if (Config.USE_FORGE_DICTIONARIES.get()) {
                net.minecraftforge.common.BiomeDictionary.Type type = getForgeType(s);
                if (type != null) {
                    biomeSet.addAll(getForgeBiomeLocations(type));
                }
            } else {
                if (BiomeDictionary.getTypes().containsKey(s)) {
                    biomeSet.addAll(BiomeDictionary.getTypes().get(s));
                }
            }
        });
        return biomeSet;
    }

    private static Set<ResourceLocation> parseBiomeList(String list) {
        Set<ResourceLocation> biomeSet = new HashSet<>();
        Splitter.on(',').trimResults().split(list).forEach(s -> biomeSet.add(new ResourceLocation(s)));

        return biomeSet;
    }

    private static String buildReturnString(Set<ResourceLocation> whitelist, Set<ResourceLocation> blacklist) {
        StringJoiner returnList = new StringJoiner(", ");
        whitelist.stream()
                .filter(resourceLocation -> !blacklist.contains(resourceLocation))
                .forEach(resourceLocation -> returnList.add(WordUtils.capitalize(resourceLocation.getPath().replace("_", " "))));
        return returnList.toString();
    }

    private static Set<ResourceKey<Biome>> getForgeBiomes(net.minecraftforge.common.BiomeDictionary.Type type) {
        return net.minecraftforge.common.BiomeDictionary.getBiomes(type);
    }

    private static Collection<? extends ResourceLocation> getForgeBiomeLocations(net.minecraftforge.common.BiomeDictionary.Type type) {
        return getForgeBiomes(type).stream().map(ResourceKey::location).collect(Collectors.toList());
    }

    private static net.minecraftforge.common.BiomeDictionary.Type getForgeType(String s) {
        Collection<net.minecraftforge.common.BiomeDictionary.Type> forgeDict = net.minecraftforge.common.BiomeDictionary.Type.getAll();
        for (net.minecraftforge.common.BiomeDictionary.Type type : forgeDict) {
            if (type.getName().equalsIgnoreCase(s)) {
                return type;
            }
        }
        return null;
    }
}
