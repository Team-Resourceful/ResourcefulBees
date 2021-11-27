package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.*;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.tileentity.*;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary.ApiaryBreederTileEntity;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary.ApiaryStorageTileEntity;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlockEntityTypes {


    private ModBlockEntityTypes() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<TileEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ResourcefulBees.MOD_ID);

    public static final RegistryObject<TileEntityType<TieredBeehiveTileEntity>> TIERED_BEEHIVE_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("tiered_beehive", () -> TileEntityType.Builder
            .of(TieredBeehiveTileEntity::new, ModBlocks.ACACIA_BEE_NEST.get(), ModBlocks.GRASS_BEE_NEST.get(), ModBlocks.JUNGLE_BEE_NEST.get(), ModBlocks.NETHER_BEE_NEST.get(),
                    ModBlocks.PRISMARINE_BEE_NEST.get(), ModBlocks.PURPUR_BEE_NEST.get(), ModBlocks.WITHER_BEE_NEST.get(), ModBlocks.OAK_BEE_NEST.get(), ModBlocks.BIRCH_BEE_NEST.get(), ModBlocks.BROWN_MUSHROOM_BEE_NEST.get(), ModBlocks.CRIMSON_BEE_NEST.get(),
                    ModBlocks.CRIMSON_NYLIUM_BEE_NEST.get(), ModBlocks.DARK_OAK_BEE_NEST.get(), ModBlocks.RED_MUSHROOM_BEE_NEST.get(), ModBlocks.SPRUCE_BEE_NEST.get(), ModBlocks.WARPED_BEE_NEST.get(), ModBlocks.WARPED_NYLIUM_BEE_NEST.get())
            .build(null));
    
    public static final RegistryObject<TileEntityType<HoneyGeneratorTileEntity>> HONEY_GENERATOR_ENTITY = BLOCK_ENTITY_TYPES.register("honey_generator", () -> TileEntityType.Builder
            .of(HoneyGeneratorTileEntity::new, ModBlocks.HONEY_GENERATOR.get())
            .build(null));
    public static final RegistryObject<TileEntityType<CreativeGenTileEntity>> CREATIVE_GEN_ENTITY = BLOCK_ENTITY_TYPES.register("creative_gen", () -> TileEntityType.Builder
            .of(CreativeGenTileEntity::new, ModBlocks.CREATIVE_GEN.get())
            .build(null));
    public static final RegistryObject<TileEntityType<ApiaryTileEntity>> APIARY_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("apiary", () -> TileEntityType.Builder
            .of(ApiaryTileEntity::new, ModBlocks.T1_APIARY_BLOCK.get(), ModBlocks.T2_APIARY_BLOCK.get(), ModBlocks.T3_APIARY_BLOCK.get(), ModBlocks.T4_APIARY_BLOCK.get())
            .build(null));
    public static final RegistryObject<TileEntityType<ApiaryStorageTileEntity>> APIARY_STORAGE_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("apiary_storage", () -> TileEntityType.Builder
            .of(ApiaryStorageTileEntity::new, ModBlocks.APIARY_STORAGE_BLOCK.get())
            .build(null));
    public static final RegistryObject<TileEntityType<ApiaryBreederTileEntity>> APIARY_BREEDER_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("apiary_breeder", () -> TileEntityType.Builder
            .of(ApiaryBreederTileEntity::new, ModBlocks.APIARY_BREEDER_BLOCK.get())
            .build(null));
    public static final RegistryObject<TileEntityType<AcceleratorTileEntity>> ACCELERATOR_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("accelerator", () -> TileEntityType.Builder
            .of(AcceleratorTileEntity::new, ModBlocks.ACCELERATOR.get())
            .build(null));
    public static final RegistryObject<TileEntityType<EnderBeeconTileEntity>> ENDER_BEECON_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("ender_beecon", () -> TileEntityType.Builder
            .of(EnderBeeconTileEntity::new, ModBlocks.ENDER_BEECON.get())
            .build(null));
    //TODO Change id to solidification_chamber for 1.17/1.18
    public static final RegistryObject<TileEntityType<SolidificationChamberTileEntity>> SOLIDIFICATION_CHAMBER_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("honey_congealer", () -> TileEntityType.Builder
            .of(SolidificationChamberTileEntity::new, ModBlocks.SOLIDIFICATION_CHAMBER.get())
            .build(null));
    public static final RegistryObject<TileEntityType<HoneyPotTileEntity>> HONEY_POT_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("honey_pot", () -> TileEntityType.Builder
            .of(HoneyPotTileEntity::new, ModBlocks.HONEY_POT.get())
            .build(null));

    //region Centrifuge Block Entity Types
    public static final RegistryObject<TileEntityType<CentrifugeCasingEntity>> CENTRIFUGE_CASING_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge_casing", () -> TileEntityType.Builder
            .of(CentrifugeCasingEntity::new, ModBlocks.CENTRIFUGE_CASING.get())
            .build(null));
    public static final RegistryObject<TileEntityType<CentrifugeProcessorEntity>> CENTRIFUGE_PROCESSOR_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge_processor", () -> TileEntityType.Builder
            .of(CentrifugeProcessorEntity::new, ModBlocks.CENTRIFUGE_PROCESSOR.get())
            .build(null));
    public static final RegistryObject<TileEntityType<CentrifugeGearboxEntity>> CENTRIFUGE_GEARBOX_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge_gearbox", () -> TileEntityType.Builder
            .of(CentrifugeGearboxEntity::new, ModBlocks.CENTRIFUGE_GEARBOX.get())
            .build(null));

    //TERMINAL
    public static final RegistryObject<TileEntityType<CentrifugeTerminalEntity>> CENTRIFUGE_BASIC_TERMINAL_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/terminal/basic", () -> TileEntityType.Builder
            .of(() -> new CentrifugeTerminalEntity(ModBlockEntityTypes.CENTRIFUGE_BASIC_TERMINAL_ENTITY, CentrifugeTier.BASIC), ModBlocks.CENTRIFUGE_BASIC_TERMINAL.get())
            .build(null));
    public static final RegistryObject<TileEntityType<CentrifugeTerminalEntity>> CENTRIFUGE_ADVANCED_TERMINAL_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/terminal/advanced", () -> TileEntityType.Builder
            .of(() -> new CentrifugeTerminalEntity(ModBlockEntityTypes.CENTRIFUGE_ADVANCED_TERMINAL_ENTITY, CentrifugeTier.ADVANCED), ModBlocks.CENTRIFUGE_ADVANCED_TERMINAL.get())
            .build(null));
    public static final RegistryObject<TileEntityType<CentrifugeTerminalEntity>> CENTRIFUGE_ELITE_TERMINAL_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/terminal/elite", () -> TileEntityType.Builder
            .of(() -> new CentrifugeTerminalEntity(ModBlockEntityTypes.CENTRIFUGE_ELITE_TERMINAL_ENTITY, CentrifugeTier.ELITE), ModBlocks.CENTRIFUGE_ELITE_TERMINAL.get())
            .build(null));
    public static final RegistryObject<TileEntityType<CentrifugeTerminalEntity>> CENTRIFUGE_ULTIMATE_TERMINAL_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/terminal/ultimate", () -> TileEntityType.Builder
            .of(() -> new CentrifugeTerminalEntity(ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_TERMINAL_ENTITY, CentrifugeTier.ULTIMATE), ModBlocks.CENTRIFUGE_ULTIMATE_TERMINAL.get())
            .build(null));

    //VOID
    public static final RegistryObject<TileEntityType<CentrifugeVoidEntity>> CENTRIFUGE_BASIC_VOID_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/void/basic", () -> TileEntityType.Builder
            .of(() -> new CentrifugeVoidEntity(ModBlockEntityTypes.CENTRIFUGE_BASIC_VOID_ENTITY, CentrifugeTier.BASIC), ModBlocks.CENTRIFUGE_BASIC_VOID.get())
            .build(null));
    public static final RegistryObject<TileEntityType<CentrifugeVoidEntity>> CENTRIFUGE_ADVANCED_VOID_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/void/advanced", () -> TileEntityType.Builder
            .of(() -> new CentrifugeVoidEntity(ModBlockEntityTypes.CENTRIFUGE_ADVANCED_VOID_ENTITY, CentrifugeTier.ADVANCED), ModBlocks.CENTRIFUGE_ADVANCED_VOID.get())
            .build(null));
    public static final RegistryObject<TileEntityType<CentrifugeVoidEntity>> CENTRIFUGE_ELITE_VOID_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/void/elite", () -> TileEntityType.Builder
            .of(() -> new CentrifugeVoidEntity(ModBlockEntityTypes.CENTRIFUGE_ELITE_VOID_ENTITY, CentrifugeTier.ELITE), ModBlocks.CENTRIFUGE_ELITE_VOID.get())
            .build(null));
    public static final RegistryObject<TileEntityType<CentrifugeVoidEntity>> CENTRIFUGE_ULTIMATE_VOID_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/void/ultimate", () -> TileEntityType.Builder
            .of(() -> new CentrifugeVoidEntity(ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_VOID_ENTITY, CentrifugeTier.ULTIMATE), ModBlocks.CENTRIFUGE_ULTIMATE_VOID.get())
            .build(null));

    //INPUT
    public static final RegistryObject<TileEntityType<CentrifugeInputEntity>> CENTRIFUGE_BASIC_INPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/input/item/basic", () -> TileEntityType.Builder
            .of(() -> new CentrifugeInputEntity(ModBlockEntityTypes.CENTRIFUGE_BASIC_INPUT_ENTITY, CentrifugeTier.BASIC), ModBlocks.CENTRIFUGE_BASIC_INPUT.get())
            .build(null));
    public static final RegistryObject<TileEntityType<CentrifugeInputEntity>> CENTRIFUGE_ADVANCED_INPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/input/item/advanced", () -> TileEntityType.Builder
            .of(() -> new CentrifugeInputEntity(ModBlockEntityTypes.CENTRIFUGE_ADVANCED_INPUT_ENTITY, CentrifugeTier.ADVANCED), ModBlocks.CENTRIFUGE_ADVANCED_INPUT.get())
            .build(null));
    public static final RegistryObject<TileEntityType<CentrifugeInputEntity>> CENTRIFUGE_ELITE_INPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/input/item/elite", () -> TileEntityType.Builder
            .of(() -> new CentrifugeInputEntity(ModBlockEntityTypes.CENTRIFUGE_ELITE_INPUT_ENTITY, CentrifugeTier.ELITE), ModBlocks.CENTRIFUGE_ELITE_INPUT.get())
            .build(null));
    public static final RegistryObject<TileEntityType<CentrifugeInputEntity>> CENTRIFUGE_ULTIMATE_INPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/input/item/ultimate", () -> TileEntityType.Builder
            .of(() -> new CentrifugeInputEntity(ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_INPUT_ENTITY, CentrifugeTier.ULTIMATE), ModBlocks.CENTRIFUGE_ULTIMATE_INPUT.get())
            .build(null));

    //ENERGY PORT
    public static final RegistryObject<TileEntityType<CentrifugeEnergyPortEntity>> CENTRIFUGE_BASIC_ENERGY_PORT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/input/energy/basic", () -> TileEntityType.Builder
            .of(() -> new CentrifugeEnergyPortEntity(ModBlockEntityTypes.CENTRIFUGE_BASIC_ENERGY_PORT_ENTITY, CentrifugeTier.BASIC), ModBlocks.CENTRIFUGE_BASIC_ENERGY_PORT.get())
            .build(null));
    public static final RegistryObject<TileEntityType<CentrifugeEnergyPortEntity>> CENTRIFUGE_ADVANCED_ENERGY_PORT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/input/energy/advanced", () -> TileEntityType.Builder
            .of(() -> new CentrifugeEnergyPortEntity(ModBlockEntityTypes.CENTRIFUGE_ADVANCED_ENERGY_PORT_ENTITY, CentrifugeTier.ADVANCED), ModBlocks.CENTRIFUGE_ADVANCED_ENERGY_PORT.get())
            .build(null));
    public static final RegistryObject<TileEntityType<CentrifugeEnergyPortEntity>> CENTRIFUGE_ELITE_ENERGY_PORT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/input/energy/elite", () -> TileEntityType.Builder
            .of(() -> new CentrifugeEnergyPortEntity(ModBlockEntityTypes.CENTRIFUGE_ELITE_ENERGY_PORT_ENTITY, CentrifugeTier.ELITE), ModBlocks.CENTRIFUGE_ELITE_ENERGY_PORT.get())
            .build(null));
    public static final RegistryObject<TileEntityType<CentrifugeEnergyPortEntity>> CENTRIFUGE_ULTIMATE_ENERGY_PORT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/input/energy/ultimate", () -> TileEntityType.Builder
            .of(() -> new CentrifugeEnergyPortEntity(ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_ENERGY_PORT_ENTITY, CentrifugeTier.ULTIMATE), ModBlocks.CENTRIFUGE_ULTIMATE_ENERGY_PORT.get())
            .build(null));

    //ITEM OUTPUT
    public static final RegistryObject<TileEntityType<CentrifugeItemOutputEntity>> CENTRIFUGE_BASIC_ITEM_OUTPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/output/item/basic", () -> TileEntityType.Builder
            .of(() -> new CentrifugeItemOutputEntity(ModBlockEntityTypes.CENTRIFUGE_BASIC_ITEM_OUTPUT_ENTITY, CentrifugeTier.BASIC), ModBlocks.CENTRIFUGE_BASIC_ITEM_OUTPUT.get())
            .build(null));
    public static final RegistryObject<TileEntityType<CentrifugeItemOutputEntity>> CENTRIFUGE_ADVANCED_ITEM_OUTPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/output/item/advanced", () -> TileEntityType.Builder
            .of(() -> new CentrifugeItemOutputEntity(ModBlockEntityTypes.CENTRIFUGE_ADVANCED_ITEM_OUTPUT_ENTITY, CentrifugeTier.ADVANCED), ModBlocks.CENTRIFUGE_ADVANCED_ITEM_OUTPUT.get())
            .build(null));
    public static final RegistryObject<TileEntityType<CentrifugeItemOutputEntity>> CENTRIFUGE_ELITE_ITEM_OUTPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/output/item/elite", () -> TileEntityType.Builder
            .of(() -> new CentrifugeItemOutputEntity(ModBlockEntityTypes.CENTRIFUGE_ELITE_ITEM_OUTPUT_ENTITY, CentrifugeTier.ELITE), ModBlocks.CENTRIFUGE_ELITE_ITEM_OUTPUT.get())
            .build(null));
    public static final RegistryObject<TileEntityType<CentrifugeItemOutputEntity>> CENTRIFUGE_ULTIMATE_ITEM_OUTPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/output/item/ultimate", () -> TileEntityType.Builder
            .of(() -> new CentrifugeItemOutputEntity(ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_ITEM_OUTPUT_ENTITY, CentrifugeTier.ULTIMATE), ModBlocks.CENTRIFUGE_ULTIMATE_ITEM_OUTPUT.get())
            .build(null));

    //FLUID OUTPUT
    public static final RegistryObject<TileEntityType<CentrifugeFluidOutputEntity>> CENTRIFUGE_BASIC_FLUID_OUTPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/output/fluid/basic", () -> TileEntityType.Builder
            .of(() -> new CentrifugeFluidOutputEntity(ModBlockEntityTypes.CENTRIFUGE_BASIC_FLUID_OUTPUT_ENTITY, CentrifugeTier.BASIC), ModBlocks.CENTRIFUGE_BASIC_FLUID_OUTPUT.get())
            .build(null));
    public static final RegistryObject<TileEntityType<CentrifugeFluidOutputEntity>> CENTRIFUGE_ADVANCED_FLUID_OUTPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/output/fluid/advanced", () -> TileEntityType.Builder
            .of(() -> new CentrifugeFluidOutputEntity(ModBlockEntityTypes.CENTRIFUGE_ADVANCED_FLUID_OUTPUT_ENTITY, CentrifugeTier.ADVANCED), ModBlocks.CENTRIFUGE_ADVANCED_FLUID_OUTPUT.get())
            .build(null));
    public static final RegistryObject<TileEntityType<CentrifugeFluidOutputEntity>> CENTRIFUGE_ELITE_FLUID_OUTPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/output/fluid/elite", () -> TileEntityType.Builder
            .of(() -> new CentrifugeFluidOutputEntity(ModBlockEntityTypes.CENTRIFUGE_ELITE_FLUID_OUTPUT_ENTITY, CentrifugeTier.ELITE), ModBlocks.CENTRIFUGE_ELITE_FLUID_OUTPUT.get())
            .build(null));
    public static final RegistryObject<TileEntityType<CentrifugeFluidOutputEntity>> CENTRIFUGE_ULTIMATE_FLUID_OUTPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/output/fluid/ultimate", () -> TileEntityType.Builder
            .of(() -> new CentrifugeFluidOutputEntity(ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_FLUID_OUTPUT_ENTITY, CentrifugeTier.ULTIMATE), ModBlocks.CENTRIFUGE_ULTIMATE_FLUID_OUTPUT.get())
            .build(null));
    //endregion
}
