package com.resourcefulbees.resourcefulbees.compat.jei;

import com.google.common.base.Splitter;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.registry.BiomeDictionary;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.text.WordUtils;

import java.util.*;
import java.util.stream.Collectors;

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


    public static List<ResourceLocation> getBiomes(CustomBeeData beeData) {
        Set<ResourceLocation> biomes = getBiomeSet(beeData.getSpawnData().getBiomeWhitelist());
        Set<ResourceLocation> blackListBiomes = getBiomeSet(beeData.getSpawnData().getBiomeBlacklist());

        biomes.removeAll(blackListBiomes);
        return biomes.stream().collect(Collectors.toList());
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
                if (BiomeDictionary.TYPES.containsKey(s)) {
                    biomeSet.addAll(BiomeDictionary.TYPES.get(s));
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

    private static String buildReturnString(Set<ResourceLocation> whitelist, Set<ResourceLocation> blacklist, CustomBeeData bee) {
        StringJoiner returnList = new StringJoiner(", ");
        whitelist.stream()
                .filter(resourceLocation -> !blacklist.contains(resourceLocation))
                .forEach(resourceLocation -> returnList.add(WordUtils.capitalize(resourceLocation.getPath().replaceAll("_", " "))));
        return returnList.toString();
    }




    // TODO: 24/02/2021 Oreo pls look at the below and see if it can replace the above parsing

    private static List<ResourceLocation> getBiomesFromList(List<String> whitelistStrings) {
        List<ResourceLocation> biomes = new ArrayList();
        for (String whitelistString : whitelistStrings) {
            if (whitelistString.startsWith(BeeConstants.TAG_PREFIX)) {
                biomes.addAll(getBiomesFromTag(whitelistString.replace(BeeConstants.TAG_PREFIX, "")));
            } else {
                biomes.add(getBiomeFromString(whitelistString));
            }
        }
        return biomes;
    }

    private static ResourceLocation getBiomeFromString(String whitelistString) {
        return new ResourceLocation(whitelistString);
    }

    private static List<ResourceLocation> getBiomesFromTag(String s) {
        if (Config.USE_FORGE_DICTIONARIES.get()) {
            return getForgeBiomeLocations(getForgeType(s)).stream().collect(Collectors.toList());
        } else {
            if (BiomeDictionary.TYPES.containsKey(s)) {
                return BiomeDictionary.TYPES.get(s).stream().collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }


    private static Set<RegistryKey<Biome>> getForgeBiomes(net.minecraftforge.common.BiomeDictionary.Type type) {
        return net.minecraftforge.common.BiomeDictionary.getBiomes(type);
    }

    private static Collection<? extends ResourceLocation> getForgeBiomeLocations(net.minecraftforge.common.BiomeDictionary.Type type) {
        return getForgeBiomes(type).stream().map(RegistryKey::getValue).collect(Collectors.toList());
    }

    private static net.minecraftforge.common.BiomeDictionary.Type getForgeType(String s) {
        Collection<net.minecraftforge.common.BiomeDictionary.Type> forgeDict = net.minecraftforge.common.BiomeDictionary.Type.getAll();
        for (net.minecraftforge.common.BiomeDictionary.Type type : forgeDict) {
            if (type.getName().toLowerCase().equals(s)) {
                return type;
            }
        }
        return null;
    }
}
