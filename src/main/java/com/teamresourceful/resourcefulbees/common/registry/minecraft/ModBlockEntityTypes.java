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


    //region Nests
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> ACACIA_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("acacia_bee_nest", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.ACACIA_BEE_NEST_ENTITY, pos, state), ModBlocks.ACACIA_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> BIRCH_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("birch_bee_nest", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.BIRCH_BEE_NEST_ENTITY, pos, state), ModBlocks.BIRCH_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> BROWN_MUSHROOM_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("brown_mushroom_bee_nest", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.BROWN_MUSHROOM_BEE_NEST_ENTITY, pos, state), ModBlocks.BROWN_MUSHROOM_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> CRIMSON_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("crimson_bee_nest", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.CRIMSON_BEE_NEST_ENTITY, pos, state), ModBlocks.CRIMSON_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> CRIMSON_NYLIUM_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("crimson_nylium_bee_nest", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.CRIMSON_NYLIUM_BEE_NEST_ENTITY, pos, state), ModBlocks.CRIMSON_NYLIUM_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> DARK_OAK_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("dark_oak_bee_nest", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.DARK_OAK_BEE_NEST_ENTITY, pos, state), ModBlocks.DARK_OAK_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> GRASS_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("grass_bee_nest", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.GRASS_BEE_NEST_ENTITY, pos, state), ModBlocks.GRASS_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> JUNGLE_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("jungle_bee_nest", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.JUNGLE_BEE_NEST_ENTITY, pos, state), ModBlocks.JUNGLE_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> NETHER_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nether_bee_nest", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.NETHER_BEE_NEST_ENTITY, pos, state), ModBlocks.NETHER_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> OAK_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("oak_bee_nest", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.OAK_BEE_NEST_ENTITY, pos, state), ModBlocks.OAK_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> PRISMARINE_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("prismarine_bee_nest", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.PRISMARINE_BEE_NEST_ENTITY, pos, state), ModBlocks.PRISMARINE_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> PURPUR_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("purpur_bee_nest", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.PURPUR_BEE_NEST_ENTITY, pos, state), ModBlocks.PURPUR_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> RED_MUSHROOM_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("red_mushroom_bee_nest", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.RED_MUSHROOM_BEE_NEST_ENTITY, pos, state), ModBlocks.RED_MUSHROOM_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> SPRUCE_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("spruce_bee_nest", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.SPRUCE_BEE_NEST_ENTITY, pos, state), ModBlocks.SPRUCE_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> WARPED_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("warped_bee_nest", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.WARPED_BEE_NEST_ENTITY, pos, state), ModBlocks.WARPED_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> WARPED_NYLIUM_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("warped_nylium_bee_nest", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.WARPED_NYLIUM_BEE_NEST_ENTITY, pos, state), ModBlocks.WARPED_NYLIUM_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> WITHER_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("wither_bee_nest", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.WITHER_BEE_NEST_ENTITY, pos, state), ModBlocks.WITHER_BEE_NEST.get())
            .build(null));
    //endregion

    //region T1 Hives
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T1_ACACIA_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t1_acacia_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T1_ACACIA_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_ACACIA_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T1_BIRCH_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t1_birch_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T1_BIRCH_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_BIRCH_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T1_BROWN_MUSHROOM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t1_brown_mushroom_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T1_BROWN_MUSHROOM_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_BROWN_MUSHROOM_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T1_CRIMSON_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t1_crimson_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T1_CRIMSON_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_CRIMSON_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T1_CRIMSON_NYLIUM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t1_crimson_nylium_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T1_CRIMSON_NYLIUM_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_CRIMSON_NYLIUM_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T1_DARK_OAK_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t1_dark_oak_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T1_DARK_OAK_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_DARK_OAK_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T1_GRASS_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t1_grass_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T1_GRASS_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_GRASS_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T1_JUNGLE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t1_jungle_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T1_JUNGLE_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_JUNGLE_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T1_NETHER_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t1_nether_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T1_NETHER_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_NETHER_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T1_OAK_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t1_oak_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T1_OAK_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_OAK_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T1_PRISMARINE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t1_prismarine_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T1_PRISMARINE_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_PRISMARINE_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T1_PURPUR_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t1_purpur_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T1_PURPUR_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_PURPUR_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T1_RED_MUSHROOM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t1_red_mushroom_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T1_RED_MUSHROOM_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_RED_MUSHROOM_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T1_SPRUCE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t1_spruce_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T1_SPRUCE_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_SPRUCE_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T1_WARPED_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t1_warped_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T1_WARPED_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_WARPED_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T1_WARPED_NYLIUM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t1_warped_nylium_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T1_WARPED_NYLIUM_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_WARPED_NYLIUM_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T1_WITHER_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t1_wither_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T1_WITHER_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_WITHER_BEEHIVE.get())
            .build(null));
    //endregion

    //region T2 Hives
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T2_ACACIA_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t2_acacia_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T2_ACACIA_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_ACACIA_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T2_BIRCH_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t2_birch_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T2_BIRCH_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_BIRCH_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T2_BROWN_MUSHROOM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t2_brown_mushroom_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T2_BROWN_MUSHROOM_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_BROWN_MUSHROOM_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T2_CRIMSON_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t2_crimson_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T2_CRIMSON_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_CRIMSON_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T2_CRIMSON_NYLIUM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t2_crimson_nylium_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T2_CRIMSON_NYLIUM_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_CRIMSON_NYLIUM_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T2_DARK_OAK_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t2_dark_oak_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T2_DARK_OAK_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_DARK_OAK_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T2_GRASS_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t2_grass_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T2_GRASS_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_GRASS_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T2_JUNGLE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t2_jungle_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T2_JUNGLE_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_JUNGLE_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T2_NETHER_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t2_nether_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T2_NETHER_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_NETHER_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T2_OAK_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t2_oak_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T2_OAK_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_OAK_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T2_PRISMARINE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t2_prismarine_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T2_PRISMARINE_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_PRISMARINE_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T2_PURPUR_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t2_purpur_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T2_PURPUR_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_PURPUR_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T2_RED_MUSHROOM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t2_red_mushroom_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T2_RED_MUSHROOM_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_RED_MUSHROOM_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T2_SPRUCE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t2_spruce_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T2_SPRUCE_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_SPRUCE_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T2_WARPED_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t2_warped_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T2_WARPED_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_WARPED_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T2_WARPED_NYLIUM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t2_warped_nylium_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T2_WARPED_NYLIUM_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_WARPED_NYLIUM_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T2_WITHER_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t2_wither_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T2_WITHER_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_WITHER_BEEHIVE.get())
            .build(null));
    //endregion

    //region T3 Hives
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T3_ACACIA_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t3_acacia_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T3_ACACIA_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_ACACIA_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T3_BIRCH_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t3_birch_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T3_BIRCH_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_BIRCH_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T3_BROWN_MUSHROOM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t3_brown_mushroom_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T3_BROWN_MUSHROOM_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_BROWN_MUSHROOM_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T3_CRIMSON_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t3_crimson_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T3_CRIMSON_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_CRIMSON_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T3_CRIMSON_NYLIUM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t3_crimson_nylium_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T3_CRIMSON_NYLIUM_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_CRIMSON_NYLIUM_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T3_DARK_OAK_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t3_dark_oak_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T3_DARK_OAK_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_DARK_OAK_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T3_GRASS_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t3_grass_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T3_GRASS_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_GRASS_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T3_JUNGLE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t3_jungle_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T3_JUNGLE_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_JUNGLE_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T3_NETHER_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t3_nether_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T3_NETHER_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_NETHER_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T3_OAK_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t3_oak_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T3_OAK_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_OAK_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T3_PRISMARINE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t3_prismarine_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T3_PRISMARINE_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_PRISMARINE_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T3_PURPUR_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t3_purpur_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T3_PURPUR_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_PURPUR_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T3_RED_MUSHROOM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t3_red_mushroom_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T3_RED_MUSHROOM_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_RED_MUSHROOM_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T3_SPRUCE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t3_spruce_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T3_SPRUCE_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_SPRUCE_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T3_WARPED_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t3_warped_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T3_WARPED_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_WARPED_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T3_WARPED_NYLIUM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t3_warped_nylium_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T3_WARPED_NYLIUM_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_WARPED_NYLIUM_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> T3_WITHER_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("t3_wither_beehive", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveTileEntity(ModBlockEntityTypes.T3_WITHER_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_WITHER_BEEHIVE.get())
            .build(null));
    //endregion

    //region Apiaries
    public static final RegistryObject<BlockEntityType<ApiaryTileEntity>> T1_APIARY_ENTITY = BLOCK_ENTITY_TYPES.register("t1_apiary", () -> BlockEntityType.Builder
            .of(ApiaryTileEntity::new, ModBlocks.T1_APIARY_BLOCK.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<ApiaryTileEntity>> T2_APIARY_ENTITY = BLOCK_ENTITY_TYPES.register("t2_apiary", () -> BlockEntityType.Builder
            .of(ApiaryTileEntity::new, ModBlocks.T2_APIARY_BLOCK.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<ApiaryTileEntity>> T3_APIARY_ENTITY = BLOCK_ENTITY_TYPES.register("t3_apiary", () -> BlockEntityType.Builder
            .of(ApiaryTileEntity::new, ModBlocks.T3_APIARY_BLOCK.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<ApiaryTileEntity>> T4_APIARY_ENTITY = BLOCK_ENTITY_TYPES.register("t4_apiary", () -> BlockEntityType.Builder
            .of(ApiaryTileEntity::new, ModBlocks.T4_APIARY_BLOCK.get())
            .build(null));
    //endregion

























    public static final RegistryObject<BlockEntityType<HoneyGeneratorTileEntity>> HONEY_GENERATOR_ENTITY = BLOCK_ENTITY_TYPES.register("honey_generator", () -> BlockEntityType.Builder
            .of(HoneyGeneratorTileEntity::new, ModBlocks.HONEY_GENERATOR.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CreativeGenTileEntity>> CREATIVE_GEN_ENTITY = BLOCK_ENTITY_TYPES.register("creative_gen", () -> BlockEntityType.Builder
            .of(CreativeGenTileEntity::new, ModBlocks.CREATIVE_GEN.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<ApiaryTileEntity>> APIARY_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("apiary", () -> BlockEntityType.Builder
            .of(ApiaryTileEntity::new, ModBlocks.T1_APIARY_BLOCK.get(), ModBlocks.T2_APIARY_BLOCK.get(), ModBlocks.T3_APIARY_BLOCK.get(), ModBlocks.T4_APIARY_BLOCK.get())
            .build(null));
/*    public static final RegistryObject<BlockEntityType<ApiaryStorageTileEntity>> APIARY_STORAGE_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("apiary_storage", () -> BlockEntityType.Builder
            .of(ApiaryStorageTileEntity::new, ModBlocks.APIARY_STORAGE_BLOCK.get())
            .build(null));*/
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
