package com.teamresourceful.resourcefulbees.common.registry.custom;

import com.google.gson.JsonArray;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.lib.ModPaths;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.utils.FileUtils;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;

import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;
import static net.minecraftforge.common.BiomeDictionary.Type;
import static net.minecraftforge.common.BiomeDictionary.getBiomes;

public class BiomeDictionary extends HashMap<String, Set<ResourceLocation>> {

    protected static final BiomeDictionary INSTANCE = new BiomeDictionary();

    public static BiomeDictionary get() {
        return INSTANCE;
    }

    public static void build() {
        LOGGER.info("Building Biome Dictionary...");
        if (Boolean.TRUE.equals(CommonConfig.GENERATE_BIOME_DICTIONARIES.get())) {
            FileUtils.setupDefaultFiles("/data/resourcefulbees/biome_dictionary", ModPaths.BIOME_DICTIONARY);
        }
        FileUtils.streamFilesAndParse(ModPaths.BIOME_DICTIONARY, BiomeDictionary::parseType, "Could not stream biome dictionary!!");
    }

    private static void parseType(Reader reader, String name) {
        Set<ResourceLocation> biomeType = CodecUtils.createSetCodec(ResourceLocation.CODEC)
                .parse(JsonOps.INSTANCE, JSONUtils.fromJson(ModConstants.GSON, reader, JsonArray.class))
                .getOrThrow(false, s -> LOGGER.warn("Could not parse biome type {}", name));
        get().put(name, biomeType);
    }

    public static Collection<? extends ResourceLocation> getForgeBiomeLocations(Type type) {
        return getBiomes(type).stream().map(RegistryKey::location).collect(Collectors.toList());
    }

    public static Type getForgeType(ResourceLocation resourceLocation) {
        Collection<Type> forgeDict = Type.getAll();
        for (Type type : forgeDict) {
            if (type.getName().equalsIgnoreCase(resourceLocation.getPath())) {
                return type;
            }
        }
        return null;
    }
}
