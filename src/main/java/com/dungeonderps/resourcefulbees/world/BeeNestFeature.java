package com.dungeonderps.resourcefulbees.world;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.config.Config;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.tileentity.BeeNestEntity;
import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.ResourcefulBee;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.common.BiomeDictionary;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.function.Function;

public class BeeNestFeature extends Feature<NoFeatureConfig> {

    public BeeNestFeature(Function<Dynamic<?>, NoFeatureConfig> configFactoryIn) {
        super(configFactoryIn);
    }



    @Override
    public boolean place(@Nonnull IWorld worldIn, @Nonnull ChunkGenerator<? extends GenerationSettings> generator, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull NoFeatureConfig config) {
        if(!Config.GENERATE_BEE_NESTS.get() || rand.nextDouble() > Config.BEE_NEST_GENERATION_WEIGHT.get())
            return false;

        pos = pos.south(rand.nextInt(16)).east(rand.nextInt(16));

        while (pos.getY() < 50 || !worldIn.isAirBlock(pos)) {
            pos = pos.up();
        }

        Direction direction = Direction.UP;
        for (Direction dir : BlockStateProperties.FACING.getAllowedValues()) {
            BlockPos blockPos = pos.offset(dir, 1);
            if (worldIn.isAirBlock(blockPos)) {
                direction = dir;
                break;
            }
        }

        if (direction == Direction.UP)
            return false;

        Biome biome = worldIn.getBiome(pos);
        Biome.Category category = biome.getCategory();
        BlockState newState;
        boolean flag = rand.nextBoolean();
        switch (category) {
            case THEEND:
                newState = RegistryHandler.PURPUR_BEE_NEST.get().getDefaultState();
                break;
            case NETHER:
                if (flag)
                    newState = RegistryHandler.NETHER_BEE_NEST.get().getDefaultState();
                else
                    newState = RegistryHandler.WITHER_BEE_NEST.get().getDefaultState();
                break;
            case SAVANNA:
            case DESERT:
            case MESA:
                newState = RegistryHandler.ACACIA_BEE_NEST.get().getDefaultState();
                break;
            case JUNGLE:
                newState = RegistryHandler.JUNGLE_BEE_NEST.get().getDefaultState();
                break;
            case BEACH:
            case OCEAN:
            case ICY:
                newState = RegistryHandler.PRISMARINE_BEE_NEST.get().getDefaultState();
                break;
            case MUSHROOM:
            case TAIGA:
            case SWAMP:
                newState = RegistryHandler.BEE_NEST.get().getDefaultState();
                break;
            default:
                if (flag)
                    newState = RegistryHandler.BEE_NEST.get().getDefaultState();
                else
                    newState = RegistryHandler.GRASS_BEE_NEST.get().getDefaultState();
        }
        worldIn.setBlockState(pos, newState, 1);


        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof BeeNestEntity) {
            BeeNestEntity nest = (BeeNestEntity) tileEntity;
            int maxBees = Math.round(Config.HIVE_MAX_BEES.get() * nest.getTierModifier());
            for (int i = rand.nextInt(maxBees); i < maxBees ; i++) {
                ResourcefulBee bee = RegistryHandler.CUSTOM_BEE.get().create(worldIn.getWorld());
                if (bee != null) {
                    bee.setBeeType(true);
                    bee.setPosition(pos.getX(), pos.getY(), pos.getZ());
                    nest.tryEnterHive(bee, false, rand.nextInt(bee.getBeeInfo().getMaxTimeInHive()));
                }
            }

        }


        return true;
    }
}
