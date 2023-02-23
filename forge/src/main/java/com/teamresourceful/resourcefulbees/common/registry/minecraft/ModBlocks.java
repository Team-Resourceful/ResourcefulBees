package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.common.block.*;
import com.teamresourceful.resourcefulbees.common.block.centrifuge.CentrifugeBlock;
import com.teamresourceful.resourcefulbees.common.block.centrifuge.CentrifugeCrankBlock;
import com.teamresourceful.resourcefulbees.common.blocks.base.TickingBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModMaterials;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public final class ModBlocks {

    private ModBlocks() {
        throw new UtilityClassError();
    }

    public static final ResourcefulRegistry<Block> BLOCKS = ResourcefulRegistries.create(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.BLOCKS);
    public static final ResourcefulRegistry<Block> CENTRIFUGE_BLOCKS = ResourcefulRegistries.create(BLOCKS);

    public static final BlockBehaviour.Properties HONEY_FLUID_BLOCK_PROPERTIES = BlockBehaviour.Properties.of(ModMaterials.MATERIAL_HONEY).noCollission().strength(100.0F).noLootTable();
    public static final BlockBehaviour.Properties CENTRIFUGE_PROPERTIES = BlockBehaviour.Properties.of(Material.METAL).strength(2).sound(SoundType.METAL);

    //region Apiaries
    public static final RegistryEntry<Block> FLOW_HIVE = BLOCKS.register("flow_hive", FlowHiveBlock::new);
    //endregion

    public static final RegistryEntry<LiquidBlock> HONEY_FLUID_BLOCK = com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.HONEY_FLUID_BLOCKS.register("honey", () -> new LiquidBlock(ModFluids.HONEY_STILL, HONEY_FLUID_BLOCK_PROPERTIES));
    public static final RegistryEntry<Block> HONEY_GENERATOR = BLOCKS.register("honey_generator", () -> new HoneyGenerator(CENTRIFUGE_PROPERTIES));
    public static final RegistryEntry<Block> CREATIVE_GEN = BLOCKS.register("creative_gen", () -> new TickingBlock<>(ModBlockEntityTypes.CREATIVE_GEN_ENTITY, CENTRIFUGE_PROPERTIES));
    public static final RegistryEntry<Block> ENDER_BEECON = BLOCKS.register("ender_beecon", EnderBeecon::new);
    public static final RegistryEntry<Block> SOLIDIFICATION_CHAMBER = BLOCKS.register("solidification_chamber", () -> new SolidificationChamber(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.GLASS).strength(1.5f).requiresCorrectToolForDrops()));
    public static final RegistryEntry<Block> HONEY_POT = BLOCKS.register("honey_pot", () -> new HoneyPotBlock(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f).requiresCorrectToolForDrops()));

    public static final RegistryEntry<Block> BASIC_CENTRIFUGE = CENTRIFUGE_BLOCKS.register("centrifuge", () -> new CentrifugeBlock(BlockBehaviour.Properties.of(Material.METAL).strength(2).sound(SoundType.METAL).noOcclusion()));
    public static final RegistryEntry<Block> CENTRIFUGE_CRANK = CENTRIFUGE_BLOCKS.register("centrifuge_crank", () -> new CentrifugeCrankBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(2).sound(SoundType.WOOD).noOcclusion()));

    @SuppressWarnings("unused")
    private static boolean never(BlockState state, BlockGetter blockGetter, BlockPos blockPos) {
        return false;
    }
}
