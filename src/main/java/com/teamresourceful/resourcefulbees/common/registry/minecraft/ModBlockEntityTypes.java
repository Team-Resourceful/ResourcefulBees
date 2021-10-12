package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
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

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ResourcefulBees.MOD_ID);


    public static final RegistryObject<TileEntityType<TieredBeehiveTileEntity>> TIERED_BEEHIVE_TILE_ENTITY = TILE_ENTITY_TYPES.register("tiered_beehive", () -> TileEntityType.Builder
            .of(TieredBeehiveTileEntity::new, ModBlocks.ACACIA_BEE_NEST.get(), ModBlocks.GRASS_BEE_NEST.get(), ModBlocks.JUNGLE_BEE_NEST.get(), ModBlocks.NETHER_BEE_NEST.get(),
                    ModBlocks.PRISMARINE_BEE_NEST.get(), ModBlocks.PURPUR_BEE_NEST.get(), ModBlocks.WITHER_BEE_NEST.get(), ModBlocks.OAK_BEE_NEST.get(), ModBlocks.BIRCH_BEE_NEST.get(), ModBlocks.BROWN_MUSHROOM_BEE_NEST.get(), ModBlocks.CRIMSON_BEE_NEST.get(),
                    ModBlocks.CRIMSON_NYLIUM_BEE_NEST.get(), ModBlocks.DARK_OAK_BEE_NEST.get(), ModBlocks.RED_MUSHROOM_BEE_NEST.get(), ModBlocks.SPRUCE_BEE_NEST.get(), ModBlocks.WARPED_BEE_NEST.get(), ModBlocks.WARPED_NYLIUM_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<TileEntityType<HoneyGeneratorTileEntity>> HONEY_GENERATOR_ENTITY = TILE_ENTITY_TYPES.register("honey_generator", () -> TileEntityType.Builder
            .of(HoneyGeneratorTileEntity::new, ModBlocks.HONEY_GENERATOR.get())
            .build(null));
    public static final RegistryObject<TileEntityType<?>> CREATIVE_GEN_ENTITY = TILE_ENTITY_TYPES.register("creative_gen", () -> TileEntityType.Builder
            .of(CreativeGenTileEntity::new, ModBlocks.CREATIVE_GEN.get())
            .build(null));
    public static final RegistryObject<TileEntityType<?>> APIARY_TILE_ENTITY = TILE_ENTITY_TYPES.register("apiary", () -> TileEntityType.Builder
            .of(ApiaryTileEntity::new, ModBlocks.T1_APIARY_BLOCK.get(), ModBlocks.T2_APIARY_BLOCK.get(), ModBlocks.T3_APIARY_BLOCK.get(), ModBlocks.T4_APIARY_BLOCK.get())
            .build(null));
    public static final RegistryObject<TileEntityType<?>> APIARY_STORAGE_TILE_ENTITY = TILE_ENTITY_TYPES.register("apiary_storage", () -> TileEntityType.Builder
            .of(ApiaryStorageTileEntity::new, ModBlocks.APIARY_STORAGE_BLOCK.get())
            .build(null));
    public static final RegistryObject<TileEntityType<?>> APIARY_BREEDER_TILE_ENTITY = TILE_ENTITY_TYPES.register("apiary_breeder", () -> TileEntityType.Builder
            .of(ApiaryBreederTileEntity::new, ModBlocks.APIARY_BREEDER_BLOCK.get())
            .build(null));
    public static final RegistryObject<TileEntityType<?>> ACCELERATOR_TILE_ENTITY = TILE_ENTITY_TYPES.register("accelerator", () -> TileEntityType.Builder
            .of(AcceleratorTileEntity::new, ModBlocks.ACCELERATOR.get())
            .build(null));
    public static final RegistryObject<TileEntityType<EnderBeeconTileEntity>> ENDER_BEECON_TILE_ENTITY = TILE_ENTITY_TYPES.register("ender_beecon", () -> TileEntityType.Builder
            .of(() -> new EnderBeeconTileEntity(ModBlockEntityTypes.ENDER_BEECON_TILE_ENTITY.get()), ModBlocks.ENDER_BEECON.get())
            .build(null));
    public static final RegistryObject<TileEntityType<HoneyTankTileEntity>> HONEY_TANK_TILE_ENTITY = TILE_ENTITY_TYPES.register("honey_tank", () -> TileEntityType.Builder
            .of(() -> new HoneyTankTileEntity(HoneyTankTileEntity.TankTier.WOODEN), ModBlocks.PURPUR_HONEY_TANK.get(), ModBlocks.NETHER_HONEY_TANK.get(), ModBlocks.WOODEN_HONEY_TANK.get())
            .build(null));
    public static final RegistryObject<TileEntityType<HoneyCongealerTileEntity>> HONEY_CONGEALER_TILE_ENTITY = TILE_ENTITY_TYPES.register("honey_congealer", () -> TileEntityType.Builder
            .of(HoneyCongealerTileEntity::new, ModBlocks.HONEY_CONGEALER.get())
            .build(null));
    public static final RegistryObject<TileEntityType<?>> HONEY_PIPE_TILE_ENTITY = TILE_ENTITY_TYPES.register("honey_pipe", () -> TileEntityType.Builder
            .of(HoneyPipeTileEntity::new, ModBlocks.HONEY_PIPE.get())
            .build(null));
    public static final RegistryObject<TileEntityType<?>> BOTTOMLESS_HONEY_POT_TILE_ENTITY = TILE_ENTITY_TYPES.register("bottomless_honey_pot", () -> TileEntityType.Builder
            .of(BottomlessHoneyPotTileEntity::new, ModBlocks.BOTTOMLESS_HONEY_POT.get())
            .build(null));

}
