package com.teamresourceful.resourcefulbees.common.world;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.config.ClientConfig;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.BeehiveEntityAccessor;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.blockentity.TieredBeehiveBlockEntity;
import com.teamresourceful.resourcefulbees.common.utils.RandomCollection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class BeeNestFeature extends Feature<NoneFeatureConfiguration> {

    private static final RandomCollection<Block> overworldBlocks = new RandomCollection<>();

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

    private void generateHivePlatform(WorldGenLevel worldIn, BlockPos hivePos, BlockState platformBlock, Direction direction, Block blockToReplace) {
        if (platformBlock == null) platformBlock = Blocks.OAK_WOOD.defaultBlockState();

        BlockPos posBlockPos = hivePos.offset(direction.getNormal());
        BlockPos negBlockPos = hivePos.offset(direction.getOpposite().getNormal());

        if (worldIn.getBlockState(posBlockPos).getBlock().equals(blockToReplace))
            worldIn.setBlock(posBlockPos, platformBlock, 1);

        if (worldIn.getBlockState(negBlockPos).getBlock().equals(blockToReplace))
            worldIn.setBlock(negBlockPos, platformBlock, 1);

        worldIn.setBlock(hivePos, platformBlock, 1);
    }

    private BlockPos getYPos(WorldGenLevel worldIn, Random rand, Biome.BiomeCategory category, BlockPos initPos){
        if (category == Biome.BiomeCategory.NETHER || worldIn.dimensionType().hasCeiling()) {
            int ceilHeight = worldIn.getHeight();
            BlockPos newPos = new BlockPos(initPos.getX(), rand.nextInt(ceilHeight - 33) + 32, initPos.getZ())
                    .south(rand.nextInt(15))
                    .east(rand.nextInt(15));
            while (worldIn.isEmptyBlock(newPos.below())) {
                newPos = newPos.below();
            }
            while (!worldIn.isEmptyBlock(newPos)) {
                newPos = newPos.above();
            }
            if (newPos.getY() >= ceilHeight) {
                return BlockPos.ZERO;
            }
            if (worldIn.getBlockState(newPos.below()).getBlock().equals(Blocks.LAVA) && rand.nextInt(10) != 0) {
                return BlockPos.ZERO;
            }
            return newPos;
        }

        BlockPos pos = worldIn.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, initPos);

        if ((category == Biome.BiomeCategory.OCEAN || category == Biome.BiomeCategory.RIVER)
                && rand.nextInt(10) != 0
                && worldIn.getBlockState(pos.below()).getBlock().equals(Blocks.WATER)) {
            return BlockPos.ZERO;
        }
        return pos;
    }

    private Block selectNest(boolean headsOrTails, Block blockOne, Block blockTwo){
        return headsOrTails ? blockOne : blockTwo;
    }

    private Block getNetherNest(boolean headsOrTails, @Nullable ResourceKey<Biome> biomeKey){
        if (Biomes.WARPED_FOREST.equals(biomeKey)) {
            return selectNest(headsOrTails, ModBlocks.WARPED_BEE_NEST.get(), ModBlocks.WARPED_NYLIUM_BEE_NEST.get());
        } else if (Biomes.CRIMSON_FOREST.equals(biomeKey)) {
            return selectNest(headsOrTails, ModBlocks.CRIMSON_BEE_NEST.get(), ModBlocks.CRIMSON_NYLIUM_BEE_NEST.get());
        } else {
            return selectNest(headsOrTails, ModBlocks.NETHER_BEE_NEST.get(), ModBlocks.WITHER_BEE_NEST.get());
        }
    }

    private boolean isFrozenBiome(@Nullable ResourceKey<Biome> biomeKey){
        return biomeKey != null && (biomeKey.equals(Biomes.FROZEN_OCEAN) || biomeKey.equals(Biomes.FROZEN_RIVER) || biomeKey.equals(Biomes.DEEP_FROZEN_OCEAN));
    }

    private void logMissingBiome(ResourceKey<Biome> biomeKey){
        if (biomeKey != null && ClientConfig.SHOW_DEBUG_INFO.get()) {
            ResourcefulBees.LOGGER.warn("*****************************************************");
            ResourcefulBees.LOGGER.warn("Could not load bees into nest during chunk generation");
            ResourcefulBees.LOGGER.warn("Biome: {}", biomeKey.location());
            ResourcefulBees.LOGGER.warn("*****************************************************");
        }
    }

    private void addBeeToNest(@Nullable EntityType<?> entityType, WorldGenLevel worldIn, BlockPos nestPos, CustomBeeData data, Random rand, TieredBeehiveBlockEntity nest){
        if (entityType != null) {
            Entity bee = entityType.create(worldIn.getLevel());
            if (bee != null) {
                bee.setPos(nestPos.getX(), nestPos.getY(), nestPos.getZ());
                CompoundTag compoundNBT = new CompoundTag();
                bee.save(compoundNBT);
                int timeInHive = rand.nextInt(data.getCoreData().getMaxTimeInHive());
                BeehiveBlockEntity.BeeData beehiveTileEntityBee = new BeehiveBlockEntity.BeeData(compoundNBT, 0, timeInHive);
                ((BeehiveEntityAccessor)nest).getBees().add(beehiveTileEntityBee);
            }
        }
    }

    private void setNestBees(BlockPos nestPos, @Nullable ResourceKey<Biome> biomeKey, WorldGenLevel worldIn, Random rand){
        BlockEntity tileEntity = worldIn.getBlockEntity(nestPos);

        if (tileEntity instanceof TieredBeehiveBlockEntity nestTE) {
            int maxBees = Math.round(CommonConfig.HIVE_MAX_BEES.get() * 0.5f);

            for (int i = rand.nextInt(maxBees); i < maxBees ; i++) {
                if (biomeKey != null && BeeRegistry.isSpawnableBiome(biomeKey.location())) {
                    CustomBeeData beeData = BeeRegistry.getSpawnableBiome(biomeKey.location()).next();
                    if (beeData.getSpawnData().canSpawnAtYLevel(nestPos)) continue;
                    EntityType<?> entityType = beeData.getEntityType();
                    addBeeToNest(entityType, worldIn, nestPos, beeData, rand, nestTE);
                } else logMissingBiome(biomeKey);
            }
        }
    }

    //TODO rewrite the Nest Feature!!!!
    @Override
    public boolean place(@NotNull FeaturePlaceContext<NoneFeatureConfiguration> context) {
        if(Boolean.FALSE.equals(CommonConfig.GENERATE_BEE_NESTS.get())) return false;

        WorldGenLevel worldIn = context.level();
        BlockPos pos = context.origin();
        Random rand = context.random();

        Holder<Biome> biome = worldIn.getBiome(pos);
        Optional<ResourceKey<Biome>> biomeKey = biome.unwrapKey();
        Biome.BiomeCategory category = Biome.getBiomeCategory(biome);

        boolean headsOrTails = rand.nextBoolean();
        BlockPos newPos = getYPos(worldIn, rand, category, pos);

        if (newPos.getY() == 0) return false;

        Direction direction;

        BlockPos finalNewPos = newPos.mutable();
        List<Direction> possibleDirections = Direction.Plane.HORIZONTAL.stream()
                .filter(dir -> worldIn.isEmptyBlock(finalNewPos.relative(dir, 1)))
                .collect(Collectors.toList());

        if(!possibleDirections.isEmpty()) {
            direction = possibleDirections.get(rand.nextInt(possibleDirections.size()));
        } else {
            return false;
        }

        Block nest;
        BlockState platformBlockState;
        switch (category) {
            case THEEND -> {
                nest = ModBlocks.PURPUR_BEE_NEST.get();
                platformBlockState = Blocks.END_STONE.defaultBlockState();
            }
            case NETHER -> {
                nest = getNetherNest(headsOrTails, biomeKey.orElse(null));
                platformBlockState = Blocks.OBSIDIAN.defaultBlockState();
            }
            case SAVANNA, DESERT, MESA -> {
                nest = ModBlocks.ACACIA_BEE_NEST.get();
                platformBlockState = Blocks.ACACIA_WOOD.defaultBlockState();
            }
            case JUNGLE -> {
                nest = ModBlocks.JUNGLE_BEE_NEST.get();
                platformBlockState = Blocks.JUNGLE_WOOD.defaultBlockState();
            }
            case BEACH, OCEAN -> {
                nest = ModBlocks.PRISMARINE_BEE_NEST.get();
                platformBlockState = isFrozenBiome(biomeKey.orElse(null)) ?
                        Blocks.PACKED_ICE.defaultBlockState() : Blocks.STRIPPED_OAK_WOOD.defaultBlockState();
            }
            case ICY, TAIGA -> {
                platformBlockState = Blocks.PACKED_ICE.defaultBlockState();
                nest = ModBlocks.SPRUCE_BEE_NEST.get();
            }
            case MUSHROOM -> {
                nest = selectNest(headsOrTails, ModBlocks.RED_MUSHROOM_BEE_NEST.get(), ModBlocks.BROWN_MUSHROOM_BEE_NEST.get());
                platformBlockState = Blocks.OAK_WOOD.defaultBlockState();
            }
            case SWAMP -> {
                nest = ModBlocks.OAK_BEE_NEST.get();
                platformBlockState = Blocks.STRIPPED_SPRUCE_WOOD.defaultBlockState();
            }
            case FOREST -> {
                nest = selectNest(headsOrTails, ModBlocks.BIRCH_BEE_NEST.get(), ModBlocks.DARK_OAK_BEE_NEST.get());
                platformBlockState = Blocks.OAK_WOOD.defaultBlockState();
            }
            case RIVER -> {
                platformBlockState = isFrozenBiome(biomeKey.orElse(null)) ? Blocks.PACKED_ICE.defaultBlockState() : Blocks.OAK_WOOD.defaultBlockState();
                nest = overworldBlocks.next();
            }
            default -> {
                platformBlockState = Blocks.OAK_WOOD.defaultBlockState();
                nest = overworldBlocks.next();
            }
        }
        BlockState newState = nest.defaultBlockState().setValue(BeehiveBlock.FACING, direction);

        BlockPos belowHive = newPos.offset(0,-1,0);
        if (worldIn.getBlockState(belowHive).getBlock().equals(Blocks.WATER)) {
            generateHivePlatform(worldIn, belowHive, platformBlockState, direction, Blocks.WATER);
        }
        if (category.equals(Biome.BiomeCategory.NETHER) && worldIn.getBlockState(belowHive).getBlock().equals(Blocks.LAVA)) {
            generateHivePlatform(worldIn, belowHive, platformBlockState, direction, Blocks.LAVA);
        }

        worldIn.setBlock(newPos, newState, 1);

        setNestBees(newPos, biomeKey.orElse(null), worldIn, rand);
        return true;
    }
}
