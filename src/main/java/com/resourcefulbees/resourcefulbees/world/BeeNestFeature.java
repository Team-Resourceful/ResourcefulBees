package com.resourcefulbees.resourcefulbees.world;

import com.mojang.serialization.Codec;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.tileentity.TieredBeehiveTileEntity;
import com.resourcefulbees.resourcefulbees.utils.MathUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.resourcefulbees.resourcefulbees.registry.RegistryHandler.*;

public class BeeNestFeature extends Feature<NoFeatureConfig> {

    private final List<Block> overworldBiomes = new ArrayList<>(Arrays.asList(GRASS_BEE_NEST.get(), OAK_BEE_NEST.get(), DARK_OAK_BEE_NEST.get(), SPRUCE_BEE_NEST.get(), BIRCH_BEE_NEST.get(), RED_MUSHROOM_BEE_NEST.get(), BROWN_MUSHROOM_BEE_NEST.get()));

    public BeeNestFeature(Codec<NoFeatureConfig> configFactoryIn) {
        super(configFactoryIn);
    }

    @Override
    public boolean generate(@Nonnull ISeedReader worldIn, @Nonnull ChunkGenerator generator, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull NoFeatureConfig config) {
        Biome biome = worldIn.getBiome(pos);
        Biome.Category category = biome.getCategory();

        if(!Config.GENERATE_BEE_NESTS.get() || rand.nextDouble() > MathUtils.clamp(Config.BEE_NEST_GENERATION_WEIGHT.get(), 0, 1))
            return false;

        boolean flag = rand.nextBoolean();
        BlockPos newPos;
        int y;
        if (category == Biome.Category.NETHER) {
            y = MathUtils.nextIntInclusive(32, 100);
            newPos = new BlockPos(pos.getX(), y, pos.getZ());
            while (worldIn.isAirBlock(newPos.down())){
                newPos = newPos.down();
            }
            while (!worldIn.isAirBlock(newPos)){
                newPos = newPos.up();
            }
            if (newPos.getY() > 100)
                return false;
            if (worldIn.getBlockState(newPos.down()).getBlock().equals(Blocks.LAVA))
                if (rand.nextInt(10) != 10)
                    return false;
        }
        else {
            y = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, pos.getX(), pos.getZ());
            newPos = new BlockPos(pos.getX(), y, pos.getZ());
        }
        if (newPos.getY() == 0)
            return false;

        Direction direction = Direction.UP;
        for (Direction dir : BlockStateProperties.FACING.getAllowedValues()) {
            BlockPos blockPos = newPos.offset(dir, 1);
            if (worldIn.isAirBlock(blockPos)) {
                direction = dir;
                break;
            }
        }

        if (direction == Direction.UP)
            return false;

        Block block;
        BlockState newState;

        switch (category) {
            case THEEND:
                block = PURPUR_BEE_NEST.get();
                break;
            case NETHER:
                block = flag ? NETHER_BEE_NEST.get() : WITHER_BEE_NEST.get();
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
                block = flag ? RED_MUSHROOM_BEE_NEST.get() : BROWN_MUSHROOM_BEE_NEST.get();
                break;
            case SWAMP:
                block = OAK_BEE_NEST.get();
                break;
            case FOREST:
                block = flag ? BIRCH_BEE_NEST.get() : DARK_OAK_BEE_NEST.get();
                break;
            case PLAINS:
                block = flag ? OAK_BEE_NEST.get() : GRASS_BEE_NEST.get();
                break;
            default:
                if (Biomes.WARPED_FOREST.equals(biome)) {
                    block = flag ? WARPED_BEE_NEST.get() : WARPED_NYLIUM_BEE_NEST.get();
                } else if (Biomes.CRIMSON_FOREST.equals(biome)) {
                    block = flag ? CRIMSON_BEE_NEST.get() : CRIMSON_NYLIUM_BEE_NEST.get();
                } else {
                    block = flag ? OAK_BEE_NEST.get() : GRASS_BEE_NEST.get();
                }
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
            // TODO change to dynamically pick bee registry ID from spawnable biomes map
            int maxBees = Math.round(Config.HIVE_MAX_BEES.get() * 0.5f);  //nest.getMaxBees();
/*            for (int i = rand.nextInt(maxBees); i < maxBees ; i++) {
                ResourcefulBee bee = CUSTOM_BEE.get().create(worldIn.getWorld()); //TODO <---- This line here!!!
                if (bee != null) {
                    bee.setPosition(newPos.getX(), newPos.getY(), newPos.getZ());
                    bee.setBeeType(true);
                    CompoundNBT compoundNBT = new CompoundNBT();
                    bee.writeUnlessPassenger(compoundNBT);
                    int timeinhive = rand.nextInt(bee.getBeeData().getMaxTimeInHive());
                    TieredBeehiveTileEntity.Bee beehivetileentity$bee = new TieredBeehiveTileEntity.Bee(compoundNBT, 0, timeinhive);
                    nest.bees.add(beehivetileentity$bee);
                }
            }*/
        }
        return true;
    }
}
