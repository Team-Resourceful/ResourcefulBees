package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.common.blockentity.*;
import com.teamresourceful.resourcefulbees.common.blockentity.centrifuge.CentrifugeBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentity.centrifuge.CentrifugeCrankBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntityTypes {

    public static final ResourcefulRegistry<BlockEntityType<?>> BLOCK_ENTITY_TYPES = ResourcefulRegistries.create(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes.BLOCK_ENTITY_TYPES);

    private ModBlockEntityTypes() {
        throw new UtilityClassError();
    }

    public static final RegistryEntry<BlockEntityType<? extends FlowHiveBlockEntity>> FLOW_HIVE_ENTITY = BLOCK_ENTITY_TYPES.register("flow_hive", () -> build(BlockEntityType.Builder
            .of(FlowHiveBlockEntity::new, ModBlocks.FLOW_HIVE.get())));

    public static final RegistryEntry<BlockEntityType<HoneyGeneratorBlockEntity>> HONEY_GENERATOR_ENTITY = BLOCK_ENTITY_TYPES.register("honey_generator", () -> build(BlockEntityType.Builder
            .of(HoneyGeneratorBlockEntity::new, ModBlocks.HONEY_GENERATOR.get())));
    public static final RegistryEntry<BlockEntityType<CreativeGenBlockEntity>> CREATIVE_GEN_ENTITY = BLOCK_ENTITY_TYPES.register("creative_gen", () -> build(BlockEntityType.Builder
            .of(CreativeGenBlockEntity::new, ModBlocks.CREATIVE_GEN.get())));
    public static final RegistryEntry<BlockEntityType<EnderBeeconBlockEntity>> ENDER_BEECON_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("ender_beecon", () -> build(BlockEntityType.Builder
            .of(EnderBeeconBlockEntity::new, ModBlocks.ENDER_BEECON.get())));
    public static final RegistryEntry<BlockEntityType<SolidificationChamberBlockEntity>> SOLIDIFICATION_CHAMBER_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("solidification_chamber", () -> build(BlockEntityType.Builder
            .of(SolidificationChamberBlockEntity::new, ModBlocks.SOLIDIFICATION_CHAMBER.get())));
    public static final RegistryEntry<BlockEntityType<HoneyPotBlockEntity>> HONEY_POT_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("honey_pot", () -> build(BlockEntityType.Builder
            .of(HoneyPotBlockEntity::new, ModBlocks.HONEY_POT.get())));

    public static final RegistryEntry<BlockEntityType<CentrifugeBlockEntity>> BASIC_CENTRIFUGE_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeBlockEntity(ModBlockEntityTypes.BASIC_CENTRIFUGE_ENTITY.get(), pos, state), ModBlocks.BASIC_CENTRIFUGE.get())));
    public static final RegistryEntry<BlockEntityType<CentrifugeCrankBlockEntity>> CENTRIFUGE_CRANK_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge_crank", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeCrankBlockEntity(ModBlockEntityTypes.CENTRIFUGE_CRANK_ENTITY.get(), pos, state), ModBlocks.CENTRIFUGE_CRANK.get())));

    private static <T extends BlockEntity> BlockEntityType<T> build(BlockEntityType.Builder<T> builder) {
        //noinspection ConstantConditions
        return builder.build(null);
    }
}
