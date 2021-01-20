package com.resourcefulbees.resourcefulbees.registry;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.tileentity.*;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryBreederTileEntity;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryStorageTileEntity;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.centrifuge.CentrifugeCasingTileEntity;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.centrifuge.CentrifugeControllerTileEntity;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.centrifuge.EliteCentrifugeCasingTileEntity;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.centrifuge.EliteCentrifugeControllerTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntityTypes {

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ResourcefulBees.MOD_ID);


    public static final RegistryObject<TileEntityType<?>> TIERED_BEEHIVE_TILE_ENTITY = TILE_ENTITY_TYPES.register("tiered_beehive", () -> TileEntityType.Builder
            .create(TieredBeehiveTileEntity::new, ModBlocks.T1_BEEHIVE.get(), ModBlocks.T2_BEEHIVE.get(), ModBlocks.T3_BEEHIVE.get(), ModBlocks.T4_BEEHIVE.get(), ModBlocks.ACACIA_BEE_NEST.get(), ModBlocks.GRASS_BEE_NEST.get(), ModBlocks.JUNGLE_BEE_NEST.get(), ModBlocks.NETHER_BEE_NEST.get(),
                    ModBlocks.PRISMARINE_BEE_NEST.get(), ModBlocks.PURPUR_BEE_NEST.get(), ModBlocks.WITHER_BEE_NEST.get(), ModBlocks.OAK_BEE_NEST.get(), ModBlocks.BIRCH_BEE_NEST.get(), ModBlocks.BROWN_MUSHROOM_BEE_NEST.get(), ModBlocks.CRIMSON_BEE_NEST.get(),
                    ModBlocks.CRIMSON_NYLIUM_BEE_NEST.get(), ModBlocks.DARK_OAK_BEE_NEST.get(), ModBlocks.RED_MUSHROOM_BEE_NEST.get(), ModBlocks.SPRUCE_BEE_NEST.get(), ModBlocks.WARPED_BEE_NEST.get(), ModBlocks.WARPED_NYLIUM_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<TileEntityType<?>> CENTRIFUGE_ENTITY = TILE_ENTITY_TYPES.register("centrifuge", () -> TileEntityType.Builder
            .create(() -> new CentrifugeTileEntity(ModTileEntityTypes.CENTRIFUGE_ENTITY.get()), ModBlocks.CENTRIFUGE.get())
            .build(null));
    public static final RegistryObject<TileEntityType<?>> MECHANICAL_CENTRIFUGE_ENTITY = TILE_ENTITY_TYPES.register("mechanical_centrifuge", () -> TileEntityType.Builder
            .create(MechanicalCentrifugeTileEntity::new, ModBlocks.MECHANICAL_CENTRIFUGE.get())
            .build(null));
    public static final RegistryObject<TileEntityType<?>> CENTRIFUGE_CONTROLLER_ENTITY = TILE_ENTITY_TYPES.register("centrifuge_controller", () -> TileEntityType.Builder
            .create(() -> new CentrifugeControllerTileEntity(ModTileEntityTypes.CENTRIFUGE_CONTROLLER_ENTITY.get()), ModBlocks.CENTRIFUGE_CONTROLLER.get())
            .build(null));
    public static final RegistryObject<TileEntityType<?>> CENTRIFUGE_CASING_ENTITY = TILE_ENTITY_TYPES.register("centrifuge_casing", () -> TileEntityType.Builder
            .create(() -> new CentrifugeCasingTileEntity(ModTileEntityTypes.CENTRIFUGE_CASING_ENTITY.get()), ModBlocks.CENTRIFUGE_CASING.get())
            .build(null));
    public static final RegistryObject<TileEntityType<HoneyGeneratorTileEntity>> HONEY_GENERATOR_ENTITY = TILE_ENTITY_TYPES.register("honey_generator", () -> TileEntityType.Builder
            .create(HoneyGeneratorTileEntity::new, ModBlocks.HONEY_GENERATOR.get())
            .build(null));
    public static final RegistryObject<TileEntityType<?>> CREATIVE_GEN_ENTITY = TILE_ENTITY_TYPES.register("creative_gen", () -> TileEntityType.Builder
            .create(CreativeGenTileEntity::new, ModBlocks.CREATIVE_GEN.get())
            .build(null));
    public static final RegistryObject<TileEntityType<?>> APIARY_TILE_ENTITY = TILE_ENTITY_TYPES.register("apiary", () -> TileEntityType.Builder
            .create(ApiaryTileEntity::new, ModBlocks.T1_APIARY_BLOCK.get(), ModBlocks.T2_APIARY_BLOCK.get(), ModBlocks.T3_APIARY_BLOCK.get(), ModBlocks.T4_APIARY_BLOCK.get())
            .build(null));
    public static final RegistryObject<TileEntityType<?>> APIARY_STORAGE_TILE_ENTITY = TILE_ENTITY_TYPES.register("apiary_storage", () -> TileEntityType.Builder
            .create(ApiaryStorageTileEntity::new, ModBlocks.APIARY_STORAGE_BLOCK.get())
            .build(null));
    public static final RegistryObject<TileEntityType<?>> APIARY_BREEDER_TILE_ENTITY = TILE_ENTITY_TYPES.register("apiary_breeder", () -> TileEntityType.Builder
            .create(ApiaryBreederTileEntity::new, ModBlocks.APIARY_BREEDER_BLOCK.get())
            .build(null));
    public static final RegistryObject<TileEntityType<?>> ACCELERATOR_TILE_ENTITY = TILE_ENTITY_TYPES.register("accelerator", () -> TileEntityType.Builder
            .create(AcceleratorTileEntity::new, ModBlocks.ACCELERATOR.get())
            .build(null));
    public static final RegistryObject<TileEntityType<?>> ELITE_CENTRIFUGE_CONTROLLER_ENTITY = TILE_ENTITY_TYPES.register("elite_centrifuge_controller", () -> TileEntityType.Builder
            .create(() -> new EliteCentrifugeControllerTileEntity(ModTileEntityTypes.ELITE_CENTRIFUGE_CONTROLLER_ENTITY.get()), ModBlocks.ELITE_CENTRIFUGE_CONTROLLER.get())
            .build(null));
    public static final RegistryObject<TileEntityType<?>> ELITE_CENTRIFUGE_CASING_ENTITY = TILE_ENTITY_TYPES.register("elite_centrifuge_casing", () -> TileEntityType.Builder
            .create(() -> new EliteCentrifugeCasingTileEntity(ModTileEntityTypes.ELITE_CENTRIFUGE_CASING_ENTITY.get()), ModBlocks.ELITE_CENTRIFUGE_CASING.get())
            .build(null));
    public static final RegistryObject<TileEntityType<EnderBeeconTileEntity>> ENDER_BEECON_TILE_ENTITY = TILE_ENTITY_TYPES.register("ender_beecon", () -> TileEntityType.Builder
            .create(() -> new EnderBeeconTileEntity(ModTileEntityTypes.ENDER_BEECON_TILE_ENTITY.get()), ModBlocks.ENDER_BEECON.get())
            .build(null));
    public static final RegistryObject<TileEntityType<HoneyTankTileEntity>> HONEY_TANK_TILE_ENTITY = TILE_ENTITY_TYPES.register("honey_tank", () -> TileEntityType.Builder
            .create(() -> new HoneyTankTileEntity(HoneyTankTileEntity.TankTier.WOODEN), ModBlocks.PURPUR_HONEY_TANK.get(), ModBlocks.NETHER_HONEY_TANK.get(), ModBlocks.WOODEN_HONEY_TANK.get())
            .build(null));
}
