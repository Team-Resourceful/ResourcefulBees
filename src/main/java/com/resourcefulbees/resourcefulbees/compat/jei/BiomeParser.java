package com.resourcefulbees.resourcefulbees.compat.jei;

import com.google.common.base.Splitter;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.registry.BiomeDictionary;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.HashSet;
import java.util.Set;

public class BiomeParser {

    public static String parseBiomes(CustomBeeData bee) {
        if (!bee.getSpawnData().getBiomeWhitelist().isEmpty()) {
            Set<ResourceLocation> whitelist = new HashSet<>(getBiomeSet(bee.getSpawnData().getBiomeWhitelist()));
            Set<ResourceLocation> blacklist = new HashSet<>();
            if (!bee.getSpawnData().getBiomeBlacklist().isEmpty())
                blacklist = getBiomeSet(bee.getSpawnData().getBiomeBlacklist());
            return buildReturnString(whitelist, blacklist, bee);
        }

        return "null";
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
        Splitter.on(",").split(list.replace(BeeConstants.TAG_PREFIX,"")).forEach(s -> {
            if (BiomeDictionary.TYPES.containsKey(s)) {
                biomeSet.addAll(BiomeDictionary.TYPES.get(s));
            }
        });

        return biomeSet;
    }

    private static Set<ResourceLocation> parseBiomeList(String list) {
        Set<ResourceLocation> biomeSet = new HashSet<>();
        Splitter.on(',').split(list).forEach(s -> biomeSet.add(new ResourceLocation(s)));

        return biomeSet;
    }

    private static String buildReturnString(Set<ResourceLocation> whitelist, Set<ResourceLocation> blacklist, CustomBeeData bee) {
        StringBuilder returnList = new StringBuilder();
        whitelist.stream()
                .filter(resourceLocation -> !blacklist.contains(resourceLocation))
                .forEach(resourceLocation -> returnList.append(WordUtils.capitalize(resourceLocation.getPath().replaceAll("_"," "))).append(", "));
        returnList.deleteCharAt(returnList.lastIndexOf(","));
        return returnList.toString();
    }
}
