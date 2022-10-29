package com.teamresourceful.resourcefulbees.common.data;

import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModEntities;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModFluids;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.versions.forge.ForgeVersion;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DataGen {

    private DataGen() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
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

        if (Boolean.TRUE.equals(CommonConfig.HONEY_GENERATE_BLOCKS.get())) {
            generateTags(ModBlocks.HONEY_BLOCKS, new ResourceLocation(ForgeVersion.MOD_ID, "tags/blocks/honey_blocks.json"));
            generateTags(ModItems.HONEY_BLOCK_ITEMS, new ResourceLocation(ForgeVersion.MOD_ID, "tags/items/honey_blocks.json"));
        }
        if (Boolean.TRUE.equals(CommonConfig.HONEY_GENERATE_FLUIDS.get())) {
            generateHoneyTags();
        }
    }

    private static void generateBeeTags() {
        TAGS.put(new ResourceLocation("minecraft", "tags/entity_types/beehive_inhabitors.json"),
                ModEntities.getSetOfModBees().stream()
                        .map(RegistryObject::get)
                        .map(Registry.ENTITY_TYPE::getKey)
                        .collect(Collectors.toSet()));
    }

    private static void generateHoneyTags() {
        TAGS.put(new ResourceLocation("forge", "tags/fluids/honey.json"),
                Stream.concat(ModFluids.FLOWING_HONEY_FLUIDS.getEntries().stream(), ModFluids.STILL_HONEY_FLUIDS.getEntries().stream())
                        .filter(RegistryObject::isPresent)
                        .map(RegistryObject::getId)
                        .collect(Collectors.toSet()));
    }

    private static void generateTags(DeferredRegister<?> register, ResourceLocation resourceLocation) {
        TAGS.put(resourceLocation,
                register.getEntries().stream()
                        .filter(RegistryObject::isPresent)
                        .map(RegistryObject::getId)
                        .collect(Collectors.toSet()));
    }
}