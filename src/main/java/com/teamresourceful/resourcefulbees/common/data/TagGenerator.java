package com.teamresourceful.resourcefulbees.common.data;

import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModEntities;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModFluids;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
//import dev.architectury.injectables.targets.ArchitecturyTarget;
import net.minecraft.resources.Identifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class TagGenerator {

    private TagGenerator() throws UtilityClassException {
        throw new UtilityClassException();
    }

    private static final Map<Identifier, Set<Identifier>> TAGS = new HashMap<>();

    public static Map<Identifier, Set<Identifier>> getTags() {
        return Collections.unmodifiableMap(TAGS);
    }

    public static void generateCommonData() {
        generateTags(ModEntities.BEES, Identifier.fromNamespaceAndPath("minecraft","tags/entity_type/beehive_inhabitors.json"));

//        generateTags(ModItems.HONEYCOMB_BLOCK_ITEMS, Identifier.fromNamespaceAndPath("forge","tags/items/storage_blocks/honeycombs.json"));
//        generateTags(ModBlocks.HONEYCOMB_BLOCKS, Identifier.fromNamespaceAndPath("forge","tags/blocks/storage_blocks/honeycombs.json"));
//        generateTags(ModItems.HONEYCOMB_ITEMS, Identifier.fromNamespaceAndPath("forge","tags/items/honeycombs.json"));
//
//        //custom honey data
//        generateTags(ModItems.HONEY_BOTTLE_ITEMS, Identifier.fromNamespaceAndPath("forge","tags/items/honey_bottles.json"));
//        generateTags(ModItems.HONEY_BUCKET_ITEMS, Identifier.fromNamespaceAndPath("forge","tags/items/buckets/honey_fluid_block.json"));
//
//        generateTags(ModBlocks.HONEY_BLOCKS, Identifier.fromNamespaceAndPath("forge","tags/blocks/honey_blocks.json"));
//        generateTags(ModItems.HONEY_BLOCK_ITEMS, Identifier.fromNamespaceAndPath("forge","tags/items/honey_blocks.json"));
//        generateHoneyTags();
    }

    private static void generateHoneyTags() {
        TAGS.put(Identifier.fromNamespaceAndPath("c","tags/fluids/honey_fluid_block.json"),
                Stream.concat(ModFluids.FLOWING_HONEY_FLUIDS.getEntries().stream(), ModFluids.STILL_HONEY_FLUIDS.getEntries().stream())
                        .map(RegistryEntry::getId)
                        .collect(Collectors.toSet()));
    }

    private static void generateTags(ResourcefulRegistry<?> registry, Identifier identifier) {
        TAGS.put(identifier,
                registry.stream()
                        .map(RegistryEntry::getId)
                        .collect(Collectors.toSet()));
    }
}
