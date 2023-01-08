package com.teamresourceful.resourcefulbees.common.worldgen;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefulbees.common.blockentities.TieredBeehiveBlockEntity;
import com.teamresourceful.resourcefulbees.common.config.WorldGenConfig;
import com.teamresourceful.resourcefulbees.common.entities.CustomBeeEntityType;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModBlockTags;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import com.teamresourceful.resourcefullib.common.utils.TagUtils;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BeeNestFeature extends Feature<NoneFeatureConfiguration> {

    private static final WeightedCollection<Block> OVERWORLD_BLOCKS = Util.make(new WeightedCollection<>(), blocks -> {
        blocks.add(10, ModBlocks.GRASS_BEE_NEST.get());
        blocks.add(10, ModBlocks.OAK_BEE_NEST.get());
        blocks.add(7, ModBlocks.DARK_OAK_BEE_NEST.get());
        blocks.add(7, ModBlocks.SPRUCE_BEE_NEST.get());
        blocks.add(5, ModBlocks.BIRCH_BEE_NEST.get());
        blocks.add(3, ModBlocks.RED_MUSHROOM_BEE_NEST.get());
        blocks.add(3, ModBlocks.BROWN_MUSHROOM_BEE_NEST.get());
    });

    public BeeNestFeature(Codec<NoneFeatureConfiguration> configFactoryIn) {
        super(configFactoryIn);
    }

    @Override
    public boolean place(@NotNull FeaturePlaceContext<NoneFeatureConfiguration> context) {
        if(!WorldGenConfig.generateBeeNests) return false;
        //gather data
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        RandomSource rand = context.random();
        Holder<Biome> biomeHolder = level.getBiome(pos);

        //find best position
        BlockPos newPos = getYPos(level, rand, biomeHolder, pos);
        if (newPos != BlockPos.ZERO) {
            List<Direction> directions = getPossibleDirections(level, newPos.mutable());
            if (!directions.isEmpty()) {
                Direction direction = directions.get(rand.nextInt(directions.size()));
                //generate hive platform
                generateHivePlatform(level, biomeHolder, newPos, direction);
                //select appropriate nest for biome
                Block nest = getNest(biomeHolder, rand.nextBoolean());
                BlockState newState = nest.defaultBlockState().setValue(BeehiveBlock.FACING, direction);
                level.setBlock(newPos, newState, 1);
                //add bees
                setNestBees(newPos, biomeHolder, level, rand);
                return true;
            }
        }
        return false;
    }

    private static BlockPos getYPos(WorldGenLevel worldIn, RandomSource rand, Holder<Biome> biome, BlockPos initPos) {
        if (biome.is(BiomeTags.IS_NETHER) || worldIn.dimensionType().hasCeiling()) {
            return getBlockPosForNetherBiomes(worldIn, rand, initPos);
        }
        BlockPos pos = worldIn.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, initPos);
        if (rand.nextInt(10) != 0 && worldIn.getBlockState(pos.below()).is(Blocks.WATER)) {
            return BlockPos.ZERO;
        }
        return canPlaceOnBlock(worldIn.getBlockState(pos.below())) ? pos : BlockPos.ZERO;
    }

    @NotNull
    private static BlockPos getBlockPosForNetherBiomes(WorldGenLevel level, RandomSource rand, BlockPos pos) {
        pos = pos.south(rand.nextInt(15)).east(rand.nextInt(15));
        while (level.isEmptyBlock(pos.below())) {
            pos = pos.below();
        }
        while (!level.isEmptyBlock(pos)) {
            pos = pos.above();
        }
        //getHeight and getMaxHeight for nether dimension return 256. need to figure out how to get the roof height
        if (level.isOutsideBuildHeight(pos)) {
            return BlockPos.ZERO;
        }
        BlockState blockBelow = level.getBlockState(pos.below());
        if (!canPlaceOnBlock(blockBelow)) {
            return BlockPos.ZERO;
        }
        if (blockBelow.is(Blocks.LAVA) && rand.nextInt(10) != 0) {
            return BlockPos.ZERO;
        }
        return pos;
    }

    private static boolean canPlaceOnBlock(BlockState state) {
        return state.is(ModBlockTags.NEST_PLACEABLE_ON);
    }

    @NotNull
    private static List<Direction> getPossibleDirections(WorldGenLevel level, BlockPos mutable) {
        return Direction.Plane.HORIZONTAL.stream()
                .filter(dir -> level.isEmptyBlock(mutable.relative(dir, 1)))
                .toList();
    }

    private static void generateHivePlatform(WorldGenLevel level, Holder<Biome> biome, BlockPos pos, Direction direction) {
        BlockState ground = level.getBlockState(pos.below());
        if (biome.is(BiomeTags.IS_NETHER) && ground.is(Blocks.LAVA)) {
            generateHivePlatform(level, pos.below(), getNestPlatform(biome), direction, Blocks.LAVA);
        }
        if (ground.is(Blocks.WATER)) {
            generateHivePlatform(level, pos.below(), getNestPlatform(biome), direction, Blocks.WATER);
        }
    }

    private static BlockState getNestPlatform(Holder<Biome> biome) {
        if (biome.is(BiomeTags.IS_END)) return Blocks.END_STONE.defaultBlockState();
        else if (biome.is(BiomeTags.IS_NETHER)) return Blocks.OBSIDIAN.defaultBlockState();
        else if (biome.is(BiomeTags.IS_SAVANNA))return Blocks.ACACIA_WOOD.defaultBlockState();
        else if (biome.is(BiomeTags.IS_JUNGLE)) return Blocks.JUNGLE_WOOD.defaultBlockState();
        else if (biome.is(BiomeTags.IS_BEACH)) return isFrozenBiome(biome) ? Blocks.PACKED_ICE.defaultBlockState() : Blocks.STRIPPED_OAK_WOOD.defaultBlockState();
        else if (biome.is(BiomeTags.IS_OCEAN)) return isFrozenBiome(biome) ? Blocks.PACKED_ICE.defaultBlockState() : Blocks.STRIPPED_OAK_WOOD.defaultBlockState();
        else if (biome.is(BiomeTags.IS_TAIGA)) return Blocks.PACKED_ICE.defaultBlockState();
        else if (doesSnowInBiome(biome)) return Blocks.PACKED_ICE.defaultBlockState();
        else if (biome.is(BiomeTags.HAS_SWAMP_HUT)) return  Blocks.STRIPPED_SPRUCE_WOOD.defaultBlockState();
        else if (biome.is(BiomeTags.IS_RIVER)) return isFrozenBiome(biome) ? Blocks.PACKED_ICE.defaultBlockState() : Blocks.OAK_WOOD.defaultBlockState();
        return Blocks.OAK_WOOD.defaultBlockState();
    }

    private static void generateHivePlatform(WorldGenLevel level, BlockPos hivePos, BlockState state, Direction direction, Block block) {
        if (state == null) state = Blocks.OAK_WOOD.defaultBlockState();
        if (state.hasProperty(BlockStateProperties.AXIS)) {
            state = state.setValue(BlockStateProperties.AXIS, direction.getAxis());
        }
        setPlatformBlockInDirection(level, state, block, hivePos.relative(direction));
        setPlatformBlockInDirection(level, state, block, hivePos.relative(direction.getOpposite()));
        setPlatformBlockInDirection(level, state, block, hivePos);
    }

    private static void setPlatformBlockInDirection(WorldGenLevel level, BlockState state, Block block, BlockPos pos) {
        if (level.getBlockState(pos).is(block)) {
            level.setBlock(pos, state, Block.UPDATE_NEIGHBORS);
        }
    }

    private static Block getNest(Holder<Biome> biome, boolean headsOrTails) {
        if (biome.is(BiomeTags.IS_END)) return ModBlocks.PURPUR_BEE_NEST.get();
        else if (biome.is(BiomeTags.IS_NETHER)) return getNetherNest(headsOrTails, biome);
        else if (biome.is(BiomeTags.IS_SAVANNA)) return ModBlocks.ACACIA_BEE_NEST.get();
        else if (biome.is(BiomeTags.IS_JUNGLE)) return ModBlocks.JUNGLE_BEE_NEST.get();
        else if (biome.is(BiomeTags.IS_BEACH)) return ModBlocks.PRISMARINE_BEE_NEST.get();
        else if (biome.is(BiomeTags.IS_OCEAN)) return ModBlocks.PRISMARINE_BEE_NEST.get();
        else if (biome.is(BiomeTags.IS_TAIGA)) return ModBlocks.SPRUCE_BEE_NEST.get();
        else if (doesSnowInBiome(biome)) return ModBlocks.SPRUCE_BEE_NEST.get();
        else if (biome.unwrapKey().map(key -> key.equals(Biomes.MUSHROOM_FIELDS)).orElse(false)) return selectNest(headsOrTails, ModBlocks.RED_MUSHROOM_BEE_NEST.get(), ModBlocks.BROWN_MUSHROOM_BEE_NEST.get());
        else if (biome.is(BiomeTags.HAS_SWAMP_HUT)) return ModBlocks.OAK_BEE_NEST.get();
        else if (biome.is(BiomeTags.IS_FOREST)) return selectNest(headsOrTails, ModBlocks.BIRCH_BEE_NEST.get(), ModBlocks.DARK_OAK_BEE_NEST.get());
        return OVERWORLD_BLOCKS.next();
    }

    private static boolean doesSnowInBiome(Holder<Biome> biome) {
        return biome.unwrap().mapRight(value -> value.getPrecipitation() == Biome.Precipitation.SNOW).right().orElse(false);
    }

    private static Block getNetherNest(boolean headsOrTails, Holder<Biome> biome){
        ResourceKey<Biome> key = biome.unwrapKey().orElse(null);
        if (Biomes.WARPED_FOREST.equals(key)) return selectNest(headsOrTails, ModBlocks.WARPED_BEE_NEST.get(), ModBlocks.WARPED_NYLIUM_BEE_NEST.get());
        else if (Biomes.CRIMSON_FOREST.equals(key)) return selectNest(headsOrTails, ModBlocks.CRIMSON_BEE_NEST.get(), ModBlocks.CRIMSON_NYLIUM_BEE_NEST.get());
        return selectNest(headsOrTails, ModBlocks.NETHER_BEE_NEST.get(), ModBlocks.WITHER_BEE_NEST.get());
    }

    private static Block selectNest(boolean headsOrTails, Block blockOne, Block blockTwo){
        return headsOrTails ? blockOne : blockTwo;
    }

    private static boolean isFrozenBiome(@Nullable Holder<Biome> biome) {
        if (biome != null && biome.isBound()) {
            return get(biome).getPrecipitation() == Biome.Precipitation.SNOW || get(biome).getBaseTemperature() < 0.15F;
        }
        return false;
    }

    private static Biome get(Holder<Biome> biome) {
        return biome.unwrap().right().orElseThrow();
    }

    private static void setNestBees(BlockPos pos, Holder<Biome> biome, WorldGenLevel level, RandomSource rand){
        if (level.getBlockEntity(pos) instanceof TieredBeehiveBlockEntity nest) {
            int maxBees = Math.round(WorldGenConfig.hiveMaxBees * 0.5f);

            WeightedRandomList<MobSpawnSettings.SpawnerData> bees = get(biome).getMobSettings().getMobs(ModConstants.BEE_CATEGORY);
            for (int i = rand.nextInt(maxBees); i < maxBees ; i++) {
                bees.getRandom(rand)
                    .map(data -> data.type)
                    .filter(type -> type instanceof CustomBeeEntityType<?>)
                    .map(type -> (CustomBeeEntityType<?>) type)
                    .ifPresent(bee -> addBeeToNest(bee, rand, nest));
            }
        }
    }

    private static void addBeeToNest(CustomBeeEntityType<?> entity, RandomSource rand, TieredBeehiveBlockEntity nest) {
        ResourceLocation id = Registry.ENTITY_TYPE.getKey(entity);
        if (id != Registry.ENTITY_TYPE.getDefaultKey()) {
            CompoundTag tag = TagUtils.tagWithData(NBTConstants.NBT_ID, StringTag.valueOf(id.toString()));
            int timeInHive = rand.nextInt(entity.getData().getCoreData().maxTimeInHive());
            nest.getBees().add(new BeehiveBlockEntity.BeeData(tag, 0, timeInHive));
        }
    }
}
