package com.resourcefulbees.resourcefulbees.world;

import com.mojang.serialization.Codec;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.tileentity.TieredBeehiveTileEntity;
import com.resourcefulbees.resourcefulbees.utils.MathUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
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

import javax.annotation.Nonnull;
import java.util.*;

import static com.resourcefulbees.resourcefulbees.registry.RegistryHandler.*;

public class BeeNestFeature extends Feature<NoFeatureConfig> {

    private final List<Block> overworldBiomes = new ArrayList<>(Arrays.asList(GRASS_BEE_NEST.get(), OAK_BEE_NEST.get(), DARK_OAK_BEE_NEST.get(), SPRUCE_BEE_NEST.get(), BIRCH_BEE_NEST.get(), RED_MUSHROOM_BEE_NEST.get(), BROWN_MUSHROOM_BEE_NEST.get()));

    public BeeNestFeature(Codec<NoFeatureConfig> configFactoryIn) {
        super(configFactoryIn);
    }

    @Override
    public boolean generate(@Nonnull ISeedReader worldIn, @Nonnull ChunkGenerator generator, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull NoFeatureConfig config) {
        Biome biome = worldIn.getBiome(pos);
        Optional<RegistryKey<Biome>> biomeKey = worldIn.method_31081(pos);
        Biome.Category category = biome.getCategory();

        if(!Config.GENERATE_BEE_NESTS.get()) {
            return false;
        }

        boolean headsOrTails = rand.nextBoolean();
        BlockPos newPos;
        int y;

        if (category == Biome.Category.NETHER) {
            y = MathUtils.nextIntInclusive(32, 100);
            newPos = new BlockPos(pos.getX(), y, pos.getZ())
                    .south(rand.nextInt(15)).east(rand.nextInt(15));
            while (worldIn.isAirBlock(newPos.down())) {
                newPos = newPos.down();
            }
            while (!worldIn.isAirBlock(newPos)) {
                newPos = newPos.up();
            }
            if (newPos.getY() > 100) {
                return false;
            }
            if (worldIn.getBlockState(newPos.down()).getBlock().equals(Blocks.LAVA)) {
                if (rand.nextInt(11) != 5) {
                    return false;
                }
            }
        } else {
            y = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, pos.getX(), pos.getZ());
            newPos = new BlockPos(pos.getX(), y, pos.getZ());
        }

        if (newPos.getY() == 0) {
            return false;
        }

        Direction direction = Direction.UP;
        for (Direction dir : BlockStateProperties.FACING.getAllowedValues()) {
            BlockPos blockPos = newPos.offset(dir, 1);
            if (worldIn.isAirBlock(blockPos)) {
                direction = dir;
                break;
            }
        }

        if (direction == Direction.UP) {
            return false;
        }

        Block block;
        BlockState newState;

        switch (category) {
            case THEEND:
                block = PURPUR_BEE_NEST.get();
                break;
            case NETHER:
                if (Biomes.WARPED_FOREST.equals(biomeKey.orElse(null))) {
                    block = headsOrTails ? WARPED_BEE_NEST.get() : WARPED_NYLIUM_BEE_NEST.get();
                } else if (Biomes.CRIMSON_FOREST.equals(biomeKey.orElse(null))) {
                    block = headsOrTails ? CRIMSON_BEE_NEST.get() : CRIMSON_NYLIUM_BEE_NEST.get();
                } else {
                    block = headsOrTails ? NETHER_BEE_NEST.get() : WITHER_BEE_NEST.get();
                }
                break;
            case SAVANNA:
            case DESERT:
            case MESA:
                block = ACACIA_BEE_NEST.get();
                break;
            case JUNGLE:
                block = JUNGLE_BEE_NEST.get();
                break;
            case BEACH:
            case OCEAN:
            case ICY:
                block = PRISMARINE_BEE_NEST.get();
                break;
            case TAIGA:
                block = SPRUCE_BEE_NEST.get();
                break;
            case MUSHROOM:
                block = headsOrTails ? RED_MUSHROOM_BEE_NEST.get() : BROWN_MUSHROOM_BEE_NEST.get();
                break;
            case SWAMP:
                block = OAK_BEE_NEST.get();
                break;
            case FOREST:
                block = headsOrTails ? BIRCH_BEE_NEST.get() : DARK_OAK_BEE_NEST.get();
                break;
            default:
                block = headsOrTails ? OAK_BEE_NEST.get() : GRASS_BEE_NEST.get();
        }

        if (!Biome.Category.NETHER.equals(category) && !Biome.Category.THEEND.equals(category)) {
            if (rand.nextFloat() < 0.20) {
                int selection = rand.nextInt(overworldBiomes.size() - 1) + 1;
                block = overworldBiomes.get(selection);
            }
        }

        newState = block.getStateContainer().getBaseState();

        worldIn.setBlockState(newPos, newState, 1);
        TileEntity tileEntity = worldIn.getTileEntity(newPos);

        if (tileEntity instanceof TieredBeehiveTileEntity) {
            TieredBeehiveTileEntity nest = (TieredBeehiveTileEntity) tileEntity;
            int maxBees = Math.round(Config.HIVE_MAX_BEES.get() * 0.5f);
            for (int i = rand.nextInt(maxBees); i < maxBees ; i++) {
                if (biomeKey.isPresent() && BeeRegistry.SPAWNABLE_BIOMES.containsKey(biomeKey.get().getValue())) {
                    CustomBeeData beeData = BeeRegistry.SPAWNABLE_BIOMES.get(biomeKey.get().getValue()).next();
                    EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(beeData.getEntityTypeRegistryID());
                    if (entityType != null) {
                        Entity bee = entityType.create(worldIn.getWorld());
                        if (bee != null) {
                            bee.setPosition(newPos.getX(), newPos.getY(), newPos.getZ());
                            CompoundNBT compoundNBT = new CompoundNBT();
                            bee.writeUnlessPassenger(compoundNBT);
                            int timeinhive = rand.nextInt(beeData.getMaxTimeInHive());
                            TieredBeehiveTileEntity.Bee beehivetileentity$bee = new TieredBeehiveTileEntity.Bee(compoundNBT, 0, timeinhive);
                            nest.bees.add(beehivetileentity$bee);
                        }
                    }
                } else if (biomeKey.isPresent() && Config.SHOW_DEBUG_INFO.get()) {
                    ResourcefulBees.LOGGER.warn("*****************************************************");
                    ResourcefulBees.LOGGER.warn("Could not load bees into nest during chunk generation");
                    ResourcefulBees.LOGGER.warn("Biome: " + biomeKey.get().getValue());
                    ResourcefulBees.LOGGER.warn("*****************************************************");
                }
            }
        }
        return true;
    }
}
