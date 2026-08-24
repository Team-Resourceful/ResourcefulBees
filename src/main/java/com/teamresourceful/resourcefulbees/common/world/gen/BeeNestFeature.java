package com.teamresourceful.resourcefulbees.common.world.gen;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefulbees.common.blockentities.TieredBeehiveBlockEntity;
import com.teamresourceful.resourcefulbees.common.config.WorldGenConfig;
import com.teamresourceful.resourcefulbees.common.entities.CustomBeeEntityType;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.records.HiveType;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
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
import org.jspecify.annotations.NonNull;

import java.util.List;

public class BeeNestFeature extends Feature<NoneFeatureConfiguration> {

    public BeeNestFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(@NonNull FeaturePlaceContext<NoneFeatureConfiguration> context) {
        if (!WorldGenConfig.generateBeeNests) {
            return false;
        }

        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        Holder<Biome> biome = level.getBiome(origin);

        if (!biome.isBound()) {
            return false;
        }

        BlockPos nestPos = getYPos(level, random, biome, origin);

        if (nestPos == BlockPos.ZERO) {
            return false;
        }

        List<Direction> directions = getPossibleDirections(level, nestPos);

        if (directions.isEmpty()) {
            return false;
        }

        Direction direction = directions.get(random.nextInt(directions.size()));
        generateHivePlatform(level, biome, nestPos, direction);
        HiveType hiveType = WorldGenData.getNestType(biome, random);
        Block nest = hiveType.tierOneNest();
        BlockState nestState = nest.defaultBlockState();

        if (nestState.hasProperty(BeehiveBlock.FACING)) {
            nestState = nestState.setValue(
                    BeehiveBlock.FACING,
                    direction
            );
        }

        level.setBlock(nestPos, nestState, Block.UPDATE_ALL);
        setNestBees(nestPos, biome, level, random);

        return true;
    }

    private static BlockPos getYPos(WorldGenLevel level, RandomSource random, Holder<Biome> biome, BlockPos origin) {
        if (biome.is(BiomeTags.IS_NETHER) || level.dimensionType().hasCeiling()) {
            return getBlockPosForCeilingDimension(level, random, origin);
        }

        BlockPos pos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, origin);

        if (random.nextInt(10) != 0 && level.getBlockState(pos.below()).is(Blocks.WATER)) {
            return BlockPos.ZERO;
        }

        return canPlaceOnBlock(level.getBlockState(pos.below()))
                ? pos
                : BlockPos.ZERO;
    }

    private static BlockPos getBlockPosForCeilingDimension(WorldGenLevel level, RandomSource random, BlockPos origin) {
        int minY = level.getMinY() + 10;
        int maxY = level.getMaxY() - 10;

        int verticalRange = maxY - minY;

        if (verticalRange <= 0) {
            return BlockPos.ZERO;
        }

        int startOffset = random.nextInt(verticalRange);

        for (int offset = 0; offset < verticalRange; offset++) {
            int y = minY + Math.floorMod(
                    startOffset - offset,
                    verticalRange
            );

            BlockPos pos = new BlockPos(
                    origin.getX(),
                    y,
                    origin.getZ()
            );

            if (!level.isEmptyBlock(pos)) {
                continue;
            }

            BlockState blockBelow =
                    level.getBlockState(pos.below());

            if (!canPlaceOnBlock(blockBelow)) {
                continue;
            }

            if (blockBelow.is(Blocks.LAVA) && random.nextInt(10) != 0) {
                continue;
            }

            return pos;
        }

        return BlockPos.ZERO;
    }

    private static boolean canPlaceOnBlock(
            BlockState state
    ) {
        return state.is(ModBlockTags.NEST_PLACEABLE_ON);
    }

    private static List<Direction> getPossibleDirections(
            WorldGenLevel level,
            BlockPos pos
    ) {
        return Direction.Plane.HORIZONTAL.stream()
                .filter(direction ->
                        level.isEmptyBlock(
                                pos.relative(direction)
                        )
                )
                .toList();
    }

    private static void generateHivePlatform(WorldGenLevel level, Holder<Biome> biome, BlockPos pos, Direction direction) {
        BlockState ground = level.getBlockState(pos.below());

        if (biome.is(BiomeTags.IS_NETHER) && ground.is(Blocks.LAVA)) {
            generateHivePlatform(level, pos.below(), WorldGenData.getNestPlatform(biome), direction, Blocks.LAVA);
        }

        if (ground.is(Blocks.WATER)) {
            generateHivePlatform(level, pos.below(), WorldGenData.getNestPlatform(biome), direction, Blocks.WATER);
        }
    }

    private static void generateHivePlatform(WorldGenLevel level, BlockPos hivePos, BlockState state, Direction direction, Block replace) {
        if (state.hasProperty(BlockStateProperties.AXIS)) {
            state = state.setValue(
                    BlockStateProperties.AXIS,
                    direction.getAxis()
            );
        }

        setPlatformBlockInDirection(level, state, replace, hivePos.relative(direction));
        setPlatformBlockInDirection(level, state, replace, hivePos.relative(direction.getOpposite()));
        setPlatformBlockInDirection(level, state, replace, hivePos);
    }

    private static void setPlatformBlockInDirection(WorldGenLevel level, BlockState state, Block replace, BlockPos pos) {
        if (level.getBlockState(pos).is(replace)) {
            level.setBlock(pos, state, Block.UPDATE_NEIGHBORS);
        }
    }

    private static void setNestBees(BlockPos pos, Holder<Biome> biome, WorldGenLevel level, RandomSource random) {
        if (!(level.getBlockEntity(pos)
                instanceof TieredBeehiveBlockEntity nest)) {
            return;
        }

        int maxBees = WorldGenConfig.hiveMaxBees;

        if (maxBees <= 0) {
            return;
        }

        WeightedList<MobSpawnSettings.SpawnerData> bees = biome.value()
                .getMobSettings()
                .getMobs(ModConstants.RESOURCEFUL_BEE_CATEGORY);

        for (int i = random.nextInt(maxBees); i < maxBees; i++) {

            bees.getRandom(random)
                    .map(MobSpawnSettings.SpawnerData::type)
                    .filter(type -> type instanceof CustomBeeEntityType<?>)
                    .map(type -> (CustomBeeEntityType<?>) type)
                    .ifPresent(bee -> addBeeToNest(bee, random, nest));
        }
    }

    private static void addBeeToNest(CustomBeeEntityType<?> entity, RandomSource random, TieredBeehiveBlockEntity nest) {
        int maxTimeInHive = entity.getData()
                .getCoreData()
                .maxTimeInHive();

        int timeInHive = random.nextInt(maxTimeInHive);

        nest.getBees().add(new BeehiveBlockEntity.BeeData(TieredBeehiveBlockEntity.create(entity, timeInHive, maxTimeInHive, nest))
        );
    }
}