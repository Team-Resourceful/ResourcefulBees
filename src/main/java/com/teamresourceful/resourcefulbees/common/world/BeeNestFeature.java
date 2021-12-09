package com.teamresourceful.resourcefulbees.common.world;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.config.ClientConfig;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.BeehiveEntityAccessor;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.tileentity.TieredBeehiveTileEntity;
import com.teamresourceful.resourcefulbees.common.utils.RandomCollection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
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

import java.util.Random;

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

    private void generateHivePlatform(ISeedReader worldIn, BlockPos hivePos, BlockState platformBlock, Direction direction, Block blockToReplace) {
        if (platformBlock == null) platformBlock = Blocks.OAK_WOOD.defaultBlockState();

        BlockPos posBlockPos = hivePos.offset(direction.getNormal());
        BlockPos negBlockPos = hivePos.offset(direction.getOpposite().getNormal());

        if (worldIn.getBlockState(posBlockPos).getBlock().equals(blockToReplace))
            worldIn.setBlock(posBlockPos, platformBlock, 1);

        if (worldIn.getBlockState(negBlockPos).getBlock().equals(blockToReplace))
            worldIn.setBlock(negBlockPos, platformBlock, 1);

        worldIn.setBlock(hivePos, platformBlock, 1);
    }

    private BlockPos getYPos(ISeedReader worldIn, Random rand, Biome.Category category, BlockPos initPos){
        if (category == Biome.Category.NETHER || worldIn.dimensionType().hasCeiling()) {
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

        if ((category == Biome.Category.OCEAN || category == Biome.Category.RIVER)
                && rand.nextInt(10) != 0
                && worldIn.getBlockState(pos.below()).getBlock().equals(Blocks.WATER)) {
            return BlockPos.ZERO;
        }
        return pos;
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
        if (biomeKey != null && ClientConfig.SHOW_DEBUG_INFO.get()) {
            ResourcefulBees.LOGGER.warn("*****************************************************");
            ResourcefulBees.LOGGER.warn("Could not load bees into nest during chunk generation");
            ResourcefulBees.LOGGER.warn("Biome: {}", biomeKey.location());
            ResourcefulBees.LOGGER.warn("*****************************************************");
        }
    }

    private void addBeeToNest(@Nullable EntityType<?> entityType, ISeedReader worldIn, BlockPos nestPos, CustomBeeData data, Random rand, TieredBeehiveTileEntity nest){
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

    private void setNestBees(BlockPos nestPos, @Nullable RegistryKey<Biome> biomeKey, ISeedReader worldIn, Random rand){
        BlockEntity tileEntity = worldIn.getBlockEntity(nestPos);

        if (tileEntity instanceof TieredBeehiveTileEntity) {
            TieredBeehiveTileEntity nestTE = (TieredBeehiveTileEntity) tileEntity;
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
        return false;
    }

/*    @Override
    public boolean place(@NotNull ISeedReader worldIn, @NotNull ChunkGenerator generator, @NotNull Random rand, @NotNull BlockPos pos, @NotNull NoFeatureConfig config) {
        if(Boolean.FALSE.equals(CommonConfig.GENERATE_BEE_NESTS.get())) return false;

        Biome biome = worldIn.getBiome(pos);
        Optional<RegistryKey<Biome>> biomeKey = worldIn.getBiomeName(pos);
        Biome.Category category = biome.getBiomeCategory();

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
        BlockState platformBlockState = null;
        switch (category) {
            case THEEND:
                nest = ModBlocks.PURPUR_BEE_NEST.get();
                break;
            case NETHER:
                nest = getNetherNest(headsOrTails, biomeKey.orElse(null));
                platformBlockState = Blocks.OBSIDIAN.defaultBlockState();
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
                            Blocks.PACKED_ICE.defaultBlockState() : Blocks.STRIPPED_OAK_WOOD.defaultBlockState();
                break;
            case ICY:
            case TAIGA:
                platformBlockState = Blocks.PACKED_ICE.defaultBlockState();
                nest = ModBlocks.SPRUCE_BEE_NEST.get();
                break;
            case MUSHROOM:
                nest = selectNest(headsOrTails, ModBlocks.RED_MUSHROOM_BEE_NEST.get(), ModBlocks.BROWN_MUSHROOM_BEE_NEST.get());
                break;
            case SWAMP:
                nest = ModBlocks.OAK_BEE_NEST.get();
                platformBlockState = Blocks.STRIPPED_SPRUCE_WOOD.defaultBlockState();
                break;
            case FOREST:
                nest = selectNest(headsOrTails, ModBlocks.BIRCH_BEE_NEST.get(), ModBlocks.DARK_OAK_BEE_NEST.get());
                break;
            case RIVER:
                platformBlockState = isFrozenBiome(biomeKey.orElse(null)) ? Blocks.PACKED_ICE.defaultBlockState() : Blocks.OAK_WOOD.defaultBlockState();
                nest = overworldBlocks.next();
                break;
            default:
                platformBlockState = Blocks.OAK_WOOD.defaultBlockState();
                nest = overworldBlocks.next();
        }
        BlockState newState = nest.defaultBlockState().setValue(BeehiveBlock.FACING, direction);

        BlockPos belowHive = newPos.offset(0,-1,0);
        if (worldIn.getBlockState(belowHive).getBlock().equals(Blocks.WATER)) {
            generateHivePlatform(worldIn, belowHive, platformBlockState, direction, Blocks.WATER);
        }
        if (category.equals(Biome.Category.NETHER) && worldIn.getBlockState(belowHive).getBlock().equals(Blocks.LAVA)) {
            generateHivePlatform(worldIn, belowHive, platformBlockState, direction, Blocks.LAVA);
        }

        worldIn.setBlock(newPos, newState, 1);

        setNestBees(newPos, biomeKey.orElse(null), worldIn, rand);
        return true;
    }*/
}
