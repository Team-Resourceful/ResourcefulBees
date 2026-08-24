package com.teamresourceful.resourcefulbees.common.world.gen;

import com.teamresourceful.resourcefulbees.common.lib.defaults.DefaultHiveTypes;
import com.teamresourceful.resourcefulbees.common.lib.records.HiveType;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class WorldGenData {

    private static final WeightedCollection<HiveType> OVERWORLD_NESTS =
            Util.make(new WeightedCollection<>(), nests -> {
                nests.add(10, DefaultHiveTypes.GRASS);
                nests.add(10, DefaultHiveTypes.OAK);
                nests.add(7, DefaultHiveTypes.DARK_OAK);
                nests.add(7, DefaultHiveTypes.SPRUCE);
                nests.add(5, DefaultHiveTypes.BIRCH);
                nests.add(3, DefaultHiveTypes.RED_MUSHROOM);
                nests.add(3, DefaultHiveTypes.BROWN_MUSHROOM);
            });

    private WorldGenData() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static HiveType getNestType(Holder<Biome> biome, RandomSource random) {
        ResourceKey<Biome> key = biome.unwrapKey().orElse(null);

        if (biome.is(BiomeTags.IS_END)) {
            return DefaultHiveTypes.CHORUS;
        }

        if (Biomes.WARPED_FOREST.equals(key)) {
            return select(
                    random,
                    DefaultHiveTypes.WARPED,
                    DefaultHiveTypes.WARPED_NYLIUM
            );
        }

        if (Biomes.CRIMSON_FOREST.equals(key)) {
            return select(
                    random,
                    DefaultHiveTypes.CRIMSON,
                    DefaultHiveTypes.CRIMSON_NYLIUM
            );
        }

        if (biome.is(BiomeTags.IS_NETHER)) {
            return select(
                    random,
                    DefaultHiveTypes.NETHERRACK,
                    DefaultHiveTypes.WITHER
            );
        }

        if (biome.is(BiomeTags.IS_SAVANNA)) {
            return DefaultHiveTypes.ACACIA;
        }

        if (biome.is(BiomeTags.IS_JUNGLE)) {
            return DefaultHiveTypes.JUNGLE;
        }

        if (biome.is(BiomeTags.IS_BEACH)
                || biome.is(BiomeTags.IS_OCEAN)) {
            return DefaultHiveTypes.PRISMARINE;
        }

        if (biome.is(BiomeTags.IS_TAIGA)
                || doesSnowInBiome(biome)) {
            return DefaultHiveTypes.SPRUCE;
        }

        if (Biomes.MUSHROOM_FIELDS.equals(key)) {
            return select(
                    random,
                    DefaultHiveTypes.RED_MUSHROOM,
                    DefaultHiveTypes.BROWN_MUSHROOM
            );
        }

        if (biome.is(BiomeTags.HAS_SWAMP_HUT)) {
            return DefaultHiveTypes.OAK;
        }

        if (biome.is(BiomeTags.IS_FOREST)) {
            return select(
                    random,
                    DefaultHiveTypes.BIRCH,
                    DefaultHiveTypes.DARK_OAK
            );
        }

        return OVERWORLD_NESTS.next();
    }

    public static BlockState getNestPlatform(Holder<Biome> biome) {
        if (biome.is(BiomeTags.IS_END)) {
            return Blocks.END_STONE.defaultBlockState();
        }

        if (biome.is(BiomeTags.IS_NETHER)) {
            return Blocks.OBSIDIAN.defaultBlockState();
        }

        if (biome.is(BiomeTags.IS_SAVANNA)) {
            return Blocks.ACACIA_WOOD.defaultBlockState();
        }

        if (biome.is(BiomeTags.IS_JUNGLE)) {
            return Blocks.JUNGLE_WOOD.defaultBlockState();
        }

        if (biome.is(BiomeTags.IS_BEACH)
                || biome.is(BiomeTags.IS_OCEAN)) {
            return doesSnowInBiome(biome)
                    ? Blocks.PACKED_ICE.defaultBlockState()
                    : Blocks.STRIPPED_OAK_WOOD.defaultBlockState();
        }

        if (biome.is(BiomeTags.IS_TAIGA)
                || doesSnowInBiome(biome)) {
            return Blocks.PACKED_ICE.defaultBlockState();
        }

        if (biome.is(BiomeTags.HAS_SWAMP_HUT)) {
            return Blocks.STRIPPED_SPRUCE_WOOD.defaultBlockState();
        }

        if (biome.is(BiomeTags.IS_RIVER)) {
            return doesSnowInBiome(biome)
                    ? Blocks.PACKED_ICE.defaultBlockState()
                    : Blocks.OAK_WOOD.defaultBlockState();
        }

        return Blocks.OAK_WOOD.defaultBlockState();
    }

    public static boolean doesSnowInBiome(Holder<Biome> biome) {
        return biome.isBound()
                && biome.value().hasPrecipitation()
                && biome.value().getBaseTemperature() < 0.15F;
    }

    private static HiveType select(RandomSource random, HiveType first, HiveType second) {
        return random.nextBoolean()
                ? first
                : second;
    }
}