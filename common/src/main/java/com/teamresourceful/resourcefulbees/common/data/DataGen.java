package com.teamresourceful.resourcefulbees.common.data;

import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModEntities;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModFluids;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import dev.architectury.injectables.targets.ArchitecturyTarget;
import net.minecraft.resources.ResourceLocation;

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
        generateTags(ModEntities.BEES, new ResourceLocation("tags/entity_types/beehive_inhabitors.json"));

        generateTags(ModItems.HONEYCOMB_BLOCK_ITEMS, new ResourceLocation(ArchitecturyTarget.getCurrentTarget(), "tags/items/storage_blocks/honeycombs.json"));
        generateTags(ModBlocks.HONEYCOMB_BLOCKS, new ResourceLocation(ArchitecturyTarget.getCurrentTarget(), "tags/blocks/storage_blocks/honeycombs.json"));
        generateTags(ModItems.HONEYCOMB_ITEMS, new ResourceLocation(ArchitecturyTarget.getCurrentTarget(), "tags/items/honeycombs.json"));

        //custom honey data
        generateTags(ModItems.HONEY_BOTTLE_ITEMS, new ResourceLocation(ArchitecturyTarget.getCurrentTarget(), "tags/items/honey_bottles.json"));
        generateTags(ModItems.HONEY_BUCKET_ITEMS, new ResourceLocation(ArchitecturyTarget.getCurrentTarget(), "tags/items/buckets/honey.json"));

        generateTags(ModBlocks.HONEY_BLOCKS, new ResourceLocation(ArchitecturyTarget.getCurrentTarget(), "tags/blocks/honey_blocks.json"));
        generateTags(ModItems.HONEY_BLOCK_ITEMS, new ResourceLocation(ArchitecturyTarget.getCurrentTarget(), "tags/items/honey_blocks.json"));
        generateHoneyTags();
    }

    private static void generateHoneyTags() {
        TAGS.put(new ResourceLocation(ArchitecturyTarget.getCurrentTarget(), "tags/fluids/honey.json"),
                Stream.concat(ModFluids.FLOWING_HONEY_FLUIDS.getEntries().stream(), ModFluids.STILL_HONEY_FLUIDS.getEntries().stream())
                        .map(RegistryEntry::getId)
                        .collect(Collectors.toSet()));
    }

    private static void generateTags(ResourcefulRegistry<?> register, ResourceLocation resourceLocation) {
        TAGS.put(resourceLocation,
                register.stream()
                        .map(RegistryEntry::getId)
                        .collect(Collectors.toSet()));
    }
}
