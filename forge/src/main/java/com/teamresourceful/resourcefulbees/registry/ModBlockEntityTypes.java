package com.teamresourceful.resourcefulbees.registry;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.lib.ModConstants;
import com.teamresourceful.resourcefulbees.tileentity.*;
import com.teamresourceful.resourcefulbees.tileentity.multiblocks.apiary.ApiaryBreederTileEntity;
import com.teamresourceful.resourcefulbees.tileentity.multiblocks.apiary.ApiaryStorageTileEntity;
import com.teamresourceful.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
import com.teamresourceful.resourcefulbees.tileentity.multiblocks.centrifuge.CentrifugeCasingTileEntity;
import com.teamresourceful.resourcefulbees.tileentity.multiblocks.centrifuge.CentrifugeControllerTileEntity;
import com.teamresourceful.resourcefulbees.tileentity.multiblocks.centrifuge.EliteCentrifugeCasingTileEntity;
import com.teamresourceful.resourcefulbees.tileentity.multiblocks.centrifuge.EliteCentrifugeControllerTileEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlockEntityTypes {


    private ModBlockEntityTypes() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ResourcefulBees.MOD_ID);


    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> TIERED_BEEHIVE_TILE_ENTITY = TILE_ENTITY_TYPES.register("tiered_beehive", () -> BlockEntityType.Builder
            .of(TieredBeehiveTileEntity::new, ModBlocks.ACACIA_BEE_NEST.get(), ModBlocks.GRASS_BEE_NEST.get(), ModBlocks.JUNGLE_BEE_NEST.get(), ModBlocks.NETHER_BEE_NEST.get(),
                    ModBlocks.PRISMARINE_BEE_NEST.get(), ModBlocks.PURPUR_BEE_NEST.get(), ModBlocks.WITHER_BEE_NEST.get(), ModBlocks.OAK_BEE_NEST.get(), ModBlocks.BIRCH_BEE_NEST.get(), ModBlocks.BROWN_MUSHROOM_BEE_NEST.get(), ModBlocks.CRIMSON_BEE_NEST.get(),
                    ModBlocks.CRIMSON_NYLIUM_BEE_NEST.get(), ModBlocks.DARK_OAK_BEE_NEST.get(), ModBlocks.RED_MUSHROOM_BEE_NEST.get(), ModBlocks.SPRUCE_BEE_NEST.get(), ModBlocks.WARPED_BEE_NEST.get(), ModBlocks.WARPED_NYLIUM_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<?>> CENTRIFUGE_ENTITY = TILE_ENTITY_TYPES.register("centrifuge", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeTileEntity(ModBlockEntityTypes.CENTRIFUGE_ENTITY.get()), ModBlocks.CENTRIFUGE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<?>> MECHANICAL_CENTRIFUGE_ENTITY = TILE_ENTITY_TYPES.register("mechanical_centrifuge", () -> BlockEntityType.Builder
            .of(MechanicalCentrifugeTileEntity::new, ModBlocks.MECHANICAL_CENTRIFUGE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<?>> CENTRIFUGE_CONTROLLER_ENTITY = TILE_ENTITY_TYPES.register("centrifuge_controller", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeControllerTileEntity(ModBlockEntityTypes.CENTRIFUGE_CONTROLLER_ENTITY.get()), ModBlocks.CENTRIFUGE_CONTROLLER.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<?>> CENTRIFUGE_CASING_ENTITY = TILE_ENTITY_TYPES.register("centrifuge_casing", () -> BlockEntityType.Builder
            .of(() -> new CentrifugeCasingTileEntity(ModBlockEntityTypes.CENTRIFUGE_CASING_ENTITY.get()), ModBlocks.CENTRIFUGE_CASING.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<HoneyGeneratorTileEntity>> HONEY_GENERATOR_ENTITY = TILE_ENTITY_TYPES.register("honey_generator", () -> BlockEntityType.Builder
            .of(HoneyGeneratorTileEntity::new, ModBlocks.HONEY_GENERATOR.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<?>> CREATIVE_GEN_ENTITY = TILE_ENTITY_TYPES.register("creative_gen", () -> BlockEntityType.Builder
            .of(CreativeGenTileEntity::new, ModBlocks.CREATIVE_GEN.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<?>> APIARY_TILE_ENTITY = TILE_ENTITY_TYPES.register("apiary", () -> BlockEntityType.Builder
            .of(ApiaryTileEntity::new, ModBlocks.T1_APIARY_BLOCK.get(), ModBlocks.T2_APIARY_BLOCK.get(), ModBlocks.T3_APIARY_BLOCK.get(), ModBlocks.T4_APIARY_BLOCK.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<?>> APIARY_STORAGE_TILE_ENTITY = TILE_ENTITY_TYPES.register("apiary_storage", () -> BlockEntityType.Builder
            .of(ApiaryStorageTileEntity::new, ModBlocks.APIARY_STORAGE_BLOCK.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<?>> APIARY_BREEDER_TILE_ENTITY = TILE_ENTITY_TYPES.register("apiary_breeder", () -> BlockEntityType.Builder
            .of(ApiaryBreederTileEntity::new, ModBlocks.APIARY_BREEDER_BLOCK.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<?>> ACCELERATOR_TILE_ENTITY = TILE_ENTITY_TYPES.register("accelerator", () -> BlockEntityType.Builder
            .of(AcceleratorTileEntity::new, ModBlocks.ACCELERATOR.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<?>> ELITE_CENTRIFUGE_CONTROLLER_ENTITY = TILE_ENTITY_TYPES.register("elite_centrifuge_controller", () -> BlockEntityType.Builder
            .of(() -> new EliteCentrifugeControllerTileEntity(ModBlockEntityTypes.ELITE_CENTRIFUGE_CONTROLLER_ENTITY.get()), ModBlocks.ELITE_CENTRIFUGE_CONTROLLER.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<?>> ELITE_CENTRIFUGE_CASING_ENTITY = TILE_ENTITY_TYPES.register("elite_centrifuge_casing", () -> BlockEntityType.Builder
            .of(() -> new EliteCentrifugeCasingTileEntity(ModBlockEntityTypes.ELITE_CENTRIFUGE_CASING_ENTITY.get()), ModBlocks.ELITE_CENTRIFUGE_CASING.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<EnderBeeconTileEntity>> ENDER_BEECON_TILE_ENTITY = TILE_ENTITY_TYPES.register("ender_beecon", () -> BlockEntityType.Builder
            .of(() -> new EnderBeeconTileEntity(ModBlockEntityTypes.ENDER_BEECON_TILE_ENTITY.get()), ModBlocks.ENDER_BEECON.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<HoneyTankTileEntity>> HONEY_TANK_TILE_ENTITY = TILE_ENTITY_TYPES.register("honey_tank", () -> BlockEntityType.Builder
            .of(() -> new HoneyTankTileEntity(HoneyTankTileEntity.TankTier.WOODEN), ModBlocks.PURPUR_HONEY_TANK.get(), ModBlocks.NETHER_HONEY_TANK.get(), ModBlocks.WOODEN_HONEY_TANK.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<HoneyCongealerTileEntity>> HONEY_CONGEALER_TILE_ENTITY = TILE_ENTITY_TYPES.register("honey_congealer", () -> BlockEntityType.Builder
            .of(HoneyCongealerTileEntity::new, ModBlocks.HONEY_CONGEALER.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<?>> HONEY_PIPE_TILE_ENTITY = TILE_ENTITY_TYPES.register("honey_pipe", () -> BlockEntityType.Builder
            .of(HoneyPipeTileEntity::new, ModBlocks.HONEY_PIPE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<?>> BOTTOMLESS_HONEY_POT_TILE_ENTITY = TILE_ENTITY_TYPES.register("bottomless_honey_pot", () -> BlockEntityType.Builder
            .of(BottomlessHoneyPotTileEntity::new, ModBlocks.BOTTOMLESS_HONEY_POT.get())
            .build(null));

}
