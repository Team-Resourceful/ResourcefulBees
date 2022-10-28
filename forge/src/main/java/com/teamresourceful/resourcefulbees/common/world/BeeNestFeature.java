package com.teamresourceful.resourcefulbees.common.world;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.blockentity.TieredBeehiveBlockEntity;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntityType;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModTags;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.BeehiveEntityAccessor;
import com.teamresourceful.resourcefulbees.common.registry.dynamic.ModSpawnData;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
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
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class BeeNestFeature extends Feature<NoneFeatureConfiguration> {

    private static final WeightedCollection<Block> overworldBlocks = new WeightedCollection<>();

    static {
        overworldBlocks.add(10, ModBlocks.GRASS_BEE_NEST.get());
        overworldBlocks.add(10, ModBlocks.OAK_BEE_NEST.get());
        overworldBlocks.add(7, ModBlocks.DARK_OAK_BEE_NEST.get());
        overworldBlocks.add(7, ModBlocks.SPRUCE_BEE_NEST.get());
        overworldBlocks.add(5, ModBlocks.BIRCH_BEE_NEST.get());
        overworldBlocks.add(3, ModBlocks.RED_MUSHROOM_BEE_NEST.get());
        overworldBlocks.add(3, ModBlocks.BROWN_MUSHROOM_BEE_NEST.get());
    }

    public BeeNestFeature(Codec<NoneFeatureConfiguration> configFactoryIn) { super(configFactoryIn); }

    //TODO rewrite the Nest Feature!!!!
    @Override
    public boolean place(@NotNull FeaturePlaceContext<NoneFeatureConfiguration> context) {
        if(Boolean.FALSE.equals(CommonConfig.GENERATE_BEE_NESTS.get())) return false;
        //gather data
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        RandomSource rand = context.random();
        Holder<Biome> biomeHolder = level.getBiome(pos);
        Optional<ResourceKey<Biome>> biomeKey = biomeHolder.unwrapKey();
        //find best position
        BlockPos newPos = getYPos(level, rand, biomeHolder, pos);
        if (newPos == BlockPos.ZERO) return false;
        //set direction
        BlockPos mutable = newPos.mutable();
        List<Direction> possibleDirections = getPossibleDirections(level, mutable);
        if (possibleDirections.isEmpty()) return false;
        Direction direction = possibleDirections.get(rand.nextInt(possibleDirections.size()));
        //generate hive platform
        generateHivePlatform(level, biomeHolder, biomeKey, newPos, direction);
        //select appropriate nest for biome
        Block nest = getNest(biomeHolder, biomeKey, rand.nextBoolean());
        BlockState newState = nest.defaultBlockState().setValue(BeehiveBlock.FACING, direction);
        level.setBlock(newPos, newState, 1);
        //add bees
        setNestBees(newPos, biomeHolder, level, rand);
        return true;
    }

    @NotNull
    private static List<Direction> getPossibleDirections(WorldGenLevel level, BlockPos mutable) {
        return Direction.Plane.HORIZONTAL.stream()
                .filter(dir -> level.isEmptyBlock(mutable.relative(dir, 1)))
                .toList();
    }

    private static void generateHivePlatform(WorldGenLevel worldIn, BlockPos hivePos, BlockState platformBlock, Direction direction, Block blockToReplace) {
        if (platformBlock == null) platformBlock = Blocks.OAK_WOOD.defaultBlockState();
        if (platformBlock.hasProperty(BlockStateProperties.AXIS)) {
            platformBlock = platformBlock.setValue(BlockStateProperties.AXIS, direction.getAxis());
        }
        setPlatformBlockInDirection(worldIn, platformBlock, blockToReplace, hivePos.offset(direction.getNormal()));
        setPlatformBlockInDirection(worldIn, platformBlock, blockToReplace, hivePos.offset(direction.getOpposite().getNormal()));
        worldIn.setBlock(hivePos, platformBlock, 1);
    }

    private static void setPlatformBlockInDirection(WorldGenLevel worldIn, BlockState platformBlock, Block blockToReplace, BlockPos posBlockPos) {
        if (worldIn.getBlockState(posBlockPos).getBlock().equals(blockToReplace))
            worldIn.setBlock(posBlockPos, platformBlock, 1);
    }

    private static BlockPos getYPos(WorldGenLevel worldIn, RandomSource rand, Holder<Biome> biome, BlockPos initPos) {
        if (biome.is(BiomeTags.IS_NETHER) || worldIn.dimensionType().hasCeiling()) {
            return getBlockPosForNetherBiomes(worldIn, rand, initPos);
        }
        BlockPos pos = worldIn.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, initPos);
        if ((biome.is(BiomeTags.IS_OCEAN) || biome.is(BiomeTags.IS_RIVER))
                && rand.nextInt(10) != 0
                && worldIn.getBlockState(pos.below()).getBlock().equals(Blocks.WATER)) {
            return BlockPos.ZERO;
        }
        return canPlaceOnBlockBelow(worldIn.getBlockState(pos.below())) ? pos : BlockPos.ZERO;
    }

    @NotNull
    private static BlockPos getBlockPosForNetherBiomes(WorldGenLevel worldIn, RandomSource rand, BlockPos initPos) {
        BlockPos newPos = initPos.south(rand.nextInt(15)).east(rand.nextInt(15));
        while (worldIn.isEmptyBlock(newPos.below())) {
            newPos = newPos.below();
        }
        while (!worldIn.isEmptyBlock(newPos)) {
            newPos = newPos.above();
        }
        //getHeight and getMaxHeight for nether dimension return 256. need to figure out how to get the roof height
        if (worldIn.isOutsideBuildHeight(newPos)) {
            return BlockPos.ZERO;
        }
        BlockState blockBelow = worldIn.getBlockState(newPos.below());
        if (!canPlaceOnBlockBelow(blockBelow)) {
            return BlockPos.ZERO;
        }
        if (blockBelow.getBlock().equals(Blocks.LAVA) && rand.nextInt(10) != 0) {
            return BlockPos.ZERO;
        }
        return newPos;
    }

    private static boolean canPlaceOnBlockBelow(BlockState state) {
        return state.is(ModTags.Blocks.NEST_PLACEABLE_ON);
    }

    private static Block selectNest(boolean headsOrTails, Block blockOne, Block blockTwo){
        return headsOrTails ? blockOne : blockTwo;
    }

    private static Block getNetherNest(boolean headsOrTails, @Nullable ResourceKey<Biome> biomeKey){
        if (Biomes.WARPED_FOREST.equals(biomeKey)) {
            return selectNest(headsOrTails, ModBlocks.WARPED_BEE_NEST.get(), ModBlocks.WARPED_NYLIUM_BEE_NEST.get());
        } else if (Biomes.CRIMSON_FOREST.equals(biomeKey)) {
            return selectNest(headsOrTails, ModBlocks.CRIMSON_BEE_NEST.get(), ModBlocks.CRIMSON_NYLIUM_BEE_NEST.get());
        }
        return selectNest(headsOrTails, ModBlocks.NETHER_BEE_NEST.get(), ModBlocks.WITHER_BEE_NEST.get());
    }

    private static boolean isFrozenBiome(@Nullable ResourceKey<Biome> biomeKey){
        return biomeKey != null && (biomeKey.equals(Biomes.FROZEN_OCEAN) || biomeKey.equals(Biomes.FROZEN_RIVER) || biomeKey.equals(Biomes.DEEP_FROZEN_OCEAN));
    }

    private static void logMissingBiome(ResourceKey<Biome> biomeKey) {
        //TODO determine if this is needed any longer
        if (biomeKey != null && CommonConfig.SHOW_DEBUG_INFO.get()) {
            ResourcefulBees.LOGGER.warn("*****************************************************");
            ResourcefulBees.LOGGER.warn("Could not load bees into nest during chunk generation");
            ResourcefulBees.LOGGER.warn("Biome: {}", biomeKey.location());
            ResourcefulBees.LOGGER.warn("*****************************************************");
        }
    }

    private static void addBeeToNest(CustomBeeEntityType<?> entityType, RandomSource rand, TieredBeehiveBlockEntity nest) {
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
        if (id != null) {
            CompoundTag tag = new CompoundTag();
            tag.putString(NBTConstants.NBT_ID, id.toString());
            int timeInHive = rand.nextInt(entityType.getData().coreData().maxTimeInHive());
            ((BeehiveEntityAccessor)nest).getBees().add(new BeehiveBlockEntity.BeeData(tag, 0, timeInHive));
        }
    }

    private static void setNestBees(BlockPos nestPos, Holder<Biome> holder, WorldGenLevel level, RandomSource rand){
        if (level.getBlockEntity(nestPos) instanceof TieredBeehiveBlockEntity nest) {
            int maxBees = Math.round(CommonConfig.HIVE_MAX_BEES.get() * 0.5f);

            Biome biome = holder.get();
            for (int i = rand.nextInt(maxBees); i < maxBees ; i++) {
                biome.getMobSettings()
                        .getMobs(ModConstants.BEE_MOB_CATEGORY)
                        .getRandom(rand)
                        .map(data -> data.type)
                        .filter(type -> type instanceof CustomBeeEntityType<?>)
                        .map(type -> (CustomBeeEntityType<?>) type)
                        .filter(bee -> ModSpawnData.getData(level, bee.getBeeType()).canSpawnAtY(nestPos.getY()))
                        .ifPresentOrElse(bee -> addBeeToNest(bee, rand, nest), () -> logMissingBiome(holder.unwrapKey().orElse(null)));
            }
        }
    }

    private static void generateHivePlatform(WorldGenLevel level, Holder<Biome> biomeHolder, Optional<ResourceKey<Biome>> biomeKey, BlockPos newPos, Direction direction) {
        BlockState platformBlockState = getNestPlatform(biomeHolder, biomeKey);
        BlockPos belowHive = newPos.offset(0,-1,0);
        if (level.getBlockState(belowHive).getBlock().equals(Blocks.WATER)) {
            generateHivePlatform(level, belowHive, platformBlockState, direction, Blocks.WATER);
        }
        if (biomeHolder.is(BiomeTags.IS_RIVER) && level.getBlockState(belowHive).getBlock().equals(Blocks.LAVA)) {
            generateHivePlatform(level, belowHive, platformBlockState, direction, Blocks.LAVA);
        }
    }

    private static Block getNest(Holder<Biome> biome, Optional<ResourceKey<Biome>> biomeKey, boolean headsOrTails) {
        if (biome.is(BiomeTags.IS_END)) {
            return ModBlocks.PURPUR_BEE_NEST.get();
        } else if (biome.is(BiomeTags.IS_NETHER)) {
            return getNetherNest(headsOrTails, biomeKey.orElse(null));
        } else if (biome.is(BiomeTags.IS_SAVANNA) || biome.is(Tags.Biomes.IS_DRY_OVERWORLD)) {
            return ModBlocks.ACACIA_BEE_NEST.get();
        } else if (biome.is(BiomeTags.IS_JUNGLE)) {
            return ModBlocks.JUNGLE_BEE_NEST.get();
        } else if (biome.is(BiomeTags.IS_BEACH) || biome.is(BiomeTags.IS_OCEAN)) {
            return ModBlocks.PRISMARINE_BEE_NEST.get();
        } else if (biome.is(Tags.Biomes.IS_COLD_OVERWORLD) || biome.is(Tags.Biomes.IS_CONIFEROUS)) {
            return ModBlocks.SPRUCE_BEE_NEST.get();
        } else if (biome.is(Tags.Biomes.IS_MUSHROOM)) {
            return selectNest(headsOrTails, ModBlocks.RED_MUSHROOM_BEE_NEST.get(), ModBlocks.BROWN_MUSHROOM_BEE_NEST.get());
        } else if (biome.is(Tags.Biomes.IS_SWAMP)) {
            return ModBlocks.OAK_BEE_NEST.get();
        } else if (biome.is(BiomeTags.IS_FOREST)) {
            return selectNest(headsOrTails, ModBlocks.BIRCH_BEE_NEST.get(), ModBlocks.DARK_OAK_BEE_NEST.get());
        }
        return overworldBlocks.next();
    }

    private static BlockState getNestPlatform(Holder<Biome> biome, Optional<ResourceKey<Biome>> biomeKey) {
        if (biome.is(BiomeTags.IS_END)) {
            return Blocks.END_STONE.defaultBlockState();
        } else if (biome.is(BiomeTags.IS_NETHER)) {
            return Blocks.OBSIDIAN.defaultBlockState();
        } else if (biome.is(BiomeTags.IS_SAVANNA) || biome.is(Tags.Biomes.IS_DRY_OVERWORLD)) {
            return Blocks.ACACIA_WOOD.defaultBlockState();
        } else if (biome.is(BiomeTags.IS_JUNGLE)) {
            return Blocks.JUNGLE_WOOD.defaultBlockState();
        } else if (biome.is(BiomeTags.IS_BEACH) || biome.is(BiomeTags.IS_OCEAN)) {
            return isFrozenBiome(biomeKey.orElse(null)) ? Blocks.PACKED_ICE.defaultBlockState() : Blocks.STRIPPED_OAK_WOOD.defaultBlockState();
        } else if (biome.is(Tags.Biomes.IS_COLD_OVERWORLD) || biome.is(BiomeTags.IS_TAIGA)) {
            return Blocks.PACKED_ICE.defaultBlockState();
        } else if (biome.is(Tags.Biomes.IS_SWAMP)) {
            return  Blocks.STRIPPED_SPRUCE_WOOD.defaultBlockState();
        } else if (biome.is(BiomeTags.IS_RIVER)) {
            return isFrozenBiome(biomeKey.orElse(null)) ? Blocks.PACKED_ICE.defaultBlockState() : Blocks.OAK_WOOD.defaultBlockState();
        }
        return Blocks.OAK_WOOD.defaultBlockState();
    }
}
