package com.resourcefulbees.resourcefulbees.registry;

import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class BiomeDictionary {

    public static final HashMap<String, HashSet<ResourceLocation>> TYPES = new HashMap<>();

    public static class BiomeType {
        public String[] biomes;
    }
}
