package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.*;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.blockentity.*;
import com.teamresourceful.resourcefulbees.common.blockentity.breeder.BreederBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentity.ApiaryBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntityTypes {

    private ModBlockEntityTypes() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, ResourcefulBees.MOD_ID);


    //region Nests
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> ACACIA_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/acacia/1", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.ACACIA_BEE_NEST_ENTITY, pos, state), ModBlocks.ACACIA_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> BIRCH_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/birch/1", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.BIRCH_BEE_NEST_ENTITY, pos, state), ModBlocks.BIRCH_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> BROWN_MUSHROOM_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/brown_mushroom/1", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.BROWN_MUSHROOM_BEE_NEST_ENTITY, pos, state), ModBlocks.BROWN_MUSHROOM_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> CRIMSON_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/crimson/1", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.CRIMSON_BEE_NEST_ENTITY, pos, state), ModBlocks.CRIMSON_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> CRIMSON_NYLIUM_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/crimson_nylium/1", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.CRIMSON_NYLIUM_BEE_NEST_ENTITY, pos, state), ModBlocks.CRIMSON_NYLIUM_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> DARK_OAK_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/dark_oak/1", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.DARK_OAK_BEE_NEST_ENTITY, pos, state), ModBlocks.DARK_OAK_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> GRASS_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/grass/1", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.GRASS_BEE_NEST_ENTITY, pos, state), ModBlocks.GRASS_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> JUNGLE_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/jungle/1", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.JUNGLE_BEE_NEST_ENTITY, pos, state), ModBlocks.JUNGLE_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> NETHER_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/netherrack/1", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.NETHER_BEE_NEST_ENTITY, pos, state), ModBlocks.NETHER_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> OAK_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/oak/1", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.OAK_BEE_NEST_ENTITY, pos, state), ModBlocks.OAK_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> PRISMARINE_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/prismarine/1", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.PRISMARINE_BEE_NEST_ENTITY, pos, state), ModBlocks.PRISMARINE_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> PURPUR_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/chorus/1", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.PURPUR_BEE_NEST_ENTITY, pos, state), ModBlocks.PURPUR_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> RED_MUSHROOM_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/red_mushroom/1", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.RED_MUSHROOM_BEE_NEST_ENTITY, pos, state), ModBlocks.RED_MUSHROOM_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> SPRUCE_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/spruce/1", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.SPRUCE_BEE_NEST_ENTITY, pos, state), ModBlocks.SPRUCE_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> WARPED_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/warped/1", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.WARPED_BEE_NEST_ENTITY, pos, state), ModBlocks.WARPED_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> WARPED_NYLIUM_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/warped_nylium/1", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.WARPED_NYLIUM_BEE_NEST_ENTITY, pos, state), ModBlocks.WARPED_NYLIUM_BEE_NEST.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> WITHER_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/wither/1", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.WITHER_BEE_NEST_ENTITY, pos, state), ModBlocks.WITHER_BEE_NEST.get())
            .build(null));
    //endregion

    //region T1 Hives
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T1_ACACIA_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/acacia/2", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_ACACIA_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_ACACIA_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T1_BIRCH_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/birch/2", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_BIRCH_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_BIRCH_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T1_BROWN_MUSHROOM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/brown_mushroom/2", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_BROWN_MUSHROOM_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_BROWN_MUSHROOM_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T1_CRIMSON_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/crimson/2", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_CRIMSON_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_CRIMSON_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T1_CRIMSON_NYLIUM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/crimson_nylium/2", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_CRIMSON_NYLIUM_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_CRIMSON_NYLIUM_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T1_DARK_OAK_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/dark_oak/2", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_DARK_OAK_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_DARK_OAK_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T1_GRASS_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/grass/2", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_GRASS_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_GRASS_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T1_JUNGLE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/jungle/2", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_JUNGLE_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_JUNGLE_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T1_NETHER_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/netherrack/2", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_NETHER_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_NETHER_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T1_OAK_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/oak/2", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_OAK_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_OAK_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T1_PRISMARINE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/prismarine/2", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_PRISMARINE_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_PRISMARINE_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T1_PURPUR_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/chorus/2", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_PURPUR_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_PURPUR_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T1_RED_MUSHROOM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/red_mushroom/2", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_RED_MUSHROOM_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_RED_MUSHROOM_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T1_SPRUCE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/spruce/2", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_SPRUCE_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_SPRUCE_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T1_WARPED_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/warped/2", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_WARPED_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_WARPED_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T1_WARPED_NYLIUM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/warped_nylium/2", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_WARPED_NYLIUM_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_WARPED_NYLIUM_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T1_WITHER_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/wither/2", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_WITHER_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_WITHER_BEEHIVE.get())
            .build(null));
    //endregion

    //region T2 Hives
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T2_ACACIA_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/acacia/3", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_ACACIA_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_ACACIA_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T2_BIRCH_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/birch/3", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_BIRCH_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_BIRCH_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T2_BROWN_MUSHROOM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/brown_mushroom/3", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_BROWN_MUSHROOM_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_BROWN_MUSHROOM_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T2_CRIMSON_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/crimson/3", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_CRIMSON_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_CRIMSON_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T2_CRIMSON_NYLIUM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/crimson_nylium/3", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_CRIMSON_NYLIUM_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_CRIMSON_NYLIUM_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T2_DARK_OAK_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/dark_oak/3", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_DARK_OAK_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_DARK_OAK_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T2_GRASS_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/grass/3", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_GRASS_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_GRASS_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T2_JUNGLE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/jungle/3", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_JUNGLE_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_JUNGLE_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T2_NETHER_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/netherrack/3", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_NETHER_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_NETHER_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T2_OAK_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/oak/3", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_OAK_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_OAK_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T2_PRISMARINE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/prismarine/3", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_PRISMARINE_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_PRISMARINE_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T2_PURPUR_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/chorus/3", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_PURPUR_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_PURPUR_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T2_RED_MUSHROOM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/red_mushroom/3", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_RED_MUSHROOM_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_RED_MUSHROOM_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T2_SPRUCE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/spruce/3", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_SPRUCE_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_SPRUCE_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T2_WARPED_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/warped/3", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_WARPED_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_WARPED_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T2_WARPED_NYLIUM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/warped_nylium/3", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_WARPED_NYLIUM_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_WARPED_NYLIUM_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T2_WITHER_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/wither/3", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_WITHER_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_WITHER_BEEHIVE.get())
            .build(null));
    //endregion

    //region T3 Hives
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T3_ACACIA_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/acacia/4", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_ACACIA_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_ACACIA_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T3_BIRCH_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/birch/4", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_BIRCH_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_BIRCH_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T3_BROWN_MUSHROOM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/brown_mushroom/4", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_BROWN_MUSHROOM_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_BROWN_MUSHROOM_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T3_CRIMSON_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/crimson/4", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_CRIMSON_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_CRIMSON_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T3_CRIMSON_NYLIUM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/crimson_nylium/4", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_CRIMSON_NYLIUM_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_CRIMSON_NYLIUM_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T3_DARK_OAK_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/dark_oak/4", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_DARK_OAK_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_DARK_OAK_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T3_GRASS_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/grass/4", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_GRASS_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_GRASS_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T3_JUNGLE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/jungle/4", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_JUNGLE_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_JUNGLE_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T3_NETHER_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/netherrack/4", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_NETHER_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_NETHER_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T3_OAK_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/oak/4", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_OAK_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_OAK_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T3_PRISMARINE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/prismarine/4", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_PRISMARINE_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_PRISMARINE_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T3_PURPUR_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/chorus/4", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_PURPUR_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_PURPUR_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T3_RED_MUSHROOM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/red_mushroom/4", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_RED_MUSHROOM_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_RED_MUSHROOM_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T3_SPRUCE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/spruce/4", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_SPRUCE_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_SPRUCE_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T3_WARPED_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/warped/4", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_WARPED_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_WARPED_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T3_WARPED_NYLIUM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/warped_nylium/4", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_WARPED_NYLIUM_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_WARPED_NYLIUM_BEEHIVE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> T3_WITHER_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/wither/4", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_WITHER_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_WITHER_BEEHIVE.get())
            .build(null));
    //endregion

    //region Apiaries
    public static final RegistryObject<BlockEntityType<? extends ApiaryBlockEntity>> T1_APIARY_ENTITY = BLOCK_ENTITY_TYPES.register("t1_apiary", () -> BlockEntityType.Builder
            .of((pos, state) -> new ApiaryBlockEntity(ApiaryTier.T1_APIARY, pos, state), ModBlocks.T1_APIARY_BLOCK.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<? extends ApiaryBlockEntity>> T2_APIARY_ENTITY = BLOCK_ENTITY_TYPES.register("t2_apiary", () -> BlockEntityType.Builder
            .of((pos, state) -> new ApiaryBlockEntity(ApiaryTier.T2_APIARY, pos, state), ModBlocks.T2_APIARY_BLOCK.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<? extends ApiaryBlockEntity>> T3_APIARY_ENTITY = BLOCK_ENTITY_TYPES.register("t3_apiary", () -> BlockEntityType.Builder
            .of((pos, state) -> new ApiaryBlockEntity(ApiaryTier.T3_APIARY, pos, state), ModBlocks.T3_APIARY_BLOCK.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<? extends ApiaryBlockEntity>> T4_APIARY_ENTITY = BLOCK_ENTITY_TYPES.register("t4_apiary", () -> BlockEntityType.Builder
            .of((pos, state) -> new ApiaryBlockEntity(ApiaryTier.T4_APIARY, pos, state), ModBlocks.T4_APIARY_BLOCK.get())
            .build(null));
    //endregion

    public static final RegistryObject<BlockEntityType<HoneyGeneratorBlockEntity>> HONEY_GENERATOR_ENTITY = BLOCK_ENTITY_TYPES.register("honey_generator", () -> BlockEntityType.Builder
            .of(HoneyGeneratorBlockEntity::new, ModBlocks.HONEY_GENERATOR.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CreativeGenBlockEntity>> CREATIVE_GEN_ENTITY = BLOCK_ENTITY_TYPES.register("creative_gen", () -> BlockEntityType.Builder
            .of(CreativeGenBlockEntity::new, ModBlocks.CREATIVE_GEN.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<BreederBlockEntity>> BREEDER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("breeder", () -> BlockEntityType.Builder
            .of(BreederBlockEntity::new, ModBlocks.BREEDER_BLOCK.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<AcceleratorBlockEntity>> ACCELERATOR_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("accelerator", () -> BlockEntityType.Builder
            .of(AcceleratorBlockEntity::new, ModBlocks.ACCELERATOR.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<EnderBeeconBlockEntity>> ENDER_BEECON_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("ender_beecon", () -> BlockEntityType.Builder
            .of(EnderBeeconBlockEntity::new, ModBlocks.ENDER_BEECON.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<SolidificationChamberBlockEntity>> SOLIDIFICATION_CHAMBER_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("solidification_chamber", () -> BlockEntityType.Builder
            .of(SolidificationChamberBlockEntity::new, ModBlocks.SOLIDIFICATION_CHAMBER.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<HoneyPotBlockEntity>> HONEY_POT_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("honey_pot", () -> BlockEntityType.Builder
            .of(HoneyPotBlockEntity::new, ModBlocks.HONEY_POT.get())
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
            .of((pos, state) -> new CentrifugeTerminalEntity(ModBlockEntityTypes.CENTRIFUGE_BASIC_TERMINAL_ENTITY, CentrifugeTier.BASIC, pos, state), ModBlocks.CENTRIFUGE_BASIC_TERMINAL.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeTerminalEntity>> CENTRIFUGE_ADVANCED_TERMINAL_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/terminal/advanced", () -> BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeTerminalEntity(ModBlockEntityTypes.CENTRIFUGE_ADVANCED_TERMINAL_ENTITY, CentrifugeTier.ADVANCED, pos, state), ModBlocks.CENTRIFUGE_ADVANCED_TERMINAL.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeTerminalEntity>> CENTRIFUGE_ELITE_TERMINAL_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/terminal/elite", () -> BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeTerminalEntity(ModBlockEntityTypes.CENTRIFUGE_ELITE_TERMINAL_ENTITY, CentrifugeTier.ELITE, pos, state), ModBlocks.CENTRIFUGE_ELITE_TERMINAL.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeTerminalEntity>> CENTRIFUGE_ULTIMATE_TERMINAL_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/terminal/ultimate", () -> BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeTerminalEntity(ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_TERMINAL_ENTITY, CentrifugeTier.ULTIMATE, pos, state), ModBlocks.CENTRIFUGE_ULTIMATE_TERMINAL.get())
            .build(null));

    //VOID
    public static final RegistryObject<BlockEntityType<CentrifugeVoidEntity>> CENTRIFUGE_BASIC_VOID_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/void/basic", () -> BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeVoidEntity(ModBlockEntityTypes.CENTRIFUGE_BASIC_VOID_ENTITY, CentrifugeTier.BASIC, pos, state), ModBlocks.CENTRIFUGE_BASIC_VOID.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeVoidEntity>> CENTRIFUGE_ADVANCED_VOID_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/void/advanced", () -> BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeVoidEntity(ModBlockEntityTypes.CENTRIFUGE_ADVANCED_VOID_ENTITY, CentrifugeTier.ADVANCED, pos, state), ModBlocks.CENTRIFUGE_ADVANCED_VOID.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeVoidEntity>> CENTRIFUGE_ELITE_VOID_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/void/elite", () -> BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeVoidEntity(ModBlockEntityTypes.CENTRIFUGE_ELITE_VOID_ENTITY, CentrifugeTier.ELITE, pos, state), ModBlocks.CENTRIFUGE_ELITE_VOID.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeVoidEntity>> CENTRIFUGE_ULTIMATE_VOID_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/void/ultimate", () -> BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeVoidEntity(ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_VOID_ENTITY, CentrifugeTier.ULTIMATE, pos, state), ModBlocks.CENTRIFUGE_ULTIMATE_VOID.get())
            .build(null));

    //INPUT
    public static final RegistryObject<BlockEntityType<CentrifugeInputEntity>> CENTRIFUGE_BASIC_INPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/input/item/basic", () -> BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeInputEntity(ModBlockEntityTypes.CENTRIFUGE_BASIC_INPUT_ENTITY, CentrifugeTier.BASIC, pos, state), ModBlocks.CENTRIFUGE_BASIC_INPUT.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeInputEntity>> CENTRIFUGE_ADVANCED_INPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/input/item/advanced", () -> BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeInputEntity(ModBlockEntityTypes.CENTRIFUGE_ADVANCED_INPUT_ENTITY, CentrifugeTier.ADVANCED, pos, state), ModBlocks.CENTRIFUGE_ADVANCED_INPUT.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeInputEntity>> CENTRIFUGE_ELITE_INPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/input/item/elite", () -> BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeInputEntity(ModBlockEntityTypes.CENTRIFUGE_ELITE_INPUT_ENTITY, CentrifugeTier.ELITE, pos, state), ModBlocks.CENTRIFUGE_ELITE_INPUT.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeInputEntity>> CENTRIFUGE_ULTIMATE_INPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/input/item/ultimate", () -> BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeInputEntity(ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_INPUT_ENTITY, CentrifugeTier.ULTIMATE, pos, state), ModBlocks.CENTRIFUGE_ULTIMATE_INPUT.get())
            .build(null));

    //ENERGY PORT
    public static final RegistryObject<BlockEntityType<CentrifugeEnergyPortEntity>> CENTRIFUGE_BASIC_ENERGY_PORT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/input/energy/basic", () -> BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeEnergyPortEntity(ModBlockEntityTypes.CENTRIFUGE_BASIC_ENERGY_PORT_ENTITY, CentrifugeTier.BASIC, pos, state), ModBlocks.CENTRIFUGE_BASIC_ENERGY_PORT.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeEnergyPortEntity>> CENTRIFUGE_ADVANCED_ENERGY_PORT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/input/energy/advanced", () -> BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeEnergyPortEntity(ModBlockEntityTypes.CENTRIFUGE_ADVANCED_ENERGY_PORT_ENTITY, CentrifugeTier.ADVANCED, pos, state), ModBlocks.CENTRIFUGE_ADVANCED_ENERGY_PORT.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeEnergyPortEntity>> CENTRIFUGE_ELITE_ENERGY_PORT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/input/energy/elite", () -> BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeEnergyPortEntity(ModBlockEntityTypes.CENTRIFUGE_ELITE_ENERGY_PORT_ENTITY, CentrifugeTier.ELITE, pos, state), ModBlocks.CENTRIFUGE_ELITE_ENERGY_PORT.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeEnergyPortEntity>> CENTRIFUGE_ULTIMATE_ENERGY_PORT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/input/energy/ultimate", () -> BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeEnergyPortEntity(ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_ENERGY_PORT_ENTITY, CentrifugeTier.ULTIMATE, pos, state), ModBlocks.CENTRIFUGE_ULTIMATE_ENERGY_PORT.get())
            .build(null));

    //ITEM OUTPUT
    public static final RegistryObject<BlockEntityType<CentrifugeItemOutputEntity>> CENTRIFUGE_BASIC_ITEM_OUTPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/output/item/basic", () -> BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeItemOutputEntity(ModBlockEntityTypes.CENTRIFUGE_BASIC_ITEM_OUTPUT_ENTITY, CentrifugeTier.BASIC, pos, state), ModBlocks.CENTRIFUGE_BASIC_ITEM_OUTPUT.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeItemOutputEntity>> CENTRIFUGE_ADVANCED_ITEM_OUTPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/output/item/advanced", () -> BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeItemOutputEntity(ModBlockEntityTypes.CENTRIFUGE_ADVANCED_ITEM_OUTPUT_ENTITY, CentrifugeTier.ADVANCED, pos, state), ModBlocks.CENTRIFUGE_ADVANCED_ITEM_OUTPUT.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeItemOutputEntity>> CENTRIFUGE_ELITE_ITEM_OUTPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/output/item/elite", () -> BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeItemOutputEntity(ModBlockEntityTypes.CENTRIFUGE_ELITE_ITEM_OUTPUT_ENTITY, CentrifugeTier.ELITE, pos, state), ModBlocks.CENTRIFUGE_ELITE_ITEM_OUTPUT.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeItemOutputEntity>> CENTRIFUGE_ULTIMATE_ITEM_OUTPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/output/item/ultimate", () -> BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeItemOutputEntity(ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_ITEM_OUTPUT_ENTITY, CentrifugeTier.ULTIMATE, pos, state), ModBlocks.CENTRIFUGE_ULTIMATE_ITEM_OUTPUT.get())
            .build(null));

    //FLUID OUTPUT
    public static final RegistryObject<BlockEntityType<CentrifugeFluidOutputEntity>> CENTRIFUGE_BASIC_FLUID_OUTPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/output/fluid/basic", () -> BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeFluidOutputEntity(ModBlockEntityTypes.CENTRIFUGE_BASIC_FLUID_OUTPUT_ENTITY, CentrifugeTier.BASIC, pos, state), ModBlocks.CENTRIFUGE_BASIC_FLUID_OUTPUT.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeFluidOutputEntity>> CENTRIFUGE_ADVANCED_FLUID_OUTPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/output/fluid/advanced", () -> BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeFluidOutputEntity(ModBlockEntityTypes.CENTRIFUGE_ADVANCED_FLUID_OUTPUT_ENTITY, CentrifugeTier.ADVANCED, pos, state), ModBlocks.CENTRIFUGE_ADVANCED_FLUID_OUTPUT.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeFluidOutputEntity>> CENTRIFUGE_ELITE_FLUID_OUTPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/output/fluid/elite", () -> BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeFluidOutputEntity(ModBlockEntityTypes.CENTRIFUGE_ELITE_FLUID_OUTPUT_ENTITY, CentrifugeTier.ELITE, pos, state), ModBlocks.CENTRIFUGE_ELITE_FLUID_OUTPUT.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<CentrifugeFluidOutputEntity>> CENTRIFUGE_ULTIMATE_FLUID_OUTPUT_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge/output/fluid/ultimate", () -> BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeFluidOutputEntity(ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_FLUID_OUTPUT_ENTITY, CentrifugeTier.ULTIMATE, pos, state), ModBlocks.CENTRIFUGE_ULTIMATE_FLUID_OUTPUT.get())
            .build(null));
    //endregion
}
