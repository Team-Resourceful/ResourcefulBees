package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.block.*;
import com.teamresourceful.resourcefulbees.common.block.base.BeeHouseTopBlock;
import com.teamresourceful.resourcefulbees.common.block.base.TickingBlock;
import com.teamresourceful.resourcefulbees.common.block.centrifuge.CentrifugeBlock;
import com.teamresourceful.resourcefulbees.common.block.centrifuge.CentrifugeCrankBlock;
import com.teamresourceful.resourcefulbees.common.blockentity.TieredBeehiveBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModMaterials;
import com.teamresourceful.resourcefulbees.common.lib.defaults.DefaultApiaryTiers;
import com.teamresourceful.resourcefulbees.common.lib.defaults.DefaultBeehiveTiers;
import com.teamresourceful.resourcefulbees.api.tiers.BeehiveTier;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks.*;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.RegistryEntry;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.ResourcefulRegistries;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.ResourcefulRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class ModBlocks {

    private ModBlocks() {
        throw new UtilityClassError();
    }

    public static final ResourcefulRegistry<Block> BLOCKS = ResourcefulRegistries.create(Registry.BLOCK, ResourcefulBees.MOD_ID);
    public static final ResourcefulRegistry<Block> HIVES = ResourcefulRegistries.create(BLOCKS);
    public static final ResourcefulRegistry<Block> APIARIES = ResourcefulRegistries.create(BLOCKS);
    public static final ResourcefulRegistry<Block> HONEYCOMB_BLOCKS = ResourcefulRegistries.create(BLOCKS);
    public static final ResourcefulRegistry<Block> HONEY_BLOCKS = ResourcefulRegistries.create(BLOCKS);
    public static final ResourcefulRegistry<Block> HONEY_FLUID_BLOCKS = ResourcefulRegistries.create(BLOCKS);
    public static final ResourcefulRegistry<Block> CENTRIFUGE_BLOCKS = ResourcefulRegistries.create(BLOCKS);

    public static final BlockBehaviour.Properties HONEY_FLUID_BLOCK_PROPERTIES = BlockBehaviour.Properties.of(ModMaterials.MATERIAL_HONEY).noCollission().strength(100.0F).noLootTable();
    public static final BlockBehaviour.Properties CENTRIFUGE_PROPERTIES = BlockBehaviour.Properties.of(Material.METAL).strength(2).sound(SoundType.METAL);
    private static final BlockBehaviour.Properties NEST_PROPERTIES = BlockBehaviour.Properties.of(Material.WOOD).strength(1F).sound(SoundType.WOOD);

    public static final BlockBehaviour.Properties WAXED_PLANKS_PROPERTIES = BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD);
    public static final WoodType WAXED_WOOD_TYPE = WoodType.register(WoodType.create(new ResourceLocation(ResourcefulBees.MOD_ID, "waxed").toString()));

    private static BlockBehaviour.Properties makeNestProperty(Material material, MaterialColor color, SoundType soundType){
        return BlockBehaviour.Properties.of(material, color).strength(1.0F).sound(soundType);
    }

    private static Supplier<TieredBeehiveBlock> createWoodNest(Supplier<BlockEntityType<TieredBeehiveBlockEntity>> entityType, BeehiveTier tier) {
        return () -> new TieredBeehiveBlock(entityType, tier, NEST_PROPERTIES);
    }

    private static Supplier<TieredBeehiveBlock> createNest(Supplier<BlockEntityType<TieredBeehiveBlockEntity>> entityType, BeehiveTier tier, Material material, MaterialColor materialColor, SoundType soundType) {
        return () -> new TieredBeehiveBlock(entityType, tier, makeNestProperty(material, materialColor, soundType));
    }

    public static final RegistryEntry<Block> WAX_BLOCK = BLOCKS.register("wax_block", () -> new Block(BlockBehaviour.Properties.of(Material.CLAY).sound(SoundType.SNOW).strength(0.3F)));
    public static final RegistryEntry<Block> GOLD_FLOWER = BLOCKS.register("gold_flower", () -> new FlowerBlock(MobEffects.INVISIBILITY, 10, BlockBehaviour.Properties.of(Material.PLANT).noCollission().strength(0).sound(SoundType.GRASS)));

    //TODO figure out how to make this nest registration cleaner and reduce duplicate processes
    //region Nests
    //region Acacia
    public static final RegistryEntry<TieredBeehiveBlock> ACACIA_BEE_NEST = HIVES.register("nest/acacia/1", createWoodNest(ModBlockEntityTypes.ACACIA_BEE_NEST_ENTITY, DefaultBeehiveTiers.T1_NEST));
    public static final RegistryEntry<TieredBeehiveBlock> T1_ACACIA_BEEHIVE = HIVES.register("nest/acacia/2", createWoodNest(ModBlockEntityTypes.T1_ACACIA_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST));
    public static final RegistryEntry<TieredBeehiveBlock> T2_ACACIA_BEEHIVE = HIVES.register("nest/acacia/3", createWoodNest(ModBlockEntityTypes.T2_ACACIA_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST));
    public static final RegistryEntry<TieredBeehiveBlock> T3_ACACIA_BEEHIVE = HIVES.register("nest/acacia/4", createWoodNest(ModBlockEntityTypes.T3_ACACIA_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST));
    //endregion
    //region Birch
    public static final RegistryEntry<TieredBeehiveBlock> BIRCH_BEE_NEST = HIVES.register("nest/birch/1", createWoodNest(ModBlockEntityTypes.BIRCH_BEE_NEST_ENTITY, DefaultBeehiveTiers.T1_NEST));
    public static final RegistryEntry<TieredBeehiveBlock> T1_BIRCH_BEEHIVE = HIVES.register("nest/birch/2", createWoodNest(ModBlockEntityTypes.T1_BIRCH_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST));
    public static final RegistryEntry<TieredBeehiveBlock> T2_BIRCH_BEEHIVE = HIVES.register("nest/birch/3", createWoodNest(ModBlockEntityTypes.T2_BIRCH_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST));
    public static final RegistryEntry<TieredBeehiveBlock> T3_BIRCH_BEEHIVE = HIVES.register("nest/birch/4", createWoodNest(ModBlockEntityTypes.T3_BIRCH_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST));
    //endregion
    //region Brown Mushroom
    public static final RegistryEntry<TieredBeehiveBlock> BROWN_MUSHROOM_BEE_NEST = HIVES.register("nest/brown_mushroom/1", createNest(ModBlockEntityTypes.BROWN_MUSHROOM_BEE_NEST_ENTITY, DefaultBeehiveTiers.T1_NEST, Material.WOOD, MaterialColor.DIRT, SoundType.WOOD));
    public static final RegistryEntry<TieredBeehiveBlock> T1_BROWN_MUSHROOM_BEEHIVE = HIVES.register("nest/brown_mushroom/2", createNest(ModBlockEntityTypes.T1_BROWN_MUSHROOM_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, Material.WOOD, MaterialColor.DIRT, SoundType.WOOD));
    public static final RegistryEntry<TieredBeehiveBlock> T2_BROWN_MUSHROOM_BEEHIVE = HIVES.register("nest/brown_mushroom/3", createNest(ModBlockEntityTypes.T2_BROWN_MUSHROOM_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, Material.WOOD, MaterialColor.DIRT, SoundType.WOOD));
    public static final RegistryEntry<TieredBeehiveBlock> T3_BROWN_MUSHROOM_BEEHIVE = HIVES.register("nest/brown_mushroom/4", createNest(ModBlockEntityTypes.T3_BROWN_MUSHROOM_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, Material.WOOD, MaterialColor.DIRT, SoundType.WOOD));
    //endregion
    //region Crimson
    public static final RegistryEntry<TieredBeehiveBlock> CRIMSON_BEE_NEST = HIVES.register("nest/crimson/1", createNest(ModBlockEntityTypes.CRIMSON_BEE_NEST_ENTITY, DefaultBeehiveTiers.T1_NEST, Material.NETHER_WOOD, MaterialColor.CRIMSON_STEM, SoundType.STEM));
    public static final RegistryEntry<TieredBeehiveBlock> T1_CRIMSON_BEEHIVE = HIVES.register("nest/crimson/2", createNest(ModBlockEntityTypes.T1_CRIMSON_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, Material.NETHER_WOOD, MaterialColor.CRIMSON_STEM, SoundType.STEM));
    public static final RegistryEntry<TieredBeehiveBlock> T2_CRIMSON_BEEHIVE = HIVES.register("nest/crimson/3", createNest(ModBlockEntityTypes.T2_CRIMSON_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, Material.NETHER_WOOD, MaterialColor.CRIMSON_STEM, SoundType.STEM));
    public static final RegistryEntry<TieredBeehiveBlock> T3_CRIMSON_BEEHIVE = HIVES.register("nest/crimson/4", createNest(ModBlockEntityTypes.T3_CRIMSON_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, Material.NETHER_WOOD, MaterialColor.CRIMSON_STEM, SoundType.STEM));
    //endregion
    //region Crimson Nylium
    public static final RegistryEntry<TieredBeehiveBlock> CRIMSON_NYLIUM_BEE_NEST = HIVES.register("nest/crimson_nylium/1", createNest(ModBlockEntityTypes.CRIMSON_NYLIUM_BEE_NEST_ENTITY, DefaultBeehiveTiers.T1_NEST, Material.NETHER_WOOD, MaterialColor.CRIMSON_NYLIUM, SoundType.STEM));
    public static final RegistryEntry<TieredBeehiveBlock> T1_CRIMSON_NYLIUM_BEEHIVE = HIVES.register("nest/crimson_nylium/2", createNest(ModBlockEntityTypes.T1_CRIMSON_NYLIUM_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, Material.NETHER_WOOD, MaterialColor.CRIMSON_NYLIUM, SoundType.STEM));
    public static final RegistryEntry<TieredBeehiveBlock> T2_CRIMSON_NYLIUM_BEEHIVE = HIVES.register("nest/crimson_nylium/3", createNest(ModBlockEntityTypes.T2_CRIMSON_NYLIUM_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, Material.NETHER_WOOD, MaterialColor.CRIMSON_NYLIUM, SoundType.STEM));
    public static final RegistryEntry<TieredBeehiveBlock> T3_CRIMSON_NYLIUM_BEEHIVE = HIVES.register("nest/crimson_nylium/4", createNest(ModBlockEntityTypes.T3_CRIMSON_NYLIUM_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, Material.NETHER_WOOD, MaterialColor.CRIMSON_NYLIUM, SoundType.STEM));
    //endregion
    //region Dark Oak
    public static final RegistryEntry<TieredBeehiveBlock> DARK_OAK_BEE_NEST = HIVES.register("nest/dark_oak/1", createWoodNest(ModBlockEntityTypes.DARK_OAK_BEE_NEST_ENTITY, DefaultBeehiveTiers.T1_NEST));
    public static final RegistryEntry<TieredBeehiveBlock> T1_DARK_OAK_BEEHIVE = HIVES.register("nest/dark_oak/2", createWoodNest(ModBlockEntityTypes.T1_DARK_OAK_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST));
    public static final RegistryEntry<TieredBeehiveBlock> T2_DARK_OAK_BEEHIVE = HIVES.register("nest/dark_oak/3", createWoodNest(ModBlockEntityTypes.T2_DARK_OAK_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST));
    public static final RegistryEntry<TieredBeehiveBlock> T3_DARK_OAK_BEEHIVE = HIVES.register("nest/dark_oak/4", createWoodNest(ModBlockEntityTypes.T3_DARK_OAK_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST));
    //endregion
    //region Grass
    public static final RegistryEntry<TieredBeehiveBlock> GRASS_BEE_NEST = HIVES.register("nest/grass/1", createNest(ModBlockEntityTypes.GRASS_BEE_NEST_ENTITY, DefaultBeehiveTiers.T1_NEST, Material.GRASS, MaterialColor.GRASS, SoundType.GRASS));
    public static final RegistryEntry<TieredBeehiveBlock> T1_GRASS_BEEHIVE = HIVES.register("nest/grass/2", createNest(ModBlockEntityTypes.T1_GRASS_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, Material.GRASS, MaterialColor.GRASS, SoundType.GRASS));
    public static final RegistryEntry<TieredBeehiveBlock> T2_GRASS_BEEHIVE = HIVES.register("nest/grass/3", createNest(ModBlockEntityTypes.T2_GRASS_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, Material.GRASS, MaterialColor.GRASS, SoundType.GRASS));
    public static final RegistryEntry<TieredBeehiveBlock> T3_GRASS_BEEHIVE = HIVES.register("nest/grass/4", createNest(ModBlockEntityTypes.T3_GRASS_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, Material.GRASS, MaterialColor.GRASS, SoundType.GRASS));
    //endregion
    //region Jungle
    public static final RegistryEntry<TieredBeehiveBlock> JUNGLE_BEE_NEST = HIVES.register("nest/jungle/1", createWoodNest(ModBlockEntityTypes.JUNGLE_BEE_NEST_ENTITY, DefaultBeehiveTiers.T1_NEST));
    public static final RegistryEntry<TieredBeehiveBlock> T1_JUNGLE_BEEHIVE = HIVES.register("nest/jungle/2", createWoodNest(ModBlockEntityTypes.T1_JUNGLE_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST));
    public static final RegistryEntry<TieredBeehiveBlock> T2_JUNGLE_BEEHIVE = HIVES.register("nest/jungle/3", createWoodNest(ModBlockEntityTypes.T2_JUNGLE_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST));
    public static final RegistryEntry<TieredBeehiveBlock> T3_JUNGLE_BEEHIVE = HIVES.register("nest/jungle/4", createWoodNest(ModBlockEntityTypes.T3_JUNGLE_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST));
    //endregion
    //region Nether
    public static final RegistryEntry<TieredBeehiveBlock> NETHER_BEE_NEST = HIVES.register("nest/netherrack/1", createNest(ModBlockEntityTypes.NETHER_BEE_NEST_ENTITY, DefaultBeehiveTiers.T1_NEST, Material.STONE, MaterialColor.NETHER, SoundType.NETHERRACK));
    public static final RegistryEntry<TieredBeehiveBlock> T1_NETHER_BEEHIVE = HIVES.register("nest/netherrack/2", createNest(ModBlockEntityTypes.T1_NETHER_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, Material.STONE, MaterialColor.NETHER, SoundType.NETHERRACK));
    public static final RegistryEntry<TieredBeehiveBlock> T2_NETHER_BEEHIVE = HIVES.register("nest/netherrack/3", createNest(ModBlockEntityTypes.T2_NETHER_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, Material.STONE, MaterialColor.NETHER, SoundType.NETHERRACK));
    public static final RegistryEntry<TieredBeehiveBlock> T3_NETHER_BEEHIVE = HIVES.register("nest/netherrack/4", createNest(ModBlockEntityTypes.T3_NETHER_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, Material.STONE, MaterialColor.NETHER, SoundType.NETHERRACK));
    //endregion
    //region Oak
    public static final RegistryEntry<TieredBeehiveBlock> OAK_BEE_NEST = HIVES.register("nest/oak/1", createWoodNest(ModBlockEntityTypes.OAK_BEE_NEST_ENTITY, DefaultBeehiveTiers.T1_NEST));
    public static final RegistryEntry<TieredBeehiveBlock> T1_OAK_BEEHIVE = HIVES.register("nest/oak/2", createWoodNest(ModBlockEntityTypes.T1_OAK_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST));
    public static final RegistryEntry<TieredBeehiveBlock> T2_OAK_BEEHIVE = HIVES.register("nest/oak/3", createWoodNest(ModBlockEntityTypes.T2_OAK_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST));
    public static final RegistryEntry<TieredBeehiveBlock> T3_OAK_BEEHIVE = HIVES.register("nest/oak/4", createWoodNest(ModBlockEntityTypes.T3_OAK_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST));
    //endregion
    //region Prismarine
    public static final RegistryEntry<TieredBeehiveBlock> PRISMARINE_BEE_NEST = HIVES.register("nest/prismarine/1", createNest(ModBlockEntityTypes.PRISMARINE_BEE_NEST_ENTITY, DefaultBeehiveTiers.T1_NEST, Material.STONE, MaterialColor.DIAMOND, SoundType.STONE));
    public static final RegistryEntry<TieredBeehiveBlock> T1_PRISMARINE_BEEHIVE = HIVES.register("nest/prismarine/2", createNest(ModBlockEntityTypes.T1_PRISMARINE_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, Material.STONE, MaterialColor.DIAMOND, SoundType.STONE));
    public static final RegistryEntry<TieredBeehiveBlock> T2_PRISMARINE_BEEHIVE = HIVES.register("nest/prismarine/3", createNest(ModBlockEntityTypes.T2_PRISMARINE_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, Material.STONE, MaterialColor.DIAMOND, SoundType.STONE));
    public static final RegistryEntry<TieredBeehiveBlock> T3_PRISMARINE_BEEHIVE = HIVES.register("nest/prismarine/4", createNest(ModBlockEntityTypes.T3_PRISMARINE_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, Material.STONE, MaterialColor.DIAMOND, SoundType.STONE));
    //endregion
    //region Purpur
    public static final RegistryEntry<TieredBeehiveBlock> PURPUR_BEE_NEST = HIVES.register("nest/chorus/1", createNest(ModBlockEntityTypes.PURPUR_BEE_NEST_ENTITY, DefaultBeehiveTiers.T1_NEST, Material.STONE, MaterialColor.COLOR_MAGENTA, SoundType.STONE));
    public static final RegistryEntry<TieredBeehiveBlock> T1_PURPUR_BEEHIVE = HIVES.register("nest/chorus/2", createNest(ModBlockEntityTypes.T1_PURPUR_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, Material.STONE, MaterialColor.COLOR_MAGENTA, SoundType.STONE));
    public static final RegistryEntry<TieredBeehiveBlock> T2_PURPUR_BEEHIVE = HIVES.register("nest/chorus/3", createNest(ModBlockEntityTypes.T2_PURPUR_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, Material.STONE, MaterialColor.COLOR_MAGENTA, SoundType.STONE));
    public static final RegistryEntry<TieredBeehiveBlock> T3_PURPUR_BEEHIVE = HIVES.register("nest/chorus/4", createNest(ModBlockEntityTypes.T3_PURPUR_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, Material.STONE, MaterialColor.COLOR_MAGENTA, SoundType.STONE));
    //endregion
    //region Red Mushroom
    public static final RegistryEntry<TieredBeehiveBlock> RED_MUSHROOM_BEE_NEST = HIVES.register("nest/red_mushroom/1", createNest(ModBlockEntityTypes.RED_MUSHROOM_BEE_NEST_ENTITY, DefaultBeehiveTiers.T1_NEST, Material.WOOD, MaterialColor.COLOR_RED, SoundType.WOOD));
    public static final RegistryEntry<TieredBeehiveBlock> T1_RED_MUSHROOM_BEEHIVE = HIVES.register("nest/red_mushroom/2", createNest(ModBlockEntityTypes.T1_RED_MUSHROOM_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, Material.WOOD, MaterialColor.COLOR_RED, SoundType.WOOD));
    public static final RegistryEntry<TieredBeehiveBlock> T2_RED_MUSHROOM_BEEHIVE = HIVES.register("nest/red_mushroom/3", createNest(ModBlockEntityTypes.T2_RED_MUSHROOM_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, Material.WOOD, MaterialColor.COLOR_RED, SoundType.WOOD));
    public static final RegistryEntry<TieredBeehiveBlock> T3_RED_MUSHROOM_BEEHIVE = HIVES.register("nest/red_mushroom/4", createNest(ModBlockEntityTypes.T3_RED_MUSHROOM_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, Material.WOOD, MaterialColor.COLOR_RED, SoundType.WOOD));
    //endregion
    //region Spruce
    public static final RegistryEntry<TieredBeehiveBlock> SPRUCE_BEE_NEST = HIVES.register("nest/spruce/1", createWoodNest(ModBlockEntityTypes.SPRUCE_BEE_NEST_ENTITY, DefaultBeehiveTiers.T1_NEST));
    public static final RegistryEntry<TieredBeehiveBlock> T1_SPRUCE_BEEHIVE = HIVES.register("nest/spruce/2", createWoodNest(ModBlockEntityTypes.T1_SPRUCE_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST));
    public static final RegistryEntry<TieredBeehiveBlock> T2_SPRUCE_BEEHIVE = HIVES.register("nest/spruce/3", createWoodNest(ModBlockEntityTypes.T2_SPRUCE_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST));
    public static final RegistryEntry<TieredBeehiveBlock> T3_SPRUCE_BEEHIVE = HIVES.register("nest/spruce/4", createWoodNest(ModBlockEntityTypes.T3_SPRUCE_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST));
    //endregion
    //region Warped
    public static final RegistryEntry<TieredBeehiveBlock> WARPED_BEE_NEST = HIVES.register("nest/warped/1", createNest(ModBlockEntityTypes.WARPED_BEE_NEST_ENTITY, DefaultBeehiveTiers.T1_NEST, Material.NETHER_WOOD, MaterialColor.WARPED_STEM, SoundType.STEM));
    public static final RegistryEntry<TieredBeehiveBlock> T1_WARPED_BEEHIVE = HIVES.register("nest/warped/2", createNest(ModBlockEntityTypes.T1_WARPED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, Material.NETHER_WOOD, MaterialColor.WARPED_STEM, SoundType.STEM));
    public static final RegistryEntry<TieredBeehiveBlock> T2_WARPED_BEEHIVE = HIVES.register("nest/warped/3", createNest(ModBlockEntityTypes.T2_WARPED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, Material.NETHER_WOOD, MaterialColor.WARPED_STEM, SoundType.STEM));
    public static final RegistryEntry<TieredBeehiveBlock> T3_WARPED_BEEHIVE = HIVES.register("nest/warped/4", createNest(ModBlockEntityTypes.T3_WARPED_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, Material.NETHER_WOOD, MaterialColor.WARPED_STEM, SoundType.STEM));
    //endregion
    //region Warped Nylium
    public static final RegistryEntry<TieredBeehiveBlock> WARPED_NYLIUM_BEE_NEST = HIVES.register("nest/warped_nylium/1", createNest(ModBlockEntityTypes.WARPED_NYLIUM_BEE_NEST_ENTITY, DefaultBeehiveTiers.T1_NEST, Material.NETHER_WOOD, MaterialColor.WARPED_NYLIUM, SoundType.STEM));
    public static final RegistryEntry<TieredBeehiveBlock> T1_WARPED_NYLIUM_BEEHIVE = HIVES.register("nest/warped_nylium/2", createNest(ModBlockEntityTypes.T1_WARPED_NYLIUM_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, Material.NETHER_WOOD, MaterialColor.WARPED_NYLIUM, SoundType.STEM));
    public static final RegistryEntry<TieredBeehiveBlock> T2_WARPED_NYLIUM_BEEHIVE = HIVES.register("nest/warped_nylium/3", createNest(ModBlockEntityTypes.T2_WARPED_NYLIUM_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, Material.NETHER_WOOD, MaterialColor.WARPED_NYLIUM, SoundType.STEM));
    public static final RegistryEntry<TieredBeehiveBlock> T3_WARPED_NYLIUM_BEEHIVE = HIVES.register("nest/warped_nylium/4", createNest(ModBlockEntityTypes.T3_WARPED_NYLIUM_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, Material.NETHER_WOOD, MaterialColor.WARPED_NYLIUM, SoundType.STEM));
    //endregion
    //region Wither
    public static final RegistryEntry<TieredBeehiveBlock> WITHER_BEE_NEST = HIVES.register("nest/wither/1", createNest(ModBlockEntityTypes.WITHER_BEE_NEST_ENTITY, DefaultBeehiveTiers.T1_NEST, Material.STONE, MaterialColor.COLOR_BLACK, SoundType.BASALT));
    public static final RegistryEntry<TieredBeehiveBlock> T1_WITHER_BEEHIVE = HIVES.register("nest/wither/2", createNest(ModBlockEntityTypes.T1_WITHER_BEEHIVE_ENTITY, DefaultBeehiveTiers.T2_NEST, Material.STONE, MaterialColor.COLOR_BLACK, SoundType.BASALT));
    public static final RegistryEntry<TieredBeehiveBlock> T2_WITHER_BEEHIVE = HIVES.register("nest/wither/3", createNest(ModBlockEntityTypes.T2_WITHER_BEEHIVE_ENTITY, DefaultBeehiveTiers.T3_NEST, Material.STONE, MaterialColor.COLOR_BLACK, SoundType.BASALT));
    public static final RegistryEntry<TieredBeehiveBlock> T3_WITHER_BEEHIVE = HIVES.register("nest/wither/4", createNest(ModBlockEntityTypes.T3_WITHER_BEEHIVE_ENTITY, DefaultBeehiveTiers.T4_NEST, Material.STONE, MaterialColor.COLOR_BLACK, SoundType.BASALT));
    //endregion
    //endregion

    //region Apiaries
    public static final RegistryEntry<Block> T1_APIARY_BLOCK = APIARIES.register("t1_apiary", () -> new ApiaryBlock(DefaultApiaryTiers.T1_APIARY));
    public static final RegistryEntry<Block> T2_APIARY_BLOCK = APIARIES.register("t2_apiary", () -> new ApiaryBlock(DefaultApiaryTiers.T2_APIARY));
    public static final RegistryEntry<Block> T3_APIARY_BLOCK = APIARIES.register("t3_apiary", () -> new ApiaryBlock(DefaultApiaryTiers.T3_APIARY));
    public static final RegistryEntry<Block> T4_APIARY_BLOCK = APIARIES.register("t4_apiary", () -> new ApiaryBlock(DefaultApiaryTiers.T4_APIARY));
    public static final RegistryEntry<Block> BEEHOUSE_TOP = BLOCKS.register("beehouse_top", BeeHouseTopBlock::new);
    public static final RegistryEntry<Block> FLOW_HIVE = BLOCKS.register("flow_hive", FlowHiveBlock::new);
    public static final RegistryEntry<Block> BREEDER_BLOCK = BLOCKS.register("breeder", () -> new BreederBlock(NEST_PROPERTIES));
    //endregion

    public static final RegistryEntry<LiquidBlock> HONEY_FLUID_BLOCK = HONEY_FLUID_BLOCKS.register("honey", () -> new LiquidBlock(ModFluids.HONEY_STILL, HONEY_FLUID_BLOCK_PROPERTIES));
    public static final RegistryEntry<Block> HONEY_GENERATOR = BLOCKS.register("honey_generator", () -> new HoneyGenerator(CENTRIFUGE_PROPERTIES));
    public static final RegistryEntry<Block> CREATIVE_GEN = BLOCKS.register("creative_gen", () -> new TickingBlock<>(ModBlockEntityTypes.CREATIVE_GEN_ENTITY, CENTRIFUGE_PROPERTIES));
    public static final RegistryEntry<Block> ACCELERATOR = BLOCKS.register("accelerator", () -> new TickingBlock<>(ModBlockEntityTypes.ACCELERATOR_TILE_ENTITY, CENTRIFUGE_PROPERTIES));
    public static final RegistryEntry<Block> ENDER_BEECON = BLOCKS.register("ender_beecon", EnderBeecon::new);
    public static final RegistryEntry<Block> SOLIDIFICATION_CHAMBER = BLOCKS.register("solidification_chamber", () -> new SolidificationChamber(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.GLASS).strength(1.5f).requiresCorrectToolForDrops()));
    public static final RegistryEntry<Block> HONEY_POT = BLOCKS.register("honey_pot", () -> new HoneyPotBlock(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f).requiresCorrectToolForDrops()));
    public static final RegistryEntry<Block> BEE_BOX = BLOCKS.register("bee_box", () -> new BeeBoxBlock(BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(0.5f)));
    public static final RegistryEntry<Block> BEE_BOX_TEMP = BLOCKS.register("bee_box_temp", () -> new BeeBoxBlock(BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(0.5f)));

    //region Centrifuge Multiblock Blocks
    public static final RegistryEntry<Block> CENTRIFUGE_CASING = CENTRIFUGE_BLOCKS.register("centrifuge/casing", () -> new CentrifugeCasing(CENTRIFUGE_PROPERTIES));
    public static final RegistryEntry<Block> CENTRIFUGE_PROCESSOR = CENTRIFUGE_BLOCKS.register("centrifuge/processor", () -> new CentrifugeProcessor(CENTRIFUGE_PROPERTIES));
    public static final RegistryEntry<Block> CENTRIFUGE_GEARBOX = CENTRIFUGE_BLOCKS.register("centrifuge/gearbox", () -> new CentrifugeGearbox(CENTRIFUGE_PROPERTIES));

    public static final RegistryEntry<Block> CENTRIFUGE_BASIC_TERMINAL = CENTRIFUGE_BLOCKS.register("centrifuge/terminal/basic", () -> new CentrifugeTerminal(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_BASIC_TERMINAL_ENTITY));
    public static final RegistryEntry<Block> CENTRIFUGE_ADVANCED_TERMINAL = CENTRIFUGE_BLOCKS.register("centrifuge/terminal/advanced", () -> new CentrifugeTerminal(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ADVANCED_TERMINAL_ENTITY));
    public static final RegistryEntry<Block> CENTRIFUGE_ELITE_TERMINAL = CENTRIFUGE_BLOCKS.register("centrifuge/terminal/elite", () -> new CentrifugeTerminal(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ELITE_TERMINAL_ENTITY));
    public static final RegistryEntry<Block> CENTRIFUGE_ULTIMATE_TERMINAL = CENTRIFUGE_BLOCKS.register("centrifuge/terminal/ultimate", () -> new CentrifugeTerminal(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_TERMINAL_ENTITY));

    public static final RegistryEntry<Block> CENTRIFUGE_BASIC_VOID = CENTRIFUGE_BLOCKS.register("centrifuge/void/basic", () -> new CentrifugeVoid(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_BASIC_VOID_ENTITY));
    public static final RegistryEntry<Block> CENTRIFUGE_ADVANCED_VOID = CENTRIFUGE_BLOCKS.register("centrifuge/void/advanced", () -> new CentrifugeVoid(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ADVANCED_VOID_ENTITY));
    public static final RegistryEntry<Block> CENTRIFUGE_ELITE_VOID = CENTRIFUGE_BLOCKS.register("centrifuge/void/elite", () -> new CentrifugeVoid(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ELITE_VOID_ENTITY));
    public static final RegistryEntry<Block> CENTRIFUGE_ULTIMATE_VOID = CENTRIFUGE_BLOCKS.register("centrifuge/void/ultimate", () -> new CentrifugeVoid(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_VOID_ENTITY));

    public static final RegistryEntry<Block> CENTRIFUGE_BASIC_INPUT = CENTRIFUGE_BLOCKS.register("centrifuge/input/item/basic", () -> new CentrifugeInput(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_BASIC_INPUT_ENTITY));
    public static final RegistryEntry<Block> CENTRIFUGE_ADVANCED_INPUT = CENTRIFUGE_BLOCKS.register("centrifuge/input/item/advanced", () -> new CentrifugeInput(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ADVANCED_INPUT_ENTITY));
    public static final RegistryEntry<Block> CENTRIFUGE_ELITE_INPUT = CENTRIFUGE_BLOCKS.register("centrifuge/input/item/elite", () -> new CentrifugeInput(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ELITE_INPUT_ENTITY));
    public static final RegistryEntry<Block> CENTRIFUGE_ULTIMATE_INPUT = CENTRIFUGE_BLOCKS.register("centrifuge/input/item/ultimate", () -> new CentrifugeInput(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_INPUT_ENTITY));

    public static final RegistryEntry<Block> CENTRIFUGE_BASIC_ENERGY_PORT = CENTRIFUGE_BLOCKS.register("centrifuge/input/energy/basic", () -> new CentrifugeEnergyPort(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_BASIC_ENERGY_PORT_ENTITY));
    public static final RegistryEntry<Block> CENTRIFUGE_ADVANCED_ENERGY_PORT = CENTRIFUGE_BLOCKS.register("centrifuge/input/energy/advanced", () -> new CentrifugeEnergyPort(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ADVANCED_ENERGY_PORT_ENTITY));
    public static final RegistryEntry<Block> CENTRIFUGE_ELITE_ENERGY_PORT = CENTRIFUGE_BLOCKS.register("centrifuge/input/energy/elite", () -> new CentrifugeEnergyPort(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ELITE_ENERGY_PORT_ENTITY));
    public static final RegistryEntry<Block> CENTRIFUGE_ULTIMATE_ENERGY_PORT = CENTRIFUGE_BLOCKS.register("centrifuge/input/energy/ultimate", () -> new CentrifugeEnergyPort(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_ENERGY_PORT_ENTITY));

    public static final RegistryEntry<Block> CENTRIFUGE_BASIC_ITEM_OUTPUT = CENTRIFUGE_BLOCKS.register("centrifuge/output/item/basic", () -> new CentrifugeItemOutput(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_BASIC_ITEM_OUTPUT_ENTITY));
    public static final RegistryEntry<Block> CENTRIFUGE_ADVANCED_ITEM_OUTPUT = CENTRIFUGE_BLOCKS.register("centrifuge/output/item/advanced", () -> new CentrifugeItemOutput(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ADVANCED_ITEM_OUTPUT_ENTITY));
    public static final RegistryEntry<Block> CENTRIFUGE_ELITE_ITEM_OUTPUT = CENTRIFUGE_BLOCKS.register("centrifuge/output/item/elite", () -> new CentrifugeItemOutput(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ELITE_ITEM_OUTPUT_ENTITY));
    public static final RegistryEntry<Block> CENTRIFUGE_ULTIMATE_ITEM_OUTPUT = CENTRIFUGE_BLOCKS.register("centrifuge/output/item/ultimate", () -> new CentrifugeItemOutput(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_ITEM_OUTPUT_ENTITY));

    public static final RegistryEntry<Block> CENTRIFUGE_BASIC_FLUID_OUTPUT = CENTRIFUGE_BLOCKS.register("centrifuge/output/fluid/basic", () -> new CentrifugeFluidOutput(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_BASIC_FLUID_OUTPUT_ENTITY));
    public static final RegistryEntry<Block> CENTRIFUGE_ADVANCED_FLUID_OUTPUT = CENTRIFUGE_BLOCKS.register("centrifuge/output/fluid/advanced", () -> new CentrifugeFluidOutput(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ADVANCED_FLUID_OUTPUT_ENTITY));
    public static final RegistryEntry<Block> CENTRIFUGE_ELITE_FLUID_OUTPUT = CENTRIFUGE_BLOCKS.register("centrifuge/output/fluid/elite", () -> new CentrifugeFluidOutput(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ELITE_FLUID_OUTPUT_ENTITY));
    public static final RegistryEntry<Block> CENTRIFUGE_ULTIMATE_FLUID_OUTPUT = CENTRIFUGE_BLOCKS.register("centrifuge/output/fluid/ultimate", () -> new CentrifugeFluidOutput(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_FLUID_OUTPUT_ENTITY));
    //endregion

    public static final RegistryEntry<Block> BASIC_CENTRIFUGE = CENTRIFUGE_BLOCKS.register("centrifuge", () -> new CentrifugeBlock(BlockBehaviour.Properties.of(Material.METAL).strength(2).sound(SoundType.METAL).noOcclusion()));
    public static final RegistryEntry<Block> CENTRIFUGE_CRANK = CENTRIFUGE_BLOCKS.register("centrifuge_crank", () -> new CentrifugeCrankBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(2).sound(SoundType.WOOD).noOcclusion()));


    //region Waxed Blocks
    public static final RegistryEntry<Block> HONEY_GLASS = BLOCKS.register("honey_glass", () -> new HoneyGlass(BlockBehaviour.Properties.copy(Blocks.GLASS).isSuffocating(ModBlocks::never).isViewBlocking(ModBlocks::never).noCollission(), true));
    public static final RegistryEntry<Block> HONEY_GLASS_PLAYER = BLOCKS.register("honey_glass_player", () -> new HoneyGlass(BlockBehaviour.Properties.copy(Blocks.GLASS).isSuffocating(ModBlocks::never).isViewBlocking(ModBlocks::never).noCollission(), false));
    public static final RegistryEntry<Block> WAXED_PLANKS = BLOCKS.register("waxed_planks", () -> new Block(WAXED_PLANKS_PROPERTIES));
    public static final RegistryEntry<StairBlock> WAXED_STAIRS = BLOCKS.register("waxed_stairs", () -> new StairBlock(() -> WAXED_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(WAXED_PLANKS.get()).dynamicShape()));
    public static final RegistryEntry<SlabBlock> WAXED_SLAB = BLOCKS.register("waxed_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(WAXED_PLANKS.get()).dynamicShape()));
    public static final RegistryEntry<FenceBlock> WAXED_FENCE = BLOCKS.register("waxed_fence", () -> new FenceBlock(BlockBehaviour.Properties.copy(WAXED_PLANKS.get()).noOcclusion()));
    public static final RegistryEntry<FenceGateBlock> WAXED_FENCE_GATE = BLOCKS.register("waxed_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.copy(WAXED_PLANKS.get()).noOcclusion()));
    public static final RegistryEntry<WoodButtonBlock> WAXED_BUTTON = BLOCKS.register("waxed_button", () -> new WoodButtonBlock(BlockBehaviour.Properties.copy(WAXED_PLANKS.get()).noOcclusion().noCollission()));
    public static final RegistryEntry<PressurePlateBlock> WAXED_PRESSURE_PLATE = BLOCKS.register("waxed_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(WAXED_PLANKS.get()).noOcclusion().noCollission()));
    public static final RegistryEntry<DoorBlock> WAXED_DOOR = BLOCKS.register("waxed_door", () -> new DoorBlock(BlockBehaviour.Properties.copy(WAXED_PLANKS.get()).noOcclusion()));
    public static final RegistryEntry<TrapDoorBlock> WAXED_TRAPDOOR = BLOCKS.register("waxed_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(WAXED_PLANKS.get()).noOcclusion()));
    public static final RegistryEntry<StandingSignBlock> WAXED_SIGN = BLOCKS.register("waxed_sign", () -> new StandingSignBlock(BlockBehaviour.Properties.copy(WAXED_PLANKS.get()).noOcclusion().noCollission(), WAXED_WOOD_TYPE) {
        @Override
        public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
            return ModBlockEntityTypes.WAXED_SIGN_ENTITY.get().create(pos, state);
        }
    });
    public static final RegistryEntry<WallSignBlock> WAXED_WALL_SIGN = BLOCKS.register("waxed_wall_sign", () -> new WallSignBlock(BlockBehaviour.Properties.copy(WAXED_PLANKS.get()).noOcclusion().noCollission(), WAXED_WOOD_TYPE) {
        @Override
        public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
            return ModBlockEntityTypes.WAXED_SIGN_ENTITY.get().create(pos, state);
        }
    });
    public static final RegistryEntry<Block> TRIMMED_WAXED_PLANKS = BLOCKS.register("trimmed_waxed_planks", () -> new WaxedBlock(WAXED_PLANKS, WAXED_PLANKS_PROPERTIES));
    public static final RegistryEntry<Block> WAXED_MACHINE_BLOCK = BLOCKS.register("waxed_machine_block", () -> new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD)));

    @SuppressWarnings("unused")
    private static boolean never(BlockState state, BlockGetter blockGetter, BlockPos blockPos) {
        return false;
    }
}
