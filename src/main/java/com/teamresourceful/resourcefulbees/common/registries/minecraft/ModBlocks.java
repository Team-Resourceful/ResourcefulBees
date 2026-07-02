package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.api.tiers.BeehiveTier;
import com.teamresourceful.resourcefulbees.common.blockentities.TieredBeehiveBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.*;
import com.teamresourceful.resourcefulbees.common.blocks.base.BeeHouseTopBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.defaults.DefaultApiaryTiers;
import com.teamresourceful.resourcefulbees.common.lib.defaults.DefaultBeehiveTiers;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.builtin.ResourcefulBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;
import java.util.function.Supplier;

public final class ModBlocks {


    private ModBlocks() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final ResourcefulBlockRegistry BLOCKS = ResourcefulRegistries.createForBlocks(ModConstants.MOD_ID);
    public static final ResourcefulBlockRegistry HIVES = ResourcefulRegistries.createForBlocks(BLOCKS);
    public static final ResourcefulBlockRegistry APIARIES = ResourcefulRegistries.createForBlocks(BLOCKS);
    public static final ResourcefulBlockRegistry CENTRIFUGE_BLOCKS = ResourcefulRegistries.createForBlocks(BLOCKS);

    public static final ResourcefulBlockRegistry HONEYCOMB_BLOCKS = ResourcefulRegistries.createForBlocks(BLOCKS);
    public static final ResourcefulBlockRegistry HONEY_BLOCKS = ResourcefulRegistries.createForBlocks(BLOCKS);
    public static final ResourcefulBlockRegistry HONEY_FLUID_BLOCKS = ResourcefulRegistries.createForBlocks(BLOCKS);

    public static final BlockBehaviour.Properties WAXED_PLANKS_PROPERTIES = BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD);
    public static final BlockBehaviour.Properties CENTRIFUGE_PROPERTIES = BlockBehaviour.Properties.of().strength(2).sound(SoundType.METAL);
    private static final BlockBehaviour.Properties WOOD_NEST_PROPERTIES = BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1F).sound(SoundType.WOOD);
    private static final BlockBehaviour.Properties APIARY_PROPERTIES = BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(5f, 6f).sound(SoundType.METAL);
    public static final BlockBehaviour.Properties HONEY_FLUID_BLOCK_PROPERTIES = BlockBehaviour.Properties.of()
            .mapColor(MapColor.TERRACOTTA_ORANGE)
            .noOcclusion()
            .replaceable()
            .noCollision()
            .strength(100.0F)
            .noLootTable();

    public static final BlockSetType WAX_BLOCK_SET = BlockSetType.register(new BlockSetType("resourcefulbees:waxed"));
    public static final WoodType WAXED_WOOD_TYPE = WoodType.register(new WoodType("resourcefulbees:waxed", WAX_BLOCK_SET));

    private static BlockBehaviour.Properties makeNestProperty(MapColor color, SoundType soundType){
        return BlockBehaviour.Properties.of().strength(1.0F).mapColor(color).sound(soundType);
    }

    private static TieredBeehiveBlock createNest(Supplier<BlockEntityType<TieredBeehiveBlockEntity>> entityType, BeehiveTier tier, BlockBehaviour.Properties properties) {
        return new TieredBeehiveBlock(entityType, tier, properties);
    }

    private static boolean never(BlockState state, BlockGetter blockGetter, BlockPos blockPos) {
        return false;
    }

    private static RegistryEntry<Block> registerBlock(ResourcefulBlockRegistry registry, String id, Function<BlockBehaviour.Properties, Block> factory, Supplier<BlockBehaviour.Properties> getter) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, ModIdentifier.of(id));
        return registry.register(id,() -> factory.apply(getter.get().setId(key)));
    }

    public static RegistryEntry<Block> registerHive(String id, int tier, Supplier<BlockBehaviour.Properties> hiveProps) {
        return registerBlock(HIVES, id, properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.ordinalOf(tier), properties), hiveProps);
    }

