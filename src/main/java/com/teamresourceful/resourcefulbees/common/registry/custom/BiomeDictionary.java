package com.teamresourceful.resourcefulbees.common.registry.custom;

import com.google.gson.JsonArray;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.lib.ModPaths;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.utils.FileUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;
import static net.minecraftforge.common.BiomeDictionary.Type;
import static net.minecraftforge.common.BiomeDictionary.getBiomes;

public class BiomeDictionary extends HashMap<String, Set<ResourceLocation>> {
    //TODO switch to using Biome Tags
    protected static final BiomeDictionary INSTANCE = new BiomeDictionary();

    public static BiomeDictionary get() {
        return INSTANCE;
    }

    public static void build() {
        if (Boolean.TRUE.equals(CommonConfig.GENERATE_BIOME_DICTIONARIES.get())) {
            LOGGER.info("Copying Default Biome Dictionaries...");
            FileUtils.copyDefaultFiles("/data/resourcefulbees/biome_dictionary", ModPaths.BIOME_DICTIONARY);
        }
        LOGGER.info("Building Biome Dictionary...");
        FileUtils.streamFilesAndParse(ModPaths.BIOME_DICTIONARY, BiomeDictionary::parseType);
    }

    private static void parseType(Reader reader, String name) {
        Set<ResourceLocation> biomeType = CodecUtils.createSetCodec(ResourceLocation.CODEC)
                .parse(JsonOps.INSTANCE, GsonHelper.fromJson(ModConstants.GSON, reader, JsonArray.class))
                .getOrThrow(false, s -> LOGGER.warn("Could not parse biome type {}", name));
        get().put(name, biomeType);
    }

    public static Collection<ResourceLocation> getForgeBiomeLocations(Type type) {
        return getBiomes(type).stream().map(ResourceKey::location).toList();
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
