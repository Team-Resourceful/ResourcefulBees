package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.blockentities.*;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.defaults.DefaultApiaryTiers;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.registries.RegistryHelper;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntityTypes {

     private ModBlockEntityTypes() {
        throw new UtilityClassError();
    }

    public static final ResourcefulRegistry<BlockEntityType<?>> BLOCK_ENTITY_TYPES = RegistryHelper.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ModConstants.MOD_ID);

    //region Nests
    //region Acacia
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> ACACIA_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/acacia/1", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.ACACIA_BEE_NEST_ENTITY, pos, state), ModBlocks.ACACIA_BEE_NEST.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T1_ACACIA_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/acacia/2", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_ACACIA_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_ACACIA_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T2_ACACIA_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/acacia/3", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_ACACIA_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_ACACIA_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T3_ACACIA_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/acacia/4", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_ACACIA_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_ACACIA_BEEHIVE.get())));
    //endregion
    //region Birch
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> BIRCH_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/birch/1", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.BIRCH_BEE_NEST_ENTITY, pos, state), ModBlocks.BIRCH_BEE_NEST.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T1_BIRCH_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/birch/2", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_BIRCH_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_BIRCH_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T2_BIRCH_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/birch/3", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_BIRCH_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_BIRCH_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T3_BIRCH_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/birch/4", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_BIRCH_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_BIRCH_BEEHIVE.get())));
    //endregion
    //region Brown Mushroom
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> BROWN_MUSHROOM_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/brown_mushroom/1", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.BROWN_MUSHROOM_BEE_NEST_ENTITY, pos, state), ModBlocks.BROWN_MUSHROOM_BEE_NEST.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T1_BROWN_MUSHROOM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/brown_mushroom/2", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_BROWN_MUSHROOM_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_BROWN_MUSHROOM_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T2_BROWN_MUSHROOM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/brown_mushroom/3", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_BROWN_MUSHROOM_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_BROWN_MUSHROOM_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T3_BROWN_MUSHROOM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/brown_mushroom/4", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_BROWN_MUSHROOM_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_BROWN_MUSHROOM_BEEHIVE.get())));
    //endregion
    //region Crimson
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> CRIMSON_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/crimson/1", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.CRIMSON_BEE_NEST_ENTITY, pos, state), ModBlocks.CRIMSON_BEE_NEST.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T1_CRIMSON_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/crimson/2", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_CRIMSON_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_CRIMSON_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T2_CRIMSON_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/crimson/3", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_CRIMSON_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_CRIMSON_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T3_CRIMSON_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/crimson/4", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_CRIMSON_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_CRIMSON_BEEHIVE.get())));
    //endregion
    //region Crimson Nylium
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> CRIMSON_NYLIUM_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/crimson_nylium/1", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.CRIMSON_NYLIUM_BEE_NEST_ENTITY, pos, state), ModBlocks.CRIMSON_NYLIUM_BEE_NEST.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T1_CRIMSON_NYLIUM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/crimson_nylium/2", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_CRIMSON_NYLIUM_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_CRIMSON_NYLIUM_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T2_CRIMSON_NYLIUM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/crimson_nylium/3", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_CRIMSON_NYLIUM_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_CRIMSON_NYLIUM_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T3_CRIMSON_NYLIUM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/crimson_nylium/4", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_CRIMSON_NYLIUM_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_CRIMSON_NYLIUM_BEEHIVE.get())));
    //endregion
    //region Dark Oak
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> DARK_OAK_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/dark_oak/1", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.DARK_OAK_BEE_NEST_ENTITY, pos, state), ModBlocks.DARK_OAK_BEE_NEST.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T1_DARK_OAK_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/dark_oak/2", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_DARK_OAK_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_DARK_OAK_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T2_DARK_OAK_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/dark_oak/3", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_DARK_OAK_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_DARK_OAK_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T3_DARK_OAK_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/dark_oak/4", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_DARK_OAK_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_DARK_OAK_BEEHIVE.get())));
    //endregion
    //region Grass
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> GRASS_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/grass/1", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.GRASS_BEE_NEST_ENTITY, pos, state), ModBlocks.GRASS_BEE_NEST.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T1_GRASS_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/grass/2", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_GRASS_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_GRASS_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T2_GRASS_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/grass/3", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_GRASS_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_GRASS_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T3_GRASS_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/grass/4", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_GRASS_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_GRASS_BEEHIVE.get())));
    //endregion
    //region Jungle
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> JUNGLE_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/jungle/1", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.JUNGLE_BEE_NEST_ENTITY, pos, state), ModBlocks.JUNGLE_BEE_NEST.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T1_JUNGLE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/jungle/2", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_JUNGLE_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_JUNGLE_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T2_JUNGLE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/jungle/3", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_JUNGLE_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_JUNGLE_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T3_JUNGLE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/jungle/4", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_JUNGLE_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_JUNGLE_BEEHIVE.get())));
    //endregion
    //region Nether
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> NETHER_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/netherrack/1", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.NETHER_BEE_NEST_ENTITY, pos, state), ModBlocks.NETHER_BEE_NEST.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T1_NETHER_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/netherrack/2", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_NETHER_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_NETHER_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T2_NETHER_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/netherrack/3", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_NETHER_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_NETHER_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T3_NETHER_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/netherrack/4", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_NETHER_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_NETHER_BEEHIVE.get())));
    //endregion
    //region Oak
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> OAK_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/oak/1", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.OAK_BEE_NEST_ENTITY, pos, state), ModBlocks.OAK_BEE_NEST.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T1_OAK_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/oak/2", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_OAK_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_OAK_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T2_OAK_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/oak/3", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_OAK_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_OAK_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T3_OAK_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/oak/4", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_OAK_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_OAK_BEEHIVE.get())));
    //endregion
    //region Prismarine
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> PRISMARINE_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/prismarine/1", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.PRISMARINE_BEE_NEST_ENTITY, pos, state), ModBlocks.PRISMARINE_BEE_NEST.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T1_PRISMARINE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/prismarine/2", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_PRISMARINE_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_PRISMARINE_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T2_PRISMARINE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/prismarine/3", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_PRISMARINE_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_PRISMARINE_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T3_PRISMARINE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/prismarine/4", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_PRISMARINE_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_PRISMARINE_BEEHIVE.get())));
    //endregion
    //region Purpur
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> PURPUR_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/chorus/1", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.PURPUR_BEE_NEST_ENTITY, pos, state), ModBlocks.PURPUR_BEE_NEST.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T1_PURPUR_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/chorus/2", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_PURPUR_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_PURPUR_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T2_PURPUR_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/chorus/3", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_PURPUR_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_PURPUR_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T3_PURPUR_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/chorus/4", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_PURPUR_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_PURPUR_BEEHIVE.get())));
    //endregion
    //region Red Mushroom
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> RED_MUSHROOM_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/red_mushroom/1", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.RED_MUSHROOM_BEE_NEST_ENTITY, pos, state), ModBlocks.RED_MUSHROOM_BEE_NEST.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T1_RED_MUSHROOM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/red_mushroom/2", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_RED_MUSHROOM_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_RED_MUSHROOM_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T2_RED_MUSHROOM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/red_mushroom/3", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_RED_MUSHROOM_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_RED_MUSHROOM_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T3_RED_MUSHROOM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/red_mushroom/4", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_RED_MUSHROOM_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_RED_MUSHROOM_BEEHIVE.get())));
    //endregion
    //region Spruce
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> SPRUCE_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/spruce/1", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.SPRUCE_BEE_NEST_ENTITY, pos, state), ModBlocks.SPRUCE_BEE_NEST.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T1_SPRUCE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/spruce/2", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_SPRUCE_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_SPRUCE_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T2_SPRUCE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/spruce/3", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_SPRUCE_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_SPRUCE_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T3_SPRUCE_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/spruce/4", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_SPRUCE_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_SPRUCE_BEEHIVE.get())));
    //endregion
    //region Warped
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> WARPED_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/warped/1", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.WARPED_BEE_NEST_ENTITY, pos, state), ModBlocks.WARPED_BEE_NEST.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T1_WARPED_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/warped/2", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_WARPED_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_WARPED_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T2_WARPED_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/warped/3", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_WARPED_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_WARPED_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T3_WARPED_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/warped/4", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_WARPED_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_WARPED_BEEHIVE.get())));
    //endregion
    //region Warped Nylium
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> WARPED_NYLIUM_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/warped_nylium/1", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.WARPED_NYLIUM_BEE_NEST_ENTITY, pos, state), ModBlocks.WARPED_NYLIUM_BEE_NEST.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T1_WARPED_NYLIUM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/warped_nylium/2", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_WARPED_NYLIUM_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_WARPED_NYLIUM_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T2_WARPED_NYLIUM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/warped_nylium/3", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_WARPED_NYLIUM_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_WARPED_NYLIUM_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T3_WARPED_NYLIUM_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/warped_nylium/4", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_WARPED_NYLIUM_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_WARPED_NYLIUM_BEEHIVE.get())));
    //endregion
    //region Wither
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> WITHER_BEE_NEST_ENTITY = BLOCK_ENTITY_TYPES.register("nest/wither/1", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.WITHER_BEE_NEST_ENTITY, pos, state), ModBlocks.WITHER_BEE_NEST.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T1_WITHER_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/wither/2", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T1_WITHER_BEEHIVE_ENTITY, pos, state), ModBlocks.T1_WITHER_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T2_WITHER_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/wither/3", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T2_WITHER_BEEHIVE_ENTITY, pos, state), ModBlocks.T2_WITHER_BEEHIVE.get())));
    public static final RegistryEntry<BlockEntityType<TieredBeehiveBlockEntity>> T3_WITHER_BEEHIVE_ENTITY = BLOCK_ENTITY_TYPES.register("nest/wither/4", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new TieredBeehiveBlockEntity(ModBlockEntityTypes.T3_WITHER_BEEHIVE_ENTITY, pos, state), ModBlocks.T3_WITHER_BEEHIVE.get())));
    //endregion
    //endregion

    //region Apiaries
    public static final RegistryEntry<BlockEntityType<? extends ApiaryBlockEntity>> T1_APIARY_ENTITY = BLOCK_ENTITY_TYPES.register("t1_apiary", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new ApiaryBlockEntity(DefaultApiaryTiers.T1_APIARY, pos, state), ModBlocks.T1_APIARY_BLOCK.get())));
    public static final RegistryEntry<BlockEntityType<? extends ApiaryBlockEntity>> T2_APIARY_ENTITY = BLOCK_ENTITY_TYPES.register("t2_apiary", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new ApiaryBlockEntity(DefaultApiaryTiers.T2_APIARY, pos, state), ModBlocks.T2_APIARY_BLOCK.get())));
    public static final RegistryEntry<BlockEntityType<? extends ApiaryBlockEntity>> T3_APIARY_ENTITY = BLOCK_ENTITY_TYPES.register("t3_apiary", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new ApiaryBlockEntity(DefaultApiaryTiers.T3_APIARY, pos, state), ModBlocks.T3_APIARY_BLOCK.get())));
    public static final RegistryEntry<BlockEntityType<? extends ApiaryBlockEntity>> T4_APIARY_ENTITY = BLOCK_ENTITY_TYPES.register("t4_apiary", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new ApiaryBlockEntity(DefaultApiaryTiers.T4_APIARY, pos, state), ModBlocks.T4_APIARY_BLOCK.get())));
    //endregion

    public static final RegistryEntry<BlockEntityType<BreederBlockEntity>> BREEDER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("breeder", () -> build(BlockEntityType.Builder
            .of(BreederBlockEntity::new, ModBlocks.BREEDER_BLOCK.get())));

    public static final RegistryEntry<BlockEntityType<BeeBoxBlockEntity>> BEE_BOX_ENTITY = BLOCK_ENTITY_TYPES.register("bee_box", () -> build(BlockEntityType.Builder
            .of(BeeBoxBlockEntity::new, ModBlocks.BEE_BOX.get(), ModBlocks.BEE_BOX_TEMP.get())));
    public static final RegistryEntry<BlockEntityType<WaxedSignBlockEntity>> WAXED_SIGN_ENTITY = BLOCK_ENTITY_TYPES.register("waxed_sign", () -> build(BlockEntityType.Builder
            .of(WaxedSignBlockEntity::new, ModBlocks.WAXED_SIGN.get(), ModBlocks.WAXED_WALL_SIGN.get())));
    public static final RegistryEntry<BlockEntityType<WaxedHangingSignBlockEntity>> WAXED_HANGING_SIGN_ENTITY = BLOCK_ENTITY_TYPES.register("waxed_hanging_sign", () -> build(BlockEntityType.Builder
            .of(WaxedHangingSignBlockEntity::new, ModBlocks.WAXED_HANGING_SIGN.get(), ModBlocks.WAXED_WALL_HANGING_SIGN.get())));
    public static final RegistryEntry<BlockEntityType<AcceleratorBlockEntity>> ACCELERATOR_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("accelerator", () -> build(BlockEntityType.Builder
            .of(AcceleratorBlockEntity::new, ModBlocks.ACCELERATOR.get())));

    public static final RegistryEntry<BlockEntityType<? extends FakeFlowerBlockEntity>> FAKE_FLOWER_ENTITY = BLOCK_ENTITY_TYPES.register("fake_flower", () -> build(BlockEntityType.Builder
            .of(FakeFlowerBlockEntity::new, ModBlocks.FAKE_FLOWER.get())));

    public static final RegistryEntry<BlockEntityType<? extends FlowHiveBlockEntity>> FLOW_HIVE_ENTITY = BLOCK_ENTITY_TYPES.register("flow_hive", () -> build(BlockEntityType.Builder
            .of(FlowHiveBlockEntity::new, ModBlocks.FLOW_HIVE.get())));

    public static final RegistryEntry<BlockEntityType<CreativeGenBlockEntity>> CREATIVE_GEN_ENTITY = BLOCK_ENTITY_TYPES.register("creative_gen", () -> build(BlockEntityType.Builder
            .of(CreativeGenBlockEntity::new, ModBlocks.CREATIVE_GEN.get())));

    public static final RegistryEntry<BlockEntityType<EnderBeeconBlockEntity>> ENDER_BEECON_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("ender_beecon", () -> build(BlockEntityType.Builder
            .of(EnderBeeconBlockEntity::new, ModBlocks.ENDER_BEECON.get())));

    public static final RegistryEntry<BlockEntityType<HoneyPotBlockEntity>> HONEY_POT_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("honey_pot", () -> build(BlockEntityType.Builder
            .of(HoneyPotBlockEntity::new, ModBlocks.HONEY_POT.get())));

    public static final RegistryEntry<BlockEntityType<SolidificationChamberBlockEntity>> SOLIDIFICATION_CHAMBER_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("solidification_chamber", () -> build(BlockEntityType.Builder
            .of(SolidificationChamberBlockEntity::new, ModBlocks.SOLIDIFICATION_CHAMBER.get())));

    public static final RegistryEntry<BlockEntityType<CentrifugeBlockEntity>> BASIC_CENTRIFUGE_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeBlockEntity(ModBlockEntityTypes.BASIC_CENTRIFUGE_ENTITY.get(), pos, state), ModBlocks.BASIC_CENTRIFUGE.get())));

    public static final RegistryEntry<BlockEntityType<CentrifugeCrankBlockEntity>> CENTRIFUGE_CRANK_ENTITY = BLOCK_ENTITY_TYPES.register("centrifuge_crank", () -> build(BlockEntityType.Builder
            .of((pos, state) -> new CentrifugeCrankBlockEntity(ModBlockEntityTypes.CENTRIFUGE_CRANK_ENTITY.get(), pos, state), ModBlocks.CENTRIFUGE_CRANK.get())));

    public static final RegistryEntry<BlockEntityType<HoneyGeneratorBlockEntity>> HONEY_GENERATOR_ENTITY = BLOCK_ENTITY_TYPES.register("honey_generator", () -> build(BlockEntityType.Builder
            .of(HoneyGeneratorBlockEntity::new, ModBlocks.HONEY_GENERATOR.get())));

    private static <T extends BlockEntity> BlockEntityType<T> build(BlockEntityType.Builder<T> builder) {
        //noinspection ConstantConditions
        return builder.build(null);
    }
}