//    //TODO figure out how to make this nest registration cleaner and reduce duplicate processes
//    //region Nests
//    //region Acacia
//    public static final RegistryEntry<Block> ACACIA_BEE_NEST = registerBlock(HIVES, "nest/acacia/1", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T1_NEST, properties), () -> WOOD_NEST_PROPERTIES);
//    public static final RegistryEntry<Block> T1_ACACIA_BEEHIVE = registerBlock(HIVES,"nest/acacia/2", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, properties), () -> WOOD_NEST_PROPERTIES);
//    public static final RegistryEntry<Block> T2_ACACIA_BEEHIVE = registerBlock(HIVES,"nest/acacia/3", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, properties), () -> WOOD_NEST_PROPERTIES);
//    public static final RegistryEntry<Block> T3_ACACIA_BEEHIVE = registerBlock(HIVES,"nest/acacia/4", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, properties), () -> WOOD_NEST_PROPERTIES);
//    //endregion
//    //region Birch
//    public static final RegistryEntry<Block> BIRCH_BEE_NEST = registerBlock(HIVES, "nest/birch/1", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T1_NEST, properties), () -> WOOD_NEST_PROPERTIES);
//    public static final RegistryEntry<Block> T1_BIRCH_BEEHIVE = registerBlock(HIVES, "nest/birch/2", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, properties), () -> WOOD_NEST_PROPERTIES);
//    public static final RegistryEntry<Block> T2_BIRCH_BEEHIVE = registerBlock(HIVES, "nest/birch/3", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, properties), () -> WOOD_NEST_PROPERTIES);
//    public static final RegistryEntry<Block> T3_BIRCH_BEEHIVE = registerBlock(HIVES, "nest/birch/4", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, properties), () -> WOOD_NEST_PROPERTIES);
//    //endregion
//    //region Brown Mushroom
//    public static final RegistryEntry<Block> BROWN_MUSHROOM_BEE_NEST = registerBlock(HIVES, "nest/brown_mushroom/1", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T1_NEST, properties), () -> makeNestProperty(MapColor.DIRT, SoundType.WOOD));
//    public static final RegistryEntry<Block> T1_BROWN_MUSHROOM_BEEHIVE = registerBlock(HIVES, "nest/brown_mushroom/2", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, properties), () -> makeNestProperty(MapColor.DIRT, SoundType.WOOD));
//    public static final RegistryEntry<Block> T2_BROWN_MUSHROOM_BEEHIVE = registerBlock(HIVES, "nest/brown_mushroom/3", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, properties), () -> makeNestProperty(MapColor.DIRT, SoundType.WOOD));
//    public static final RegistryEntry<Block> T3_BROWN_MUSHROOM_BEEHIVE = registerBlock(HIVES, "nest/brown_mushroom/4", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, properties), () -> makeNestProperty(MapColor.DIRT, SoundType.WOOD));
//    //endregion
//    //region Crimson
//    public static final RegistryEntry<Block> CRIMSON_BEE_NEST = registerBlock(HIVES, "nest/crimson/1", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T1_NEST, properties), () -> makeNestProperty(MapColor.CRIMSON_STEM, SoundType.STEM));
//    public static final RegistryEntry<Block> T1_CRIMSON_BEEHIVE = registerBlock(HIVES, "nest/crimson/2", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, properties), () -> makeNestProperty(MapColor.CRIMSON_STEM, SoundType.STEM));
//    public static final RegistryEntry<Block> T2_CRIMSON_BEEHIVE = registerBlock(HIVES, "nest/crimson/3", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, properties), () -> makeNestProperty(MapColor.CRIMSON_STEM, SoundType.STEM));
//    public static final RegistryEntry<Block> T3_CRIMSON_BEEHIVE = registerBlock(HIVES, "nest/crimson/4", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, properties), () -> makeNestProperty(MapColor.CRIMSON_STEM, SoundType.STEM));
//    //endregion
//    //region Crimson Nylium
//    public static final RegistryEntry<Block> CRIMSON_NYLIUM_BEE_NEST = registerBlock(HIVES, "nest/crimson_nylium/1", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T1_NEST, properties), () -> makeNestProperty(MapColor.CRIMSON_NYLIUM, SoundType.STEM));
//    public static final RegistryEntry<Block> T1_CRIMSON_NYLIUM_BEEHIVE = registerBlock(HIVES, "nest/crimson_nylium/2", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, properties), () -> makeNestProperty(MapColor.CRIMSON_NYLIUM, SoundType.STEM));
//    public static final RegistryEntry<Block> T2_CRIMSON_NYLIUM_BEEHIVE = registerBlock(HIVES, "nest/crimson_nylium/3", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, properties), () -> makeNestProperty(MapColor.CRIMSON_NYLIUM, SoundType.STEM));
//    public static final RegistryEntry<Block> T3_CRIMSON_NYLIUM_BEEHIVE = registerBlock(HIVES, "nest/crimson_nylium/4", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, properties), () -> makeNestProperty(MapColor.CRIMSON_NYLIUM, SoundType.STEM));
//    //endregion
//    //region Dark Oak
//    public static final RegistryEntry<Block> DARK_OAK_BEE_NEST = registerBlock(HIVES, "nest/dark_oak/1", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T1_NEST, properties), () -> WOOD_NEST_PROPERTIES);
//    public static final RegistryEntry<Block> T1_DARK_OAK_BEEHIVE = registerBlock(HIVES, "nest/dark_oak/2", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, properties), () -> WOOD_NEST_PROPERTIES);
//    public static final RegistryEntry<Block> T2_DARK_OAK_BEEHIVE = registerBlock(HIVES, "nest/dark_oak/3", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, properties), () -> WOOD_NEST_PROPERTIES);
//    public static final RegistryEntry<Block> T3_DARK_OAK_BEEHIVE = registerBlock(HIVES, "nest/dark_oak/4", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, properties), () -> WOOD_NEST_PROPERTIES);
//    //endregion
//    //region Grass
//    public static final RegistryEntry<Block> GRASS_BEE_NEST = registerBlock(HIVES, "nest/grass/1", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T1_NEST, properties), () -> makeNestProperty(MapColor.GRASS, SoundType.GRASS));
//    public static final RegistryEntry<Block> T1_GRASS_BEEHIVE = registerBlock(HIVES, "nest/grass/2", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, properties), () -> makeNestProperty(MapColor.GRASS, SoundType.GRASS));
//    public static final RegistryEntry<Block> T2_GRASS_BEEHIVE = registerBlock(HIVES, "nest/grass/3", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, properties), () -> makeNestProperty(MapColor.GRASS, SoundType.GRASS));
//    public static final RegistryEntry<Block> T3_GRASS_BEEHIVE = registerBlock(HIVES, "nest/grass/4", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, properties), () -> makeNestProperty(MapColor.GRASS, SoundType.GRASS));
//    //endregion
//    //region Jungle
//    public static final RegistryEntry<Block> JUNGLE_BEE_NEST = registerBlock(HIVES, "nest/jungle/1", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T1_NEST, properties), () -> WOOD_NEST_PROPERTIES);
//    public static final RegistryEntry<Block> T1_JUNGLE_BEEHIVE = registerBlock(HIVES, "nest/jungle/2", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, properties), () -> WOOD_NEST_PROPERTIES);
//    public static final RegistryEntry<Block> T2_JUNGLE_BEEHIVE = registerBlock(HIVES, "nest/jungle/3", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, properties), () -> WOOD_NEST_PROPERTIES);
//    public static final RegistryEntry<Block> T3_JUNGLE_BEEHIVE = registerBlock(HIVES, "nest/jungle/4", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, properties), () -> WOOD_NEST_PROPERTIES);
//    //endregion
//    //region Nether
//    public static final RegistryEntry<Block> NETHER_BEE_NEST = registerBlock(HIVES, "nest/netherrack/1", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T1_NEST, properties), () -> makeNestProperty(MapColor.NETHER, SoundType.NETHERRACK));
//    public static final RegistryEntry<Block> T1_NETHER_BEEHIVE = registerBlock(HIVES, "nest/netherrack/2", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, properties), () -> makeNestProperty(MapColor.NETHER, SoundType.NETHERRACK));
//    public static final RegistryEntry<Block> T2_NETHER_BEEHIVE = registerBlock(HIVES, "nest/netherrack/3", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, properties), () -> makeNestProperty(MapColor.NETHER, SoundType.NETHERRACK));
//    public static final RegistryEntry<Block> T3_NETHER_BEEHIVE = registerBlock(HIVES, "nest/netherrack/4", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, properties), () -> makeNestProperty(MapColor.NETHER, SoundType.NETHERRACK));
//    //endregion
//    //region Oak
//    public static final RegistryEntry<Block> OAK_BEE_NEST = registerBlock(HIVES, "nest/oak/1", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T1_NEST, properties), () -> WOOD_NEST_PROPERTIES);
//    public static final RegistryEntry<Block> T1_OAK_BEEHIVE = registerBlock(HIVES, "nest/oak/2", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, properties), () -> WOOD_NEST_PROPERTIES);
//    public static final RegistryEntry<Block> T2_OAK_BEEHIVE = registerBlock(HIVES, "nest/oak/3", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, properties), () -> WOOD_NEST_PROPERTIES);
//    public static final RegistryEntry<Block> T3_OAK_BEEHIVE = registerBlock(HIVES, "nest/oak/4", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, properties), () -> WOOD_NEST_PROPERTIES);
//    //endregion
//    //region Prismarine
//    public static final RegistryEntry<Block> PRISMARINE_BEE_NEST = registerBlock(HIVES, "nest/prismarine/1", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T1_NEST, properties), () -> makeNestProperty(MapColor.DIAMOND, SoundType.STONE));
//    public static final RegistryEntry<Block> T1_PRISMARINE_BEEHIVE = registerBlock(HIVES, "nest/prismarine/2", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, properties), () -> makeNestProperty(MapColor.DIAMOND, SoundType.STONE));
//    public static final RegistryEntry<Block> T2_PRISMARINE_BEEHIVE = registerBlock(HIVES, "nest/prismarine/3", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, properties), () -> makeNestProperty(MapColor.DIAMOND, SoundType.STONE));
//    public static final RegistryEntry<Block> T3_PRISMARINE_BEEHIVE = registerBlock(HIVES, "nest/prismarine/4", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, properties), () -> makeNestProperty(MapColor.DIAMOND, SoundType.STONE));
//    //endregion
//    //region Purpur
//    public static final RegistryEntry<Block> PURPUR_BEE_NEST = registerBlock(HIVES, "nest/chorus/1", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T1_NEST, properties), () -> makeNestProperty(MapColor.COLOR_MAGENTA, SoundType.STONE));
//    public static final RegistryEntry<Block> T1_PURPUR_BEEHIVE = registerBlock(HIVES, "nest/chorus/2", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, properties), () -> makeNestProperty(MapColor.COLOR_MAGENTA, SoundType.STONE));
//    public static final RegistryEntry<Block> T2_PURPUR_BEEHIVE = registerBlock(HIVES, "nest/chorus/3", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, properties), () -> makeNestProperty(MapColor.COLOR_MAGENTA, SoundType.STONE));
//    public static final RegistryEntry<Block> T3_PURPUR_BEEHIVE = registerBlock(HIVES, "nest/chorus/4", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, properties), () -> makeNestProperty(MapColor.COLOR_MAGENTA, SoundType.STONE));
//    //endregion
//    //region Red Mushroom
//    public static final RegistryEntry<Block> RED_MUSHROOM_BEE_NEST = registerBlock(HIVES, "nest/red_mushroom/1", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T1_NEST, properties), () -> makeNestProperty(MapColor.COLOR_RED, SoundType.WOOD));
//    public static final RegistryEntry<Block> T1_RED_MUSHROOM_BEEHIVE = registerBlock(HIVES, "nest/red_mushroom/2", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, properties), () -> makeNestProperty(MapColor.COLOR_RED, SoundType.WOOD));
//    public static final RegistryEntry<Block> T2_RED_MUSHROOM_BEEHIVE = registerBlock(HIVES, "nest/red_mushroom/3", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, properties), () -> makeNestProperty(MapColor.COLOR_RED, SoundType.WOOD));
//    public static final RegistryEntry<Block> T3_RED_MUSHROOM_BEEHIVE = registerBlock(HIVES, "nest/red_mushroom/4", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, properties), () -> makeNestProperty(MapColor.COLOR_RED, SoundType.WOOD));
//    //endregion
//    //region Spruce
//    public static final RegistryEntry<Block> SPRUCE_BEE_NEST = registerBlock(HIVES, "nest/spruce/1", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T1_NEST, properties), () -> WOOD_NEST_PROPERTIES);
//    public static final RegistryEntry<Block> T1_SPRUCE_BEEHIVE = registerBlock(HIVES, "nest/spruce/2", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, properties), () -> WOOD_NEST_PROPERTIES);
//    public static final RegistryEntry<Block> T2_SPRUCE_BEEHIVE = registerBlock(HIVES, "nest/spruce/3", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, properties), () -> WOOD_NEST_PROPERTIES);
//    public static final RegistryEntry<Block> T3_SPRUCE_BEEHIVE = registerBlock(HIVES, "nest/spruce/4", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, properties), () -> WOOD_NEST_PROPERTIES);
//    //endregion
//    //region Warped
//    public static final RegistryEntry<Block> WARPED_BEE_NEST = registerBlock(HIVES, "nest/warped/1", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T1_NEST, properties), () -> makeNestProperty(MapColor.WARPED_STEM, SoundType.STEM));
//    public static final RegistryEntry<Block> T1_WARPED_BEEHIVE = registerBlock(HIVES, "nest/warped/2", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, properties), () -> makeNestProperty(MapColor.WARPED_STEM, SoundType.STEM));
//    public static final RegistryEntry<Block> T2_WARPED_BEEHIVE = registerBlock(HIVES, "nest/warped/3", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, properties), () -> makeNestProperty(MapColor.WARPED_STEM, SoundType.STEM));
//    public static final RegistryEntry<Block> T3_WARPED_BEEHIVE = registerBlock(HIVES, "nest/warped/4", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, properties), () -> makeNestProperty(MapColor.WARPED_STEM, SoundType.STEM));
//    //endregion
//    //region Warped Nylium
//    public static final RegistryEntry<Block> WARPED_NYLIUM_BEE_NEST = registerBlock(HIVES, "nest/warped_nylium/1", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T1_NEST, properties), () -> makeNestProperty(MapColor.WARPED_NYLIUM, SoundType.STEM));
//    public static final RegistryEntry<Block> T1_WARPED_NYLIUM_BEEHIVE = registerBlock(HIVES, "nest/warped_nylium/2", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, properties), () -> makeNestProperty(MapColor.WARPED_NYLIUM, SoundType.STEM));
//    public static final RegistryEntry<Block> T2_WARPED_NYLIUM_BEEHIVE = registerBlock(HIVES, "nest/warped_nylium/3", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, properties), () -> makeNestProperty(MapColor.WARPED_NYLIUM, SoundType.STEM));
//    public static final RegistryEntry<Block> T3_WARPED_NYLIUM_BEEHIVE = registerBlock(HIVES, "nest/warped_nylium/4", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, properties), () -> makeNestProperty(MapColor.WARPED_NYLIUM, SoundType.STEM));
//    //endregion
//    //region Wither
//    public static final RegistryEntry<Block> WITHER_BEE_NEST = registerBlock(HIVES, "nest/wither/1", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T1_NEST, properties), () -> makeNestProperty(MapColor.COLOR_BLACK, SoundType.BASALT));
//    public static final RegistryEntry<Block> T1_WITHER_BEEHIVE = registerBlock(HIVES, "nest/wither/2", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, properties), () -> makeNestProperty(MapColor.COLOR_BLACK, SoundType.BASALT));
//    public static final RegistryEntry<Block> T2_WITHER_BEEHIVE = registerBlock(HIVES, "nest/wither/3", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, properties), () -> makeNestProperty(MapColor.COLOR_BLACK, SoundType.BASALT));
//    public static final RegistryEntry<Block> T3_WITHER_BEEHIVE = registerBlock(HIVES, "nest/wither/4", properties -> createNest(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, properties), () -> makeNestProperty(MapColor.COLOR_BLACK, SoundType.BASALT));
//    //endregion
//    //endregion

    public static final RegistryEntry<Block> T1_APIARY_BLOCK = registerBlock(APIARIES, "apiary/1", properties -> new ApiaryBlock(DefaultApiaryTiers.T1_APIARY, properties), () -> APIARY_PROPERTIES);
    public static final RegistryEntry<Block> T2_APIARY_BLOCK = registerBlock(APIARIES, "apiary/2", properties -> new ApiaryBlock(DefaultApiaryTiers.T2_APIARY, properties), () -> APIARY_PROPERTIES);
    public static final RegistryEntry<Block> T3_APIARY_BLOCK = registerBlock(APIARIES, "apiary/3", properties -> new ApiaryBlock(DefaultApiaryTiers.T3_APIARY, properties), () -> APIARY_PROPERTIES);
    public static final RegistryEntry<Block> T4_APIARY_BLOCK = registerBlock(APIARIES, "apiary/4", properties -> new ApiaryBlock(DefaultApiaryTiers.T4_APIARY, properties), () -> APIARY_PROPERTIES);
 //   public static final RegistryEntry<Block> FLOW_HIVE = BLOCKS.register("flow_hive", FlowHiveBlock::new);

    public static final RegistryEntry<Block> WAX_BLOCK = registerBlock(BLOCKS, "wax_block", Block::new, () -> BlockBehaviour.Properties.of().sound(SoundType.SNOW).strength(0.3F));

    public static final RegistryEntry<Block> BEE_BOX = registerBlock(BLOCKS, "bee_box", BeeBoxBlock::new, () -> BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(0.5f));
    public static final RegistryEntry<Block> BEE_BOX_TEMP = registerBlock(BLOCKS, "bee_box_temp", BeeBoxBlock::new, () -> BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(0.5f));

    public static final RegistryEntry<Block> HONEY_GLASS_PLAYER = registerBlock(BLOCKS, "honey_glass_player", properties -> new HoneyGlass(properties, false), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).isSuffocating(ModBlocks::never).isViewBlocking(ModBlocks::never).noCollision());
    public static final RegistryEntry<Block> HONEY_GLASS = registerBlock(BLOCKS, "honey_glass", properties -> new HoneyGlass(properties, true), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).isSuffocating(ModBlocks::never).isViewBlocking(ModBlocks::never).noCollision());

    public static final RegistryEntry<Block> WAXED_PLANKS = registerBlock(BLOCKS, "waxed_planks", Block::new, () -> WAXED_PLANKS_PROPERTIES);
    public static final RegistryEntry<Block> WAXED_STAIRS = registerBlock(BLOCKS, "waxed_stairs", properties -> new StairBlock(WAXED_PLANKS.get().defaultBlockState(), properties), WAXED_PLANKS_PROPERTIES::dynamicShape);
    public static final RegistryEntry<Block> WAXED_SLAB = registerBlock(BLOCKS, "waxed_slab", SlabBlock::new, WAXED_PLANKS_PROPERTIES::dynamicShape);
    public static final RegistryEntry<Block> WAXED_FENCE = registerBlock(BLOCKS, "waxed_fence", FenceBlock::new, WAXED_PLANKS_PROPERTIES::noOcclusion);
    public static final RegistryEntry<Block> WAXED_FENCE_GATE = registerBlock(BLOCKS, "waxed_fence_gate", properties -> new FenceGateBlock(WAXED_WOOD_TYPE, properties), WAXED_PLANKS_PROPERTIES::noOcclusion);
    public static final RegistryEntry<Block> WAXED_BUTTON = registerBlock(BLOCKS, "waxed_button", properties -> new ButtonBlock(WAX_BLOCK_SET, 30, properties), () -> WAXED_PLANKS_PROPERTIES.noOcclusion().noCollision());
    public static final RegistryEntry<Block> WAXED_PRESSURE_PLATE = registerBlock(BLOCKS, "waxed_pressure_plate", properties -> new PressurePlateBlock( WAX_BLOCK_SET, properties), () -> WAXED_PLANKS_PROPERTIES.noOcclusion().noCollision());
    public static final RegistryEntry<Block> WAXED_DOOR = registerBlock(BLOCKS, "waxed_door", properties -> new DoorBlock(WAX_BLOCK_SET, properties), WAXED_PLANKS_PROPERTIES::noOcclusion);
    public static final RegistryEntry<Block> WAXED_TRAPDOOR = registerBlock(BLOCKS, "waxed_trapdoor", properties -> new TrapDoorBlock(WAX_BLOCK_SET, properties), WAXED_PLANKS_PROPERTIES::noOcclusion);
    public static final RegistryEntry<Block> TRIMMED_WAXED_PLANKS = registerBlock(BLOCKS, "trimmed_waxed_planks", Block::new, () -> WAXED_PLANKS_PROPERTIES);
    public static final RegistryEntry<Block> WAXED_MACHINE_BLOCK = registerBlock(BLOCKS, "waxed_machine_block", Block::new, () -> BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD));

    public static final RegistryEntry<Block> WAXED_SIGN = registerBlock(BLOCKS, "waxed_sign", properties -> new StandingSignBlock(WAXED_WOOD_TYPE, properties){
        @Override
        public @NonNull BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
            return ModBlockEntityTypes.WAXED_SIGN_ENTITY.get().create(pos, state);
        }
    }, () -> BlockBehaviour.Properties.ofFullCopy(WAXED_PLANKS.get()).noOcclusion().noCollision());

    public static final RegistryEntry<Block> WAXED_WALL_SIGN = registerBlock(BLOCKS, "waxed_wall_sign", properties -> new WallSignBlock(WAXED_WOOD_TYPE, properties) {
        @Override
        public @NonNull BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
            return ModBlockEntityTypes.WAXED_SIGN_ENTITY.get().create(pos, state);
        }
    }, () -> BlockBehaviour.Properties.ofFullCopy(WAXED_PLANKS.get()).noOcclusion().noCollision());

    public static final RegistryEntry<Block> WAXED_HANGING_SIGN = registerBlock(BLOCKS, "waxed_hanging_sign", properties -> new CeilingHangingSignBlock(WAXED_WOOD_TYPE, properties) {
        @Override
        public @NonNull BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
            return ModBlockEntityTypes.WAXED_HANGING_SIGN_ENTITY.get().create(pos, state);
        }
    }, () -> BlockBehaviour.Properties.ofFullCopy(WAXED_PLANKS.get()).noOcclusion().noCollision());

    public static final RegistryEntry<Block> WAXED_WALL_HANGING_SIGN = registerBlock(BLOCKS, "waxed_wall_hanging_sign", properties -> new WallHangingSignBlock(WAXED_WOOD_TYPE, properties) {
        @Override
        public @NonNull BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
            return ModBlockEntityTypes.WAXED_HANGING_SIGN_ENTITY.get().create(pos, state);
        }
    }, () -> BlockBehaviour.Properties.ofFullCopy(WAXED_PLANKS.get()).noOcclusion().noCollision());

