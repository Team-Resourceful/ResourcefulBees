package com.teamresourceful.resourcefulbees.centrifuge.common.registries;

import com.teamresourceful.resourcefulbees.centrifuge.common.entities.*;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes.BLOCK_ENTITY_TYPES;

public final class CentrifugeBlockEntities {

    public static final ResourcefulRegistry<BlockEntityType<?>> CENTRIFUGE_BLOCK_ENTITY_TYPES = ResourcefulRegistries.create(BLOCK_ENTITY_TYPES);

    //region Centrifuge Block Entity Types
    public static final RegistryEntry<BlockEntityType<CentrifugeCasingEntity>> CENTRIFUGE_CASING_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge_casing", () -> build(BlockEntityType.Builder
            .of(CentrifugeCasingEntity::new, CentrifugeBlocks.CENTRIFUGE_CASING.get())));
    public static final RegistryEntry<BlockEntityType<CentrifugeProcessorEntity>> CENTRIFUGE_PROCESSOR_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge_processor", () -> build(BlockEntityType.Builder
            .of(CentrifugeProcessorEntity::new, CentrifugeBlocks.CENTRIFUGE_PROCESSOR.get())));
    public static final RegistryEntry<BlockEntityType<CentrifugeGearboxEntity>> CENTRIFUGE_GEARBOX_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge_gearbox", () -> build(BlockEntityType.Builder
            .of(CentrifugeGearboxEntity::new, CentrifugeBlocks.CENTRIFUGE_GEARBOX.get())));

    //TERMINAL
    public static final RegistryEntry<BlockEntityType<CentrifugeTerminalEntity>> CENTRIFUGE_BASIC_TERMINAL_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge/terminal/basic", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeTerminalEntity(CentrifugeBlockEntities.CENTRIFUGE_BASIC_TERMINAL_ENTITY, CentrifugeTier.BASIC, pos, state), CentrifugeBlocks.CENTRIFUGE_BASIC_TERMINAL.get())));
    public static final RegistryEntry<BlockEntityType<CentrifugeTerminalEntity>> CENTRIFUGE_ADVANCED_TERMINAL_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge/terminal/advanced", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeTerminalEntity(CentrifugeBlockEntities.CENTRIFUGE_ADVANCED_TERMINAL_ENTITY, CentrifugeTier.ADVANCED, pos, state), CentrifugeBlocks.CENTRIFUGE_ADVANCED_TERMINAL.get())));
    public static final RegistryEntry<BlockEntityType<CentrifugeTerminalEntity>> CENTRIFUGE_ELITE_TERMINAL_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge/terminal/elite", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeTerminalEntity(CentrifugeBlockEntities.CENTRIFUGE_ELITE_TERMINAL_ENTITY, CentrifugeTier.ELITE, pos, state), CentrifugeBlocks.CENTRIFUGE_ELITE_TERMINAL.get())));
    public static final RegistryEntry<BlockEntityType<CentrifugeTerminalEntity>> CENTRIFUGE_ULTIMATE_TERMINAL_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge/terminal/ultimate", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeTerminalEntity(CentrifugeBlockEntities.CENTRIFUGE_ULTIMATE_TERMINAL_ENTITY, CentrifugeTier.ULTIMATE, pos, state), CentrifugeBlocks.CENTRIFUGE_ULTIMATE_TERMINAL.get())));

    //VOID
    public static final RegistryEntry<BlockEntityType<CentrifugeVoidEntity>> CENTRIFUGE_BASIC_VOID_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge/void/basic", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeVoidEntity(CentrifugeBlockEntities.CENTRIFUGE_BASIC_VOID_ENTITY, CentrifugeTier.BASIC, pos, state), CentrifugeBlocks.CENTRIFUGE_BASIC_VOID.get())));
    public static final RegistryEntry<BlockEntityType<CentrifugeVoidEntity>> CENTRIFUGE_ADVANCED_VOID_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge/void/advanced", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeVoidEntity(CentrifugeBlockEntities.CENTRIFUGE_ADVANCED_VOID_ENTITY, CentrifugeTier.ADVANCED, pos, state), CentrifugeBlocks.CENTRIFUGE_ADVANCED_VOID.get())));
    public static final RegistryEntry<BlockEntityType<CentrifugeVoidEntity>> CENTRIFUGE_ELITE_VOID_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge/void/elite", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeVoidEntity(CentrifugeBlockEntities.CENTRIFUGE_ELITE_VOID_ENTITY, CentrifugeTier.ELITE, pos, state), CentrifugeBlocks.CENTRIFUGE_ELITE_VOID.get())));
    public static final RegistryEntry<BlockEntityType<CentrifugeVoidEntity>> CENTRIFUGE_ULTIMATE_VOID_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge/void/ultimate", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeVoidEntity(CentrifugeBlockEntities.CENTRIFUGE_ULTIMATE_VOID_ENTITY, CentrifugeTier.ULTIMATE, pos, state), CentrifugeBlocks.CENTRIFUGE_ULTIMATE_VOID.get())));

    //INPUT
    public static final RegistryEntry<BlockEntityType<CentrifugeInputEntity>> CENTRIFUGE_BASIC_INPUT_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge/input/item/basic", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeInputEntity(CentrifugeBlockEntities.CENTRIFUGE_BASIC_INPUT_ENTITY, CentrifugeTier.BASIC, pos, state), CentrifugeBlocks.CENTRIFUGE_BASIC_INPUT.get())));
    public static final RegistryEntry<BlockEntityType<CentrifugeInputEntity>> CENTRIFUGE_ADVANCED_INPUT_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge/input/item/advanced", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeInputEntity(CentrifugeBlockEntities.CENTRIFUGE_ADVANCED_INPUT_ENTITY, CentrifugeTier.ADVANCED, pos, state), CentrifugeBlocks.CENTRIFUGE_ADVANCED_INPUT.get())));
    public static final RegistryEntry<BlockEntityType<CentrifugeInputEntity>> CENTRIFUGE_ELITE_INPUT_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge/input/item/elite", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeInputEntity(CentrifugeBlockEntities.CENTRIFUGE_ELITE_INPUT_ENTITY, CentrifugeTier.ELITE, pos, state), CentrifugeBlocks.CENTRIFUGE_ELITE_INPUT.get())));
    public static final RegistryEntry<BlockEntityType<CentrifugeInputEntity>> CENTRIFUGE_ULTIMATE_INPUT_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge/input/item/ultimate", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeInputEntity(CentrifugeBlockEntities.CENTRIFUGE_ULTIMATE_INPUT_ENTITY, CentrifugeTier.ULTIMATE, pos, state), CentrifugeBlocks.CENTRIFUGE_ULTIMATE_INPUT.get())));

    //ENERGY PORT
    public static final RegistryEntry<BlockEntityType<CentrifugeEnergyPortEntity>> CENTRIFUGE_BASIC_ENERGY_PORT_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge/input/energy/basic", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeEnergyPortEntity(CentrifugeBlockEntities.CENTRIFUGE_BASIC_ENERGY_PORT_ENTITY, CentrifugeTier.BASIC, pos, state), CentrifugeBlocks.CENTRIFUGE_BASIC_ENERGY_PORT.get())));
    public static final RegistryEntry<BlockEntityType<CentrifugeEnergyPortEntity>> CENTRIFUGE_ADVANCED_ENERGY_PORT_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge/input/energy/advanced", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeEnergyPortEntity(CentrifugeBlockEntities.CENTRIFUGE_ADVANCED_ENERGY_PORT_ENTITY, CentrifugeTier.ADVANCED, pos, state), CentrifugeBlocks.CENTRIFUGE_ADVANCED_ENERGY_PORT.get())));
    public static final RegistryEntry<BlockEntityType<CentrifugeEnergyPortEntity>> CENTRIFUGE_ELITE_ENERGY_PORT_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge/input/energy/elite", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeEnergyPortEntity(CentrifugeBlockEntities.CENTRIFUGE_ELITE_ENERGY_PORT_ENTITY, CentrifugeTier.ELITE, pos, state), CentrifugeBlocks.CENTRIFUGE_ELITE_ENERGY_PORT.get())));
    public static final RegistryEntry<BlockEntityType<CentrifugeEnergyPortEntity>> CENTRIFUGE_ULTIMATE_ENERGY_PORT_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge/input/energy/ultimate", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeEnergyPortEntity(CentrifugeBlockEntities.CENTRIFUGE_ULTIMATE_ENERGY_PORT_ENTITY, CentrifugeTier.ULTIMATE, pos, state), CentrifugeBlocks.CENTRIFUGE_ULTIMATE_ENERGY_PORT.get())));

    //ITEM OUTPUT
    public static final RegistryEntry<BlockEntityType<CentrifugeItemOutputEntity>> CENTRIFUGE_BASIC_ITEM_OUTPUT_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge/output/item/basic", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeItemOutputEntity(CentrifugeBlockEntities.CENTRIFUGE_BASIC_ITEM_OUTPUT_ENTITY, CentrifugeTier.BASIC, pos, state), CentrifugeBlocks.CENTRIFUGE_BASIC_ITEM_OUTPUT.get())));
    public static final RegistryEntry<BlockEntityType<CentrifugeItemOutputEntity>> CENTRIFUGE_ADVANCED_ITEM_OUTPUT_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge/output/item/advanced", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeItemOutputEntity(CentrifugeBlockEntities.CENTRIFUGE_ADVANCED_ITEM_OUTPUT_ENTITY, CentrifugeTier.ADVANCED, pos, state), CentrifugeBlocks.CENTRIFUGE_ADVANCED_ITEM_OUTPUT.get())));
    public static final RegistryEntry<BlockEntityType<CentrifugeItemOutputEntity>> CENTRIFUGE_ELITE_ITEM_OUTPUT_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge/output/item/elite", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeItemOutputEntity(CentrifugeBlockEntities.CENTRIFUGE_ELITE_ITEM_OUTPUT_ENTITY, CentrifugeTier.ELITE, pos, state), CentrifugeBlocks.CENTRIFUGE_ELITE_ITEM_OUTPUT.get())));
    public static final RegistryEntry<BlockEntityType<CentrifugeItemOutputEntity>> CENTRIFUGE_ULTIMATE_ITEM_OUTPUT_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge/output/item/ultimate", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeItemOutputEntity(CentrifugeBlockEntities.CENTRIFUGE_ULTIMATE_ITEM_OUTPUT_ENTITY, CentrifugeTier.ULTIMATE, pos, state), CentrifugeBlocks.CENTRIFUGE_ULTIMATE_ITEM_OUTPUT.get())));

    //FLUID OUTPUT
    public static final RegistryEntry<BlockEntityType<CentrifugeFluidOutputEntity>> CENTRIFUGE_BASIC_FLUID_OUTPUT_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge/output/fluid/basic", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeFluidOutputEntity(CentrifugeBlockEntities.CENTRIFUGE_BASIC_FLUID_OUTPUT_ENTITY, CentrifugeTier.BASIC, pos, state), CentrifugeBlocks.CENTRIFUGE_BASIC_FLUID_OUTPUT.get())));
    public static final RegistryEntry<BlockEntityType<CentrifugeFluidOutputEntity>> CENTRIFUGE_ADVANCED_FLUID_OUTPUT_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge/output/fluid/advanced", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeFluidOutputEntity(CentrifugeBlockEntities.CENTRIFUGE_ADVANCED_FLUID_OUTPUT_ENTITY, CentrifugeTier.ADVANCED, pos, state), CentrifugeBlocks.CENTRIFUGE_ADVANCED_FLUID_OUTPUT.get())));
    public static final RegistryEntry<BlockEntityType<CentrifugeFluidOutputEntity>> CENTRIFUGE_ELITE_FLUID_OUTPUT_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge/output/fluid/elite", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeFluidOutputEntity(CentrifugeBlockEntities.CENTRIFUGE_ELITE_FLUID_OUTPUT_ENTITY, CentrifugeTier.ELITE, pos, state), CentrifugeBlocks.CENTRIFUGE_ELITE_FLUID_OUTPUT.get())));
    public static final RegistryEntry<BlockEntityType<CentrifugeFluidOutputEntity>> CENTRIFUGE_ULTIMATE_FLUID_OUTPUT_ENTITY = CENTRIFUGE_BLOCK_ENTITY_TYPES.register("centrifuge/output/fluid/ultimate", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeFluidOutputEntity(CentrifugeBlockEntities.CENTRIFUGE_ULTIMATE_FLUID_OUTPUT_ENTITY, CentrifugeTier.ULTIMATE, pos, state), CentrifugeBlocks.CENTRIFUGE_ULTIMATE_FLUID_OUTPUT.get())));
    //endregion

    private CentrifugeBlockEntities() {
        throw new UtilityClassError();
    }

    private static <T extends BlockEntity> BlockEntityType<T> build(BlockEntityType.Builder<T> builder) {
        //noinspection ConstantConditions
        return builder.build(null);
    }
}
