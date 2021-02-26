package com.resourcefulbees.resourcefulbees.registry;

import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class BiomeDictionary {

    private BiomeDictionary() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    private static final Map<String, HashSet<ResourceLocation>> TYPES = new HashMap<>();

    public static Map<String, HashSet<ResourceLocation>> getTypes() {
        return TYPES;
    }

    public static class BiomeType {
        private String[] biomes;

        public String[] getBiomes() {
            return biomes;
        }

        public void setBiomes(String[] biomes) {
            this.biomes = biomes;
        }
    }
}
