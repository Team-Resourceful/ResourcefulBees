package com.teamresourceful.resourcefulbees.common.data;

import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.registry.api.RegistryEntry;
import com.teamresourceful.resourcefulbees.common.registry.api.ResourcefulRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModEntities;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModFluids;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.versions.forge.ForgeVersion;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DataGen {

    private DataGen() {
        throw new UtilityClassError();
    }

    private static final Map<ResourceLocation, Set<ResourceLocation>> TAGS = new HashMap<>();

    public static Map<ResourceLocation, Set<ResourceLocation>> getTags() {
        return Collections.unmodifiableMap(TAGS);
    }

    public static void generateCommonData() {
        generateBeeTags();

        generateTags(ModItems.HONEYCOMB_BLOCK_ITEMS, new ResourceLocation(ForgeVersion.MOD_ID, "tags/items/storage_blocks/honeycombs.json"));
        generateTags(ModBlocks.HONEYCOMB_BLOCKS, new ResourceLocation(ForgeVersion.MOD_ID, "tags/blocks/storage_blocks/honeycombs.json"));
        generateTags(ModItems.HONEYCOMB_ITEMS, new ResourceLocation(ForgeVersion.MOD_ID, "tags/items/honeycombs.json"));

        //custom honey data
        generateTags(ModItems.HONEY_BOTTLE_ITEMS, new ResourceLocation(ForgeVersion.MOD_ID, "tags/items/honey_bottles.json"));
        generateTags(ModItems.HONEY_BUCKET_ITEMS, new ResourceLocation(ForgeVersion.MOD_ID, "tags/items/buckets/honey.json"));

        generateTags(ModBlocks.HONEY_BLOCKS, new ResourceLocation(ForgeVersion.MOD_ID, "tags/blocks/honey_blocks.json"));
        generateTags(ModItems.HONEY_BLOCK_ITEMS, new ResourceLocation(ForgeVersion.MOD_ID, "tags/items/honey_blocks.json"));
        generateHoneyTags();
    }

    private static void generateBeeTags() {
        TAGS.put(new ResourceLocation("minecraft", "tags/entity_types/beehive_inhabitors.json"),
                ModEntities.getSetOfModBees().stream()
                        .map(RegistryEntry::getId)
                        .collect(Collectors.toSet()));
    }

    private static void generateHoneyTags() {
        TAGS.put(new ResourceLocation("forge", "tags/fluids/honey.json"),
                Stream.concat(ModFluids.FLOWING_HONEY_FLUIDS.getEntries().stream(), ModFluids.STILL_HONEY_FLUIDS.getEntries().stream())
                        .map(RegistryEntry::getId)
                        .collect(Collectors.toSet()));
    }

    private static void generateTags(ResourcefulRegistry<?> register, ResourceLocation resourceLocation) {
        TAGS.put(resourceLocation,
                register.getEntries().stream()
                        .map(RegistryEntry::getId)
                        .collect(Collectors.toSet()));
    }
}