//    public static final RegistryEntry<Block> ACCELERATOR = BLOCKS.register("accelerator", () -> new TickingBlock<>(ModBlockEntityTypes.ACCELERATOR_TILE_ENTITY, CENTRIFUGE_PROPERTIES));

//    public static final RegistryEntry<Block> POLLEN_SPREADER_FAN = BLOCKS.register("pollen_spreader_fan", () -> new PollenSpreader.Fan(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.5f).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion()));
//    public static final RegistryEntry<Block> POLLEN_SPREADER = BLOCKS.register("pollen_spreader", () -> new PollenSpreader(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.5f).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion()));

//    public static final RegistryEntry<Block> FAKE_FLOWER = BLOCKS.register("fake_flower", () -> new FakeFlowerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).strength(2.0f, 3.0f).sound(SoundType.WOOD).noOcclusion().lightLevel(value -> 1)));

    public static final RegistryEntry<Block> GOLD_FLOWER = registerBlock(BLOCKS, "gold_flower", properties -> new FlowerBlock(MobEffects.INVISIBILITY, 10, properties), () -> BlockBehaviour.Properties.of().noCollision().strength(0).sound(SoundType.GRASS));

    public static final RegistryEntry<Block> BEEHOUSE_TOP = registerBlock(BLOCKS, "beehouse_top", BeeHouseTopBlock::new, () -> BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(5f, 6f).pushReaction(PushReaction.BLOCK));
