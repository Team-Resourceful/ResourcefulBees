package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.*;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.tileentity.*;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary.ApiaryBreederTileEntity;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary.ApiaryStorageTileEntity;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntityTypes {


    private ModBlockEntityTypes() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, ResourcefulBees.MOD_ID);

    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> TIERED_BEEHIVE_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("tiered_beehive", () -> BlockEntityType.Builder
            .of(TieredBeehiveTileEntity::new, ModBlocks.ACACIA_BEE_NEST.get(), ModBlocks.GRASS_BEE_NEST.get(), ModBlocks.JUNGLE_BEE_NEST.get(), ModBlocks.NETHER_BEE_NEST.get(),
                    ModBlocks.PRISMARINE_BEE_NEST.get(), ModBlocks.PURPUR_BEE_NEST.get(), ModBlocks.WITHER_BEE_NEST.get(), ModBlocks.OAK_BEE_NEST.get(), ModBlocks.BIRCH_BEE_NEST.get(), ModBlocks.BROWN_MUSHROOM_BEE_NEST.get(), ModBlocks.CRIMSON_BEE_NEST.get(),
                    ModBlocks.CRIMSON_NYLIUM_BEE_NEST.get(), ModBlocks.DARK_OAK_BEE_NEST.get(), ModBlocks.RED_MUSHROOM_BEE_NEST.get(), ModBlocks.SPRUCE_BEE_NEST.get(), ModBlocks.WARPED_BEE_NEST.get(), ModBlocks.WARPED_NYLIUM_BEE_NEST.get())
            .build(null));
    
    public static final RegistryObject<BlockEntityType<HoneyGeneratorTileEntity>> HONEY_GENERATOR_ENTITY = BLOCK_ENTITY_TYPES.register("honey_generator", () -> BlockEntityType.Builder
            .of(HoneyGeneratorTileEntity::new, ModBlocks.HONEY_GENERATOR.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CreativeGenTileEntity>> CREATIVE_GEN_ENTITY = BLOCK_ENTITY_TYPES.register("creative_gen", () -> BlockEntityType.Builder
            .of(CreativeGenTileEntity::new, ModBlocks.CREATIVE_GEN.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<ApiaryTileEntity>> APIARY_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("apiary", () -> BlockEntityType.Builder
            .of(ApiaryTileEntity::new, ModBlocks.T1_APIARY_BLOCK.get(), ModBlocks.T2_APIARY_BLOCK.get(), ModBlocks.T3_APIARY_BLOCK.get(), ModBlocks.T4_APIARY_BLOCK.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<ApiaryStorageTileEntity>> APIARY_STORAGE_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("apiary_storage", () -> BlockEntityType.Builder
            .of(ApiaryStorageTileEntity::new, ModBlocks.APIARY_STORAGE_BLOCK.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<ApiaryBreederTileEntity>> APIARY_BREEDER_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("apiary_breeder", () -> BlockEntityType.Builder
            .of(ApiaryBreederTileEntity::new, ModBlocks.APIARY_BREEDER_BLOCK.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<AcceleratorTileEntity>> ACCELERATOR_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("accelerator", () -> BlockEntityType.Builder
            .of(AcceleratorTileEntity::new, ModBlocks.ACCELERATOR.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<EnderBeeconTileEntity>> ENDER_BEECON_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("ender_beecon", () -> BlockEntityType.Builder
            .of(EnderBeeconTileEntity::new, ModBlocks.ENDER_BEECON.get())
            .build(null));
    //TODO Change id to solidification_chamber for 1.17/1.18
    public static final RegistryObject<BlockEntityType<SolidificationChamberTileEntity>> SOLIDIFICATION_CHAMBER_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("honey_congealer", () -> BlockEntityType.Builder
            .of(SolidificationChamberTileEntity::new, ModBlocks.SOLIDIFICATION_CHAMBER.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<HoneyPotTileEntity>> HONEY_POT_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("honey_pot", () -> BlockEntityType.Builder
            .of(HoneyPotTileEntity::new, ModBlocks.HONEY_POT.get())
            .build(null));

    //region Centrifuge Block Entity Types
    public static final RegistryObject<BlockEntityType<CentrifugeCasingEntity>> CENTRIFUGE_CASING_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge_casing", () -> BlockEntityType.Builder
            .of(CentrifugeCasingEntity::new, ModBlocks.CENTRIFUGE_CASING.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeProcessorEntity>> CENTRIFUGE_PROCESSOR_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge_processor", () -> BlockEntityType.Builder
            .of(CentrifugeProcessorEntity::new, ModBlocks.CENTRIFUGE_PROCESSOR.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeGearboxEntity>> CENTRIFUGE_GEARBOX_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge_gearbox", () -> BlockEntityType.Builder
            .of(CentrifugeGearboxEntity::new, ModBlocks.CENTRIFUGE_GEARBOX.get())
            .build(null));

    //TERMINAL
    public static final RegistryObject<BlockEntityType<CentrifugeTerminalEntity>> CENTRIFUGE_BASIC_TERMINAL_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/terminal/basic", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeTerminalEntity(ModBlockEntityTypes.CENTRIFUGE_BASIC_TERMINAL_ENTITY, CentrifugeTier.BASIC), ModBlocks.CENTRIFUGE_BASIC_TERMINAL.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeTerminalEntity>> CENTRIFUGE_ADVANCED_TERMINAL_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/terminal/advanced", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeTerminalEntity(ModBlockEntityTypes.CENTRIFUGE_ADVANCED_TERMINAL_ENTITY, CentrifugeTier.ADVANCED), ModBlocks.CENTRIFUGE_ADVANCED_TERMINAL.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeTerminalEntity>> CENTRIFUGE_ELITE_TERMINAL_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/terminal/elite", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeTerminalEntity(ModBlockEntityTypes.CENTRIFUGE_ELITE_TERMINAL_ENTITY, CentrifugeTier.ELITE), ModBlocks.CENTRIFUGE_ELITE_TERMINAL.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeTerminalEntity>> CENTRIFUGE_ULTIMATE_TERMINAL_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/terminal/ultimate", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeTerminalEntity(ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_TERMINAL_ENTITY, CentrifugeTier.ULTIMATE), ModBlocks.CENTRIFUGE_ULTIMATE_TERMINAL.get())
            .build(null));

    //VOID
    public static final RegistryObject<BlockEntityType<CentrifugeVoidEntity>> CENTRIFUGE_BASIC_VOID_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/void/basic", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeVoidEntity(ModBlockEntityTypes.CENTRIFUGE_BASIC_VOID_ENTITY, CentrifugeTier.BASIC), ModBlocks.CENTRIFUGE_BASIC_VOID.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeVoidEntity>> CENTRIFUGE_ADVANCED_VOID_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/void/advanced", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeVoidEntity(ModBlockEntityTypes.CENTRIFUGE_ADVANCED_VOID_ENTITY, CentrifugeTier.ADVANCED), ModBlocks.CENTRIFUGE_ADVANCED_VOID.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeVoidEntity>> CENTRIFUGE_ELITE_VOID_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/void/elite", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeVoidEntity(ModBlockEntityTypes.CENTRIFUGE_ELITE_VOID_ENTITY, CentrifugeTier.ELITE), ModBlocks.CENTRIFUGE_ELITE_VOID.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeVoidEntity>> CENTRIFUGE_ULTIMATE_VOID_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/void/ultimate", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeVoidEntity(ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_VOID_ENTITY, CentrifugeTier.ULTIMATE), ModBlocks.CENTRIFUGE_ULTIMATE_VOID.get())
            .build(null));

    //INPUT
    public static final RegistryObject<BlockEntityType<CentrifugeInputEntity>> CENTRIFUGE_BASIC_INPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/input/item/basic", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeInputEntity(ModBlockEntityTypes.CENTRIFUGE_BASIC_INPUT_ENTITY, CentrifugeTier.BASIC), ModBlocks.CENTRIFUGE_BASIC_INPUT.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeInputEntity>> CENTRIFUGE_ADVANCED_INPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/input/item/advanced", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeInputEntity(ModBlockEntityTypes.CENTRIFUGE_ADVANCED_INPUT_ENTITY, CentrifugeTier.ADVANCED), ModBlocks.CENTRIFUGE_ADVANCED_INPUT.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeInputEntity>> CENTRIFUGE_ELITE_INPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/input/item/elite", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeInputEntity(ModBlockEntityTypes.CENTRIFUGE_ELITE_INPUT_ENTITY, CentrifugeTier.ELITE), ModBlocks.CENTRIFUGE_ELITE_INPUT.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeInputEntity>> CENTRIFUGE_ULTIMATE_INPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/input/item/ultimate", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeInputEntity(ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_INPUT_ENTITY, CentrifugeTier.ULTIMATE), ModBlocks.CENTRIFUGE_ULTIMATE_INPUT.get())
            .build(null));

    //ENERGY PORT
    public static final RegistryObject<BlockEntityType<CentrifugeEnergyPortEntity>> CENTRIFUGE_BASIC_ENERGY_PORT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/input/energy/basic", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeEnergyPortEntity(ModBlockEntityTypes.CENTRIFUGE_BASIC_ENERGY_PORT_ENTITY, CentrifugeTier.BASIC), ModBlocks.CENTRIFUGE_BASIC_ENERGY_PORT.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeEnergyPortEntity>> CENTRIFUGE_ADVANCED_ENERGY_PORT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/input/energy/advanced", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeEnergyPortEntity(ModBlockEntityTypes.CENTRIFUGE_ADVANCED_ENERGY_PORT_ENTITY, CentrifugeTier.ADVANCED), ModBlocks.CENTRIFUGE_ADVANCED_ENERGY_PORT.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeEnergyPortEntity>> CENTRIFUGE_ELITE_ENERGY_PORT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/input/energy/elite", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeEnergyPortEntity(ModBlockEntityTypes.CENTRIFUGE_ELITE_ENERGY_PORT_ENTITY, CentrifugeTier.ELITE), ModBlocks.CENTRIFUGE_ELITE_ENERGY_PORT.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeEnergyPortEntity>> CENTRIFUGE_ULTIMATE_ENERGY_PORT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/input/energy/ultimate", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeEnergyPortEntity(ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_ENERGY_PORT_ENTITY, CentrifugeTier.ULTIMATE), ModBlocks.CENTRIFUGE_ULTIMATE_ENERGY_PORT.get())
            .build(null));

    //ITEM OUTPUT
    public static final RegistryObject<BlockEntityType<CentrifugeItemOutputEntity>> CENTRIFUGE_BASIC_ITEM_OUTPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/output/item/basic", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeItemOutputEntity(ModBlockEntityTypes.CENTRIFUGE_BASIC_ITEM_OUTPUT_ENTITY, CentrifugeTier.BASIC), ModBlocks.CENTRIFUGE_BASIC_ITEM_OUTPUT.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeItemOutputEntity>> CENTRIFUGE_ADVANCED_ITEM_OUTPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/output/item/advanced", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeItemOutputEntity(ModBlockEntityTypes.CENTRIFUGE_ADVANCED_ITEM_OUTPUT_ENTITY, CentrifugeTier.ADVANCED), ModBlocks.CENTRIFUGE_ADVANCED_ITEM_OUTPUT.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeItemOutputEntity>> CENTRIFUGE_ELITE_ITEM_OUTPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/output/item/elite", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeItemOutputEntity(ModBlockEntityTypes.CENTRIFUGE_ELITE_ITEM_OUTPUT_ENTITY, CentrifugeTier.ELITE), ModBlocks.CENTRIFUGE_ELITE_ITEM_OUTPUT.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeItemOutputEntity>> CENTRIFUGE_ULTIMATE_ITEM_OUTPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/output/item/ultimate", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeItemOutputEntity(ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_ITEM_OUTPUT_ENTITY, CentrifugeTier.ULTIMATE), ModBlocks.CENTRIFUGE_ULTIMATE_ITEM_OUTPUT.get())
            .build(null));

    //FLUID OUTPUT
    public static final RegistryObject<BlockEntityType<CentrifugeFluidOutputEntity>> CENTRIFUGE_BASIC_FLUID_OUTPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/output/fluid/basic", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeFluidOutputEntity(ModBlockEntityTypes.CENTRIFUGE_BASIC_FLUID_OUTPUT_ENTITY, CentrifugeTier.BASIC), ModBlocks.CENTRIFUGE_BASIC_FLUID_OUTPUT.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeFluidOutputEntity>> CENTRIFUGE_ADVANCED_FLUID_OUTPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/output/fluid/advanced", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeFluidOutputEntity(ModBlockEntityTypes.CENTRIFUGE_ADVANCED_FLUID_OUTPUT_ENTITY, CentrifugeTier.ADVANCED), ModBlocks.CENTRIFUGE_ADVANCED_FLUID_OUTPUT.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeFluidOutputEntity>> CENTRIFUGE_ELITE_FLUID_OUTPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/output/fluid/elite", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeFluidOutputEntity(ModBlockEntityTypes.CENTRIFUGE_ELITE_FLUID_OUTPUT_ENTITY, CentrifugeTier.ELITE), ModBlocks.CENTRIFUGE_ELITE_FLUID_OUTPUT.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeFluidOutputEntity>> CENTRIFUGE_ULTIMATE_FLUID_OUTPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/output/fluid/ultimate", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeFluidOutputEntity(ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_FLUID_OUTPUT_ENTITY, CentrifugeTier.ULTIMATE), ModBlocks.CENTRIFUGE_ULTIMATE_FLUID_OUTPUT.get())
            .build(null));
    //endregion
}
