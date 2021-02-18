package com.resourcefulbees.resourcefulbees.world;

import com.mojang.serialization.Codec;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModBlocks;
import com.resourcefulbees.resourcefulbees.tileentity.TieredBeehiveTileEntity;
import com.resourcefulbees.resourcefulbees.utils.MathUtils;
import com.resourcefulbees.resourcefulbees.utils.RandomCollection;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class BeeNestFeature extends Feature<NoFeatureConfig> {

    private static final EnumSet<Direction> ALLOWED_DIRECTIONS = EnumSet.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);

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

    public BeeNestFeature(Codec<NoFeatureConfig> configFactoryIn) { super(configFactoryIn); }

    private void generateHivePlatform(ISeedReader worldIn, BlockPos hivePos, BlockState platformBlock, Direction direction, Block blockToReplace){
        if (platformBlock == null) platformBlock = Blocks.OAK_WOOD.getDefaultState();

        BlockPos posBlockPos = hivePos.add(direction.getDirectionVec());
        BlockPos negBlockPos = hivePos.add(direction.getOpposite().getDirectionVec());

        if (worldIn.getBlockState(posBlockPos).getBlock().equals(blockToReplace))
            worldIn.setBlockState(posBlockPos, platformBlock, 1);

        if (worldIn.getBlockState(negBlockPos).getBlock().equals(blockToReplace))
            worldIn.setBlockState(negBlockPos, platformBlock, 1);

        worldIn.setBlockState(hivePos, platformBlock, 1);
    }

    private BlockPos getYPos(ISeedReader worldIn, Random rand, Biome.Category category, BlockPos initPos){
        BlockPos newPos;
        if (category == Biome.Category.NETHER || worldIn.getDimension().hasCeiling()) {
            int ceilHeight = worldIn.getDimensionHeight();
            newPos = new BlockPos(initPos.getX(), MathUtils.nextIntInclusive(32, ceilHeight), initPos.getZ())
                    .south(rand.nextInt(15))
                    .east(rand.nextInt(15));
            while (worldIn.isAirBlock(newPos.down())) {
                newPos = newPos.down();
            }
            while (!worldIn.isAirBlock(newPos)) {
                newPos = newPos.up();
            }
            if (newPos.getY() >= ceilHeight) {
                return new BlockPos(0,0,0);
            }
            if (worldIn.getBlockState(newPos.down()).getBlock().equals(net.minecraft.block.Blocks.LAVA) &&
                    rand.nextInt(10) != 0
            ){
                return new BlockPos(0,0,0);
            }
        } else {
            int y = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, initPos.getX(), initPos.getZ());
            newPos = new BlockPos(initPos.getX(), y, initPos.getZ());
        }
        return newPos;
    }

    private Block selectNest(boolean headsOrTails, Block blockOne, Block blockTwo){
        return headsOrTails ? blockOne : blockTwo;
    }

    private Block getNetherNest(boolean headsOrTails, @Nullable RegistryKey<Biome> biomeKey){
        if (Biomes.WARPED_FOREST.equals(biomeKey)) {
            return selectNest(headsOrTails, ModBlocks.WARPED_BEE_NEST.get(), ModBlocks.WARPED_NYLIUM_BEE_NEST.get());
        } else if (Biomes.CRIMSON_FOREST.equals(biomeKey)) {
            return selectNest(headsOrTails, ModBlocks.CRIMSON_BEE_NEST.get(), ModBlocks.CRIMSON_NYLIUM_BEE_NEST.get());
        } else {
            return selectNest(headsOrTails, ModBlocks.NETHER_BEE_NEST.get(), ModBlocks.WITHER_BEE_NEST.get());
        }
    }

    private boolean isFrozenBiome(@Nullable RegistryKey<Biome> biomeKey){
        return biomeKey != null && (biomeKey.equals(Biomes.FROZEN_OCEAN) || biomeKey.equals(Biomes.FROZEN_RIVER) || biomeKey.equals(Biomes.DEEP_FROZEN_OCEAN));
    }

    private void logMissingBiome(RegistryKey<Biome> biomeKey){
        if (biomeKey != null && Config.SHOW_DEBUG_INFO.get()) {
            ResourcefulBees.LOGGER.warn("*****************************************************");
            ResourcefulBees.LOGGER.warn("Could not load bees into nest during chunk generation");
            ResourcefulBees.LOGGER.warn("Biome: {}", biomeKey.getValue());
            ResourcefulBees.LOGGER.warn("*****************************************************");
        }
    }

    private void addBeeToNest(@Nullable EntityType<?> entityType, ISeedReader worldIn, BlockPos nestPos, CustomBeeData data, Random rand, TieredBeehiveTileEntity nest){
        if (entityType != null) {
            Entity bee = entityType.create(worldIn.getWorld());
            if (bee != null) {
                bee.setPosition(nestPos.getX(), nestPos.getY(), nestPos.getZ());
                CompoundNBT compoundNBT = new CompoundNBT();
                bee.writeUnlessPassenger(compoundNBT);
                int timeInHive = rand.nextInt(data.getMaxTimeInHive());
                BeehiveTileEntity.Bee beehiveTileEntityBee = new BeehiveTileEntity.Bee(compoundNBT, 0, timeInHive);
                nest.bees.add(beehiveTileEntityBee);
            }
        }
    }

    private void setNestBees(BlockPos nestPos, @Nullable RegistryKey<Biome> biomeKey, ISeedReader worldIn, Random rand){
        TileEntity tileEntity = worldIn.getTileEntity(nestPos);

        if (tileEntity instanceof TieredBeehiveTileEntity) {
            TieredBeehiveTileEntity nestTE = (TieredBeehiveTileEntity) tileEntity;
            int maxBees = Math.round(Config.HIVE_MAX_BEES.get() * 0.5f);

            for (int i = rand.nextInt(maxBees); i < maxBees ; i++) {
                if (biomeKey != null && BeeRegistry.SPAWNABLE_BIOMES.containsKey(biomeKey.getValue())) {
                    CustomBeeData beeData = BeeRegistry.SPAWNABLE_BIOMES.get(biomeKey.getValue()).next();
                    EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(beeData.getEntityTypeRegistryID());
                    addBeeToNest(entityType, worldIn, nestPos, beeData, rand, nestTE);
                } else logMissingBiome(biomeKey);
            }
        }
    }


    @Override
    public boolean generate(@NotNull ISeedReader worldIn, @NotNull ChunkGenerator generator, @NotNull Random rand, @NotNull BlockPos pos, @NotNull NoFeatureConfig config) {
        if(!Config.GENERATE_BEE_NESTS.get()) {
            return false;
        }

        Biome biome = worldIn.getBiome(pos);
        Optional<RegistryKey<Biome>> biomeKey = worldIn.method_31081(pos);
        Biome.Category category = biome.getCategory();

        boolean headsOrTails = rand.nextBoolean();
        BlockPos newPos = getYPos(worldIn, rand, category, pos);

        if (newPos.getY() == 0) {
            return false;
        }

        Direction direction;

        BlockPos finalNewPos = newPos.mutableCopy();
        List<Direction> possibleDirections = ALLOWED_DIRECTIONS.stream()
                .filter(dir -> worldIn.isAirBlock(finalNewPos.offset(dir, 1)))
                .collect(Collectors.toList());

        if(!possibleDirections.isEmpty()) {
            direction = possibleDirections.get(rand.nextInt(possibleDirections.size()));
        } else {
            return false;
        }


        Block nest;
        BlockState platformBlockState = null;
        switch (category) {
            case THEEND:
                nest = ModBlocks.PURPUR_BEE_NEST.get();
                break;
            case NETHER:
                nest = getNetherNest(headsOrTails, biomeKey.orElse(null));
                platformBlockState = Blocks.OBSIDIAN.getDefaultState();
                break;
            case SAVANNA:
            case DESERT:
            case MESA:
                nest = ModBlocks.ACACIA_BEE_NEST.get();
                break;
            case JUNGLE:
                nest = ModBlocks.JUNGLE_BEE_NEST.get();
                break;
            case BEACH:
            case OCEAN:
                nest = ModBlocks.PRISMARINE_BEE_NEST.get();
                    platformBlockState = isFrozenBiome(biomeKey.orElse(null)) ?
                            Blocks.PACKED_ICE.getDefaultState() : Blocks.STRIPPED_OAK_WOOD.getDefaultState();
                break;
            case ICY:
            case TAIGA:
                platformBlockState = Blocks.PACKED_ICE.getDefaultState();
                nest = ModBlocks.SPRUCE_BEE_NEST.get();
                break;
            case MUSHROOM:
                nest = selectNest(headsOrTails, ModBlocks.RED_MUSHROOM_BEE_NEST.get(), ModBlocks.BROWN_MUSHROOM_BEE_NEST.get());
                break;
            case SWAMP:
                nest = ModBlocks.OAK_BEE_NEST.get();
                platformBlockState = Blocks.STRIPPED_SPRUCE_WOOD.getDefaultState();
                break;
            case FOREST:
                nest = selectNest(headsOrTails, ModBlocks.BIRCH_BEE_NEST.get(), ModBlocks.DARK_OAK_BEE_NEST.get());
                break;
            case RIVER:
                platformBlockState = isFrozenBiome(biomeKey.orElse(null)) ? Blocks.PACKED_ICE.getDefaultState() : Blocks.OAK_WOOD.getDefaultState();
                nest = overworldBlocks.next();
                break;
            default:
                platformBlockState = Blocks.OAK_WOOD.getDefaultState();
                nest = overworldBlocks.next();
        }
        BlockState newState = nest.getDefaultState().with(BeehiveBlock.FACING, direction);

        BlockPos belowHive = newPos.add(0,-1,0);
        if (worldIn.getBlockState(belowHive).getBlock().equals(Blocks.WATER)) {
            generateHivePlatform(worldIn, belowHive, platformBlockState, direction, Blocks.WATER);
        }
        if (category.equals(Biome.Category.NETHER) && worldIn.getBlockState(belowHive).getBlock().equals(Blocks.LAVA)) {
            generateHivePlatform(worldIn, belowHive, platformBlockState, direction, Blocks.LAVA);
        }

        worldIn.setBlockState(newPos, newState, 1);

        setNestBees(newPos, biomeKey.orElse(null), worldIn, rand);
        return true;
    }
}