//    public static final RegistryEntry<Block> BREEDER_BLOCK = BLOCKS.register("breeder", () -> new BreederBlock(BlockBehaviour.Properties.of().strength(1F).sound(SoundType.WOOD)));
//    public static final RegistryEntry<Block> CREATIVE_GEN = BLOCKS.register("creative_gen", () -> new TickingBlock<>(ModBlockEntityTypes.CREATIVE_GEN_ENTITY, CENTRIFUGE_PROPERTIES) {
//        @Override
//        protected MapCodec<? extends BaseEntityBlock> codec() {
//            return null;
//        }
//    });
//    public static final RegistryEntry<Block> ENDER_BEECON = BLOCKS.register("ender_beecon", EnderBeeconBlock::new);
//    public static final RegistryEntry<Block> HONEY_POT = BLOCKS.register("honey_pot", () -> new HoneyPotBlock(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(1.5f).requiresCorrectToolForDrops()));
//    public static final RegistryEntry<Block> SOLIDIFICATION_CHAMBER = BLOCKS.register("solidification_chamber", () -> new SolidificationChamberBlock(BlockBehaviour.Properties.of().sound(SoundType.GLASS).strength(1.5f).requiresCorrectToolForDrops()));
//    public static final RegistryEntry<Block> BASIC_CENTRIFUGE = CENTRIFUGE_BLOCKS.register("centrifuge", () -> new CentrifugeBlock(BlockBehaviour.Properties.of().strength(2).sound(SoundType.METAL).noOcclusion()));
//   public static final RegistryEntry<Block> CENTRIFUGE_CRANK = CENTRIFUGE_BLOCKS.register("centrifuge_crank", () -> new CentrifugeCrankBlock(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD).noOcclusion()));
//  public static final RegistryEntry<Block> HONEY_GENERATOR = BLOCKS.register("honey_generator", () -> new HoneyGenerator(CENTRIFUGE_PROPERTIES));

//    public static final RegistryEntry<Block> HONEY_FLUID_BLOCK = HONEY_FLUID_BLOCKS.register("honey", () -> new BotariumLiquidBlock(ModFluidProperties.HONEY, HONEY_FLUID_BLOCK_PROPERTIES));

}
