package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.block.*;
import com.teamresourceful.resourcefulbees.common.block.multiblocks.apiary.ApiaryBlock;
import com.teamresourceful.resourcefulbees.common.block.multiblocks.apiary.ApiaryBreederBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeehiveTier;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks.*;
import com.teamresourceful.resourcefulbees.common.tileentity.TieredBeehiveTileEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {

    private ModBlocks() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<Block> BLOCKS = createBlockRegistry();
    public static final DeferredRegister<Block> HIVES = createBlockRegistry();
    public static final DeferredRegister<Block> HONEYCOMB_BLOCKS = createBlockRegistry();
    public static final DeferredRegister<Block> HONEY_BLOCKS = createBlockRegistry();
    public static final DeferredRegister<Block> HONEY_FLUID_BLOCKS = createBlockRegistry();
    public static final DeferredRegister<Block> CENTRIFUGE_BLOCKS = createBlockRegistry();

    private static DeferredRegister<Block> createBlockRegistry() {
        return DeferredRegister.create(ForgeRegistries.BLOCKS, ResourcefulBees.MOD_ID);
    }

    public static void initializeRegistries(IEventBus bus) {
        BLOCKS.register(bus);
        HIVES.register(bus);
        HONEYCOMB_BLOCKS.register(bus);
        HONEY_BLOCKS.register(bus);
        HONEY_FLUID_BLOCKS.register(bus);
        CENTRIFUGE_BLOCKS.register(bus);
    }


    public static final BlockBehaviour.Properties CENTRIFUGE_PROPERTIES = BlockBehaviour.Properties.of(Material.METAL).strength(2).sound(SoundType.METAL);
    private static final BlockBehaviour.Properties NEST_PROPERTIES = BlockBehaviour.Properties.of(Material.WOOD).strength(1F).sound(SoundType.WOOD);

    private static BlockBehaviour.Properties makeNestProperty(Material material, MaterialColor color, SoundType soundType){
        return BlockBehaviour.Properties.of(material, color).strength(1.0F).sound(soundType);
    }

    private static Supplier<TieredBeehiveBlock> createWoodNest(RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> entityType, BeehiveTier tier) {
        return () -> new TieredBeehiveBlock(entityType, tier, NEST_PROPERTIES);
    }

    private static Supplier<TieredBeehiveBlock> createNest(RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> entityType, BeehiveTier tier, Material material, MaterialColor materialColor, SoundType soundType) {
        return () -> new TieredBeehiveBlock(entityType, tier, makeNestProperty(material, materialColor, soundType));
    }

    public static final RegistryObject<Block> WAX_BLOCK = BLOCKS.register("wax_block", () -> new Block(BlockBehaviour.Properties.of(Material.CLAY).sound(SoundType.SNOW).strength(0.3F)));
    public static final RegistryObject<Block> PREVIEW_BLOCK = BLOCKS.register("preview_block", () -> new Block(BlockBehaviour.Properties.of(Material.DECORATION).sound(SoundType.GLASS)));
    public static final RegistryObject<Block> ERRORED_PREVIEW_BLOCK = BLOCKS.register("error_preview_block", () -> new Block(BlockBehaviour.Properties.of(Material.DECORATION).sound(SoundType.GLASS)));
    public static final RegistryObject<Block> GOLD_FLOWER = BLOCKS.register("gold_flower", () -> new FlowerBlock(MobEffects.INVISIBILITY, 10, BlockBehaviour.Properties.of(Material.PLANT).noCollission().strength(0).sound(SoundType.GRASS)));

    //TODO figure out how to make this nest registration cleaner and reduce duplicate processes
    //region Nests
    public static final RegistryObject<TieredBeehiveBlock> ACACIA_BEE_NEST = HIVES.register("acacia_bee_nest", createWoodNest(ModBlockEntityTypes.ACACIA_BEE_NEST_ENTITY, BeehiveTier.NEST));
    public static final RegistryObject<TieredBeehiveBlock> BIRCH_BEE_NEST = HIVES.register("birch_bee_nest", createWoodNest(ModBlockEntityTypes.BIRCH_BEE_NEST_ENTITY, BeehiveTier.NEST));
    public static final RegistryObject<TieredBeehiveBlock> BROWN_MUSHROOM_BEE_NEST = HIVES.register("brown_mushroom_bee_nest", createNest(ModBlockEntityTypes.BROWN_MUSHROOM_BEE_NEST_ENTITY, BeehiveTier.NEST, Material.WOOD, MaterialColor.DIRT, SoundType.WOOD));
    public static final RegistryObject<TieredBeehiveBlock> CRIMSON_BEE_NEST = HIVES.register("crimson_bee_nest", createNest(ModBlockEntityTypes.CRIMSON_BEE_NEST_ENTITY, BeehiveTier.NEST, Material.NETHER_WOOD, MaterialColor.CRIMSON_STEM, SoundType.STEM));
    public static final RegistryObject<TieredBeehiveBlock> CRIMSON_NYLIUM_BEE_NEST = HIVES.register("crimson_nylium_bee_nest", createNest(ModBlockEntityTypes.CRIMSON_NYLIUM_BEE_NEST_ENTITY, BeehiveTier.NEST, Material.NETHER_WOOD, MaterialColor.CRIMSON_NYLIUM, SoundType.STEM));
    public static final RegistryObject<TieredBeehiveBlock> DARK_OAK_BEE_NEST = HIVES.register("dark_oak_bee_nest", createWoodNest(ModBlockEntityTypes.DARK_OAK_BEE_NEST_ENTITY, BeehiveTier.NEST));
    public static final RegistryObject<TieredBeehiveBlock> GRASS_BEE_NEST = HIVES.register("grass_bee_nest", createNest(ModBlockEntityTypes.GRASS_BEE_NEST_ENTITY, BeehiveTier.NEST, Material.GRASS, MaterialColor.GRASS, SoundType.GRASS));
    public static final RegistryObject<TieredBeehiveBlock> JUNGLE_BEE_NEST = HIVES.register("jungle_bee_nest", createWoodNest(ModBlockEntityTypes.JUNGLE_BEE_NEST_ENTITY, BeehiveTier.NEST));
    public static final RegistryObject<TieredBeehiveBlock> NETHER_BEE_NEST = HIVES.register("nether_bee_nest", createNest(ModBlockEntityTypes.NETHER_BEE_NEST_ENTITY, BeehiveTier.NEST, Material.STONE, MaterialColor.NETHER, SoundType.NETHERRACK));
    public static final RegistryObject<TieredBeehiveBlock> OAK_BEE_NEST = HIVES.register("oak_bee_nest", createWoodNest(ModBlockEntityTypes.OAK_BEE_NEST_ENTITY, BeehiveTier.NEST));
    public static final RegistryObject<TieredBeehiveBlock> PRISMARINE_BEE_NEST = HIVES.register("prismarine_bee_nest", createNest(ModBlockEntityTypes.PRISMARINE_BEE_NEST_ENTITY, BeehiveTier.NEST, Material.STONE, MaterialColor.DIAMOND, SoundType.STONE));
    public static final RegistryObject<TieredBeehiveBlock> PURPUR_BEE_NEST = HIVES.register("purpur_bee_nest", createNest(ModBlockEntityTypes.PURPUR_BEE_NEST_ENTITY, BeehiveTier.NEST, Material.STONE, MaterialColor.COLOR_MAGENTA, SoundType.STONE));
    public static final RegistryObject<TieredBeehiveBlock> RED_MUSHROOM_BEE_NEST = HIVES.register("red_mushroom_bee_nest", createNest(ModBlockEntityTypes.RED_MUSHROOM_BEE_NEST_ENTITY, BeehiveTier.NEST, Material.WOOD, MaterialColor.COLOR_RED, SoundType.WOOD));
    public static final RegistryObject<TieredBeehiveBlock> SPRUCE_BEE_NEST = HIVES.register("spruce_bee_nest", createWoodNest(ModBlockEntityTypes.SPRUCE_BEE_NEST_ENTITY, BeehiveTier.NEST));
    public static final RegistryObject<TieredBeehiveBlock> WARPED_BEE_NEST = HIVES.register("warped_bee_nest", createNest(ModBlockEntityTypes.WARPED_BEE_NEST_ENTITY, BeehiveTier.NEST, Material.NETHER_WOOD, MaterialColor.WARPED_STEM, SoundType.STEM));
    public static final RegistryObject<TieredBeehiveBlock> WARPED_NYLIUM_BEE_NEST = HIVES.register("warped_nylium_bee_nest", createNest(ModBlockEntityTypes.WARPED_NYLIUM_BEE_NEST_ENTITY, BeehiveTier.NEST, Material.NETHER_WOOD, MaterialColor.WARPED_NYLIUM, SoundType.STEM));
    public static final RegistryObject<TieredBeehiveBlock> WITHER_BEE_NEST = HIVES.register("wither_bee_nest", createNest(ModBlockEntityTypes.WITHER_BEE_NEST_ENTITY, BeehiveTier.NEST, Material.STONE, MaterialColor.COLOR_BLACK, SoundType.BASALT));
    //endregion

    //region T1 Hives
    public static final RegistryObject<TieredBeehiveBlock> T1_ACACIA_BEEHIVE = HIVES.register("t1_acacia_beehive", createWoodNest(ModBlockEntityTypes.T1_ACACIA_BEEHIVE_ENTITY, BeehiveTier.T1_HIVE));
    public static final RegistryObject<TieredBeehiveBlock> T1_BIRCH_BEEHIVE = HIVES.register("t1_birch_beehive", createWoodNest(ModBlockEntityTypes.T1_BIRCH_BEEHIVE_ENTITY, BeehiveTier.T1_HIVE));
    public static final RegistryObject<TieredBeehiveBlock> T1_BROWN_MUSHROOM_BEEHIVE = HIVES.register("t1_brown_mushroom_beehive", createNest(ModBlockEntityTypes.T1_BROWN_MUSHROOM_BEEHIVE_ENTITY, BeehiveTier.T1_HIVE, Material.WOOD, MaterialColor.DIRT, SoundType.WOOD));
    public static final RegistryObject<TieredBeehiveBlock> T1_CRIMSON_BEEHIVE = HIVES.register("t1_crimson_beehive", createNest(ModBlockEntityTypes.T1_CRIMSON_BEEHIVE_ENTITY, BeehiveTier.T1_HIVE, Material.NETHER_WOOD, MaterialColor.CRIMSON_STEM, SoundType.STEM));
    public static final RegistryObject<TieredBeehiveBlock> T1_CRIMSON_NYLIUM_BEEHIVE = HIVES.register("t1_crimson_nylium_beehive", createNest(ModBlockEntityTypes.T1_CRIMSON_NYLIUM_BEEHIVE_ENTITY, BeehiveTier.T1_HIVE, Material.NETHER_WOOD, MaterialColor.CRIMSON_NYLIUM, SoundType.STEM));
    public static final RegistryObject<TieredBeehiveBlock> T1_DARK_OAK_BEEHIVE = HIVES.register("t1_dark_oak_beehive", createWoodNest(ModBlockEntityTypes.T1_DARK_OAK_BEEHIVE_ENTITY, BeehiveTier.T1_HIVE));
    public static final RegistryObject<TieredBeehiveBlock> T1_GRASS_BEEHIVE = HIVES.register("t1_grass_beehive", createNest(ModBlockEntityTypes.T1_GRASS_BEEHIVE_ENTITY, BeehiveTier.T1_HIVE, Material.GRASS, MaterialColor.GRASS, SoundType.GRASS));
    public static final RegistryObject<TieredBeehiveBlock> T1_JUNGLE_BEEHIVE = HIVES.register("t1_jungle_beehive", createWoodNest(ModBlockEntityTypes.T1_JUNGLE_BEEHIVE_ENTITY, BeehiveTier.T1_HIVE));
    public static final RegistryObject<TieredBeehiveBlock> T1_NETHER_BEEHIVE = HIVES.register("t1_nether_beehive", createNest(ModBlockEntityTypes.T1_NETHER_BEEHIVE_ENTITY, BeehiveTier.T1_HIVE, Material.STONE, MaterialColor.NETHER, SoundType.NETHERRACK));
    public static final RegistryObject<TieredBeehiveBlock> T1_OAK_BEEHIVE = HIVES.register("t1_oak_beehive", createWoodNest(ModBlockEntityTypes.T1_OAK_BEEHIVE_ENTITY, BeehiveTier.T1_HIVE));
    public static final RegistryObject<TieredBeehiveBlock> T1_PRISMARINE_BEEHIVE = HIVES.register("t1_prismarine_beehive", createNest(ModBlockEntityTypes.T1_PRISMARINE_BEEHIVE_ENTITY, BeehiveTier.T1_HIVE, Material.STONE, MaterialColor.DIAMOND, SoundType.STONE));
    public static final RegistryObject<TieredBeehiveBlock> T1_PURPUR_BEEHIVE = HIVES.register("t1_purpur_beehive", createNest(ModBlockEntityTypes.T1_PURPUR_BEEHIVE_ENTITY, BeehiveTier.T1_HIVE, Material.STONE, MaterialColor.COLOR_MAGENTA, SoundType.STONE));
    public static final RegistryObject<TieredBeehiveBlock> T1_RED_MUSHROOM_BEEHIVE = HIVES.register("t1_red_mushroom_beehive", createNest(ModBlockEntityTypes.T1_RED_MUSHROOM_BEEHIVE_ENTITY, BeehiveTier.T1_HIVE, Material.WOOD, MaterialColor.COLOR_RED, SoundType.WOOD));
    public static final RegistryObject<TieredBeehiveBlock> T1_SPRUCE_BEEHIVE = HIVES.register("t1_spruce_beehive", createWoodNest(ModBlockEntityTypes.T1_SPRUCE_BEEHIVE_ENTITY, BeehiveTier.T1_HIVE));
    public static final RegistryObject<TieredBeehiveBlock> T1_WARPED_BEEHIVE = HIVES.register("t1_warped_beehive", createNest(ModBlockEntityTypes.T1_WARPED_BEEHIVE_ENTITY, BeehiveTier.T1_HIVE, Material.NETHER_WOOD, MaterialColor.WARPED_STEM, SoundType.STEM));
    public static final RegistryObject<TieredBeehiveBlock> T1_WARPED_NYLIUM_BEEHIVE = HIVES.register("t1_warped_nylium_beehive", createNest(ModBlockEntityTypes.T1_WARPED_NYLIUM_BEEHIVE_ENTITY, BeehiveTier.T1_HIVE, Material.NETHER_WOOD, MaterialColor.WARPED_NYLIUM, SoundType.STEM));
    public static final RegistryObject<TieredBeehiveBlock> T1_WITHER_BEEHIVE = HIVES.register("t1_wither_beehive", createNest(ModBlockEntityTypes.T1_WITHER_BEEHIVE_ENTITY, BeehiveTier.T1_HIVE, Material.STONE, MaterialColor.COLOR_BLACK, SoundType.BASALT));
    //endregion

    //region T2_Hives
    public static final RegistryObject<TieredBeehiveBlock> T2_ACACIA_BEEHIVE = HIVES.register("t2_acacia_beehive", createWoodNest(ModBlockEntityTypes.T2_ACACIA_BEEHIVE_ENTITY, BeehiveTier.T2_HIVE));
    public static final RegistryObject<TieredBeehiveBlock> T2_BIRCH_BEEHIVE = HIVES.register("t2_birch_beehive", createWoodNest(ModBlockEntityTypes.T2_BIRCH_BEEHIVE_ENTITY, BeehiveTier.T2_HIVE));
    public static final RegistryObject<TieredBeehiveBlock> T2_BROWN_MUSHROOM_BEEHIVE = HIVES.register("t2_brown_mushroom_beehive", createNest(ModBlockEntityTypes.T2_BROWN_MUSHROOM_BEEHIVE_ENTITY, BeehiveTier.T2_HIVE, Material.WOOD, MaterialColor.DIRT, SoundType.WOOD));
    public static final RegistryObject<TieredBeehiveBlock> T2_CRIMSON_BEEHIVE = HIVES.register("t2_crimson_beehive", createNest(ModBlockEntityTypes.T2_CRIMSON_BEEHIVE_ENTITY, BeehiveTier.T2_HIVE, Material.NETHER_WOOD, MaterialColor.CRIMSON_STEM, SoundType.STEM));
    public static final RegistryObject<TieredBeehiveBlock> T2_CRIMSON_NYLIUM_BEEHIVE = HIVES.register("t2_crimson_nylium_beehive", createNest(ModBlockEntityTypes.T2_CRIMSON_NYLIUM_BEEHIVE_ENTITY, BeehiveTier.T2_HIVE, Material.NETHER_WOOD, MaterialColor.CRIMSON_NYLIUM, SoundType.STEM));
    public static final RegistryObject<TieredBeehiveBlock> T2_DARK_OAK_BEEHIVE = HIVES.register("t2_dark_oak_beehive", createWoodNest(ModBlockEntityTypes.T2_DARK_OAK_BEEHIVE_ENTITY, BeehiveTier.T2_HIVE));
    public static final RegistryObject<TieredBeehiveBlock> T2_GRASS_BEEHIVE = HIVES.register("t2_grass_beehive", createNest(ModBlockEntityTypes.T2_GRASS_BEEHIVE_ENTITY, BeehiveTier.T2_HIVE, Material.GRASS, MaterialColor.GRASS, SoundType.GRASS));
    public static final RegistryObject<TieredBeehiveBlock> T2_JUNGLE_BEEHIVE = HIVES.register("t2_jungle_beehive", createWoodNest(ModBlockEntityTypes.T2_JUNGLE_BEEHIVE_ENTITY, BeehiveTier.T2_HIVE));
    public static final RegistryObject<TieredBeehiveBlock> T2_NETHER_BEEHIVE = HIVES.register("t2_nether_beehive", createNest(ModBlockEntityTypes.T2_NETHER_BEEHIVE_ENTITY, BeehiveTier.T2_HIVE, Material.STONE, MaterialColor.NETHER, SoundType.NETHERRACK));
    public static final RegistryObject<TieredBeehiveBlock> T2_OAK_BEEHIVE = HIVES.register("t2_oak_beehive", createWoodNest(ModBlockEntityTypes.T2_OAK_BEEHIVE_ENTITY, BeehiveTier.T2_HIVE));
    public static final RegistryObject<TieredBeehiveBlock> T2_PRISMARINE_BEEHIVE = HIVES.register("t2_prismarine_beehive", createNest(ModBlockEntityTypes.T2_PRISMARINE_BEEHIVE_ENTITY, BeehiveTier.T2_HIVE, Material.STONE, MaterialColor.DIAMOND, SoundType.STONE));
    public static final RegistryObject<TieredBeehiveBlock> T2_PURPUR_BEEHIVE = HIVES.register("t2_purpur_beehive", createNest(ModBlockEntityTypes.T2_PURPUR_BEEHIVE_ENTITY, BeehiveTier.T2_HIVE, Material.STONE, MaterialColor.COLOR_MAGENTA, SoundType.STONE));
    public static final RegistryObject<TieredBeehiveBlock> T2_RED_MUSHROOM_BEEHIVE = HIVES.register("t2_red_mushroom_beehive", createNest(ModBlockEntityTypes.T2_RED_MUSHROOM_BEEHIVE_ENTITY, BeehiveTier.T2_HIVE, Material.WOOD, MaterialColor.COLOR_RED, SoundType.WOOD));
    public static final RegistryObject<TieredBeehiveBlock> T2_SPRUCE_BEEHIVE = HIVES.register("t2_spruce_beehive", createWoodNest(ModBlockEntityTypes.T2_SPRUCE_BEEHIVE_ENTITY, BeehiveTier.T2_HIVE));
    public static final RegistryObject<TieredBeehiveBlock> T2_WARPED_BEEHIVE = HIVES.register("t2_warped_beehive", createNest(ModBlockEntityTypes.T2_WARPED_BEEHIVE_ENTITY, BeehiveTier.T2_HIVE, Material.NETHER_WOOD, MaterialColor.WARPED_STEM, SoundType.STEM));
    public static final RegistryObject<TieredBeehiveBlock> T2_WARPED_NYLIUM_BEEHIVE = HIVES.register("t2_warped_nylium_beehive", createNest(ModBlockEntityTypes.T2_WARPED_NYLIUM_BEEHIVE_ENTITY, BeehiveTier.T2_HIVE, Material.NETHER_WOOD, MaterialColor.WARPED_NYLIUM, SoundType.STEM));
    public static final RegistryObject<TieredBeehiveBlock> T2_WITHER_BEEHIVE = HIVES.register("t2_wither_beehive", createNest(ModBlockEntityTypes.T2_WITHER_BEEHIVE_ENTITY, BeehiveTier.T2_HIVE, Material.STONE, MaterialColor.COLOR_BLACK, SoundType.BASALT));
    //endregion

    //region T3_Hives
    public static final RegistryObject<TieredBeehiveBlock> T3_ACACIA_BEEHIVE = HIVES.register("t3_acacia_beehive", createWoodNest(ModBlockEntityTypes.T3_ACACIA_BEEHIVE_ENTITY, BeehiveTier.T3_HIVE));
    public static final RegistryObject<TieredBeehiveBlock> T3_BIRCH_BEEHIVE = HIVES.register("t3_birch_beehive", createWoodNest(ModBlockEntityTypes.T3_BIRCH_BEEHIVE_ENTITY, BeehiveTier.T3_HIVE));
    public static final RegistryObject<TieredBeehiveBlock> T3_BROWN_MUSHROOM_BEEHIVE = HIVES.register("t3_brown_mushroom_beehive", createNest(ModBlockEntityTypes.T3_BROWN_MUSHROOM_BEEHIVE_ENTITY, BeehiveTier.T3_HIVE, Material.WOOD, MaterialColor.DIRT, SoundType.WOOD));
    public static final RegistryObject<TieredBeehiveBlock> T3_CRIMSON_BEEHIVE = HIVES.register("t3_crimson_beehive", createNest(ModBlockEntityTypes.T3_CRIMSON_BEEHIVE_ENTITY, BeehiveTier.T3_HIVE, Material.NETHER_WOOD, MaterialColor.CRIMSON_STEM, SoundType.STEM));
    public static final RegistryObject<TieredBeehiveBlock> T3_CRIMSON_NYLIUM_BEEHIVE = HIVES.register("t3_crimson_nylium_beehive", createNest(ModBlockEntityTypes.T3_CRIMSON_NYLIUM_BEEHIVE_ENTITY, BeehiveTier.T3_HIVE, Material.NETHER_WOOD, MaterialColor.CRIMSON_NYLIUM, SoundType.STEM));
    public static final RegistryObject<TieredBeehiveBlock> T3_DARK_OAK_BEEHIVE = HIVES.register("t3_dark_oak_beehive", createWoodNest(ModBlockEntityTypes.T3_DARK_OAK_BEEHIVE_ENTITY, BeehiveTier.T3_HIVE));
    public static final RegistryObject<TieredBeehiveBlock> T3_GRASS_BEEHIVE = HIVES.register("t3_grass_beehive", createNest(ModBlockEntityTypes.T3_GRASS_BEEHIVE_ENTITY, BeehiveTier.T3_HIVE, Material.GRASS, MaterialColor.GRASS, SoundType.GRASS));
    public static final RegistryObject<TieredBeehiveBlock> T3_JUNGLE_BEEHIVE = HIVES.register("t3_jungle_beehive", createWoodNest(ModBlockEntityTypes.T3_JUNGLE_BEEHIVE_ENTITY, BeehiveTier.T3_HIVE));
    public static final RegistryObject<TieredBeehiveBlock> T3_NETHER_BEEHIVE = HIVES.register("t3_nether_beehive", createNest(ModBlockEntityTypes.T3_NETHER_BEEHIVE_ENTITY, BeehiveTier.T3_HIVE, Material.STONE, MaterialColor.NETHER, SoundType.NETHERRACK));
    public static final RegistryObject<TieredBeehiveBlock> T3_OAK_BEEHIVE = HIVES.register("t3_oak_beehive", createWoodNest(ModBlockEntityTypes.T3_OAK_BEEHIVE_ENTITY, BeehiveTier.T3_HIVE));
    public static final RegistryObject<TieredBeehiveBlock> T3_PRISMARINE_BEEHIVE = HIVES.register("t3_prismarine_beehive", createNest(ModBlockEntityTypes.T3_PRISMARINE_BEEHIVE_ENTITY, BeehiveTier.T3_HIVE, Material.STONE, MaterialColor.DIAMOND, SoundType.STONE));
    public static final RegistryObject<TieredBeehiveBlock> T3_PURPUR_BEEHIVE = HIVES.register("t3_purpur_beehive", createNest(ModBlockEntityTypes.T3_PURPUR_BEEHIVE_ENTITY, BeehiveTier.T3_HIVE, Material.STONE, MaterialColor.COLOR_MAGENTA, SoundType.STONE));
    public static final RegistryObject<TieredBeehiveBlock> T3_RED_MUSHROOM_BEEHIVE = HIVES.register("t3_red_mushroom_beehive", createNest(ModBlockEntityTypes.T3_RED_MUSHROOM_BEEHIVE_ENTITY, BeehiveTier.T3_HIVE, Material.WOOD, MaterialColor.COLOR_RED, SoundType.WOOD));
    public static final RegistryObject<TieredBeehiveBlock> T3_SPRUCE_BEEHIVE = HIVES.register("t3_spruce_beehive", createWoodNest(ModBlockEntityTypes.T3_SPRUCE_BEEHIVE_ENTITY, BeehiveTier.T3_HIVE));
    public static final RegistryObject<TieredBeehiveBlock> T3_WARPED_BEEHIVE = HIVES.register("t3_warped_beehive", createNest(ModBlockEntityTypes.T3_WARPED_BEEHIVE_ENTITY, BeehiveTier.T3_HIVE, Material.NETHER_WOOD, MaterialColor.WARPED_STEM, SoundType.STEM));
    public static final RegistryObject<TieredBeehiveBlock> T3_WARPED_NYLIUM_BEEHIVE = HIVES.register("t3_warped_nylium_beehive", createNest(ModBlockEntityTypes.T3_WARPED_NYLIUM_BEEHIVE_ENTITY, BeehiveTier.T3_HIVE, Material.NETHER_WOOD, MaterialColor.WARPED_NYLIUM, SoundType.STEM));
    public static final RegistryObject<TieredBeehiveBlock> T3_WITHER_BEEHIVE = HIVES.register("t3_wither_beehive", createNest(ModBlockEntityTypes.T3_WITHER_BEEHIVE_ENTITY, BeehiveTier.T3_HIVE, Material.STONE, MaterialColor.COLOR_BLACK, SoundType.BASALT));
    //endregion

    //region Apiaries
    public static final RegistryObject<Block> T1_APIARY_BLOCK = BLOCKS.register("t1_apiary", () -> new ApiaryBlock(ApiaryTier.T1_APIARY, 5, 6));
    public static final RegistryObject<Block> T2_APIARY_BLOCK = BLOCKS.register("t2_apiary", () -> new ApiaryBlock(ApiaryTier.T2_APIARY, 5, 6));
    public static final RegistryObject<Block> T3_APIARY_BLOCK = BLOCKS.register("t3_apiary", () -> new ApiaryBlock(ApiaryTier.T3_APIARY, 6, 8));
    public static final RegistryObject<Block> T4_APIARY_BLOCK = BLOCKS.register("t4_apiary", () -> new ApiaryBlock(ApiaryTier.T4_APIARY, 6, 8));
    //endregion








    public static final RegistryObject<Block> APIARY_BREEDER_BLOCK = BLOCKS.register("apiary_breeder", () -> new ApiaryBreederBlock(NEST_PROPERTIES));
    //public static final RegistryObject<Block> APIARY_STORAGE_BLOCK = BLOCKS.register("apiary_storage", () -> new ApiaryStorageBlock(NEST_PROPERTIES));
    public static final RegistryObject<LiquidBlock> HONEY_FLUID_BLOCK = BLOCKS.register("honey_fluid_block", () -> new LiquidBlock(ModFluids.HONEY_STILL, BlockBehaviour.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops()));
    public static final RegistryObject<Block> HONEY_GENERATOR = BLOCKS.register("honey_generator", () -> new HoneyGenerator(CENTRIFUGE_PROPERTIES));
    public static final RegistryObject<Block> CREATIVE_GEN = BLOCKS.register("creative_gen", () -> new CreativeGen(CENTRIFUGE_PROPERTIES));
    public static final RegistryObject<Block> ACCELERATOR = BLOCKS.register("accelerator", () -> new AcceleratorBlock(CENTRIFUGE_PROPERTIES));
    public static final RegistryObject<Block> ENDER_BEECON = BLOCKS.register("ender_beecon", () -> new EnderBeecon(EnderBeecon.PROPERTIES));
    //TODO Change to solidification_chamber for 1.17/1.18
    public static final RegistryObject<Block> SOLIDIFICATION_CHAMBER = BLOCKS.register("honey_congealer", () -> new SolidificationChamber(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.GLASS).strength(1.5f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> HONEY_POT = BLOCKS.register("honey_pot", () -> new HoneyPotBlock(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f).requiresCorrectToolForDrops()));

    //region Centrifuge Multiblock Blocks
    public static final RegistryObject<Block> CENTRIFUGE_CASING = CENTRIFUGE_BLOCKS.register("centrifuge/casing", () -> new CentrifugeCasing(CENTRIFUGE_PROPERTIES));
    public static final RegistryObject<Block> CENTRIFUGE_PROCESSOR = CENTRIFUGE_BLOCKS.register("centrifuge/processor", () -> new CentrifugeProcessor(CENTRIFUGE_PROPERTIES));
    public static final RegistryObject<Block> CENTRIFUGE_GEARBOX = CENTRIFUGE_BLOCKS.register("centrifuge/gearbox", () -> new CentrifugeGearbox(CENTRIFUGE_PROPERTIES));

    public static final RegistryObject<Block> CENTRIFUGE_BASIC_TERMINAL = CENTRIFUGE_BLOCKS.register("centrifuge/terminal/basic", () -> new CentrifugeTerminal(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_BASIC_TERMINAL_ENTITY));
    public static final RegistryObject<Block> CENTRIFUGE_ADVANCED_TERMINAL = CENTRIFUGE_BLOCKS.register("centrifuge/terminal/advanced", () -> new CentrifugeTerminal(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ADVANCED_TERMINAL_ENTITY));
    public static final RegistryObject<Block> CENTRIFUGE_ELITE_TERMINAL = CENTRIFUGE_BLOCKS.register("centrifuge/terminal/elite", () -> new CentrifugeTerminal(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ELITE_TERMINAL_ENTITY));
    public static final RegistryObject<Block> CENTRIFUGE_ULTIMATE_TERMINAL = CENTRIFUGE_BLOCKS.register("centrifuge/terminal/ultimate", () -> new CentrifugeTerminal(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_TERMINAL_ENTITY));

    public static final RegistryObject<Block> CENTRIFUGE_BASIC_VOID = CENTRIFUGE_BLOCKS.register("centrifuge/void/basic", () -> new CentrifugeVoid(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_BASIC_VOID_ENTITY));
    public static final RegistryObject<Block> CENTRIFUGE_ADVANCED_VOID = CENTRIFUGE_BLOCKS.register("centrifuge/void/advanced", () -> new CentrifugeVoid(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ADVANCED_VOID_ENTITY));
    public static final RegistryObject<Block> CENTRIFUGE_ELITE_VOID = CENTRIFUGE_BLOCKS.register("centrifuge/void/elite", () -> new CentrifugeVoid(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ELITE_VOID_ENTITY));
    public static final RegistryObject<Block> CENTRIFUGE_ULTIMATE_VOID = CENTRIFUGE_BLOCKS.register("centrifuge/void/ultimate", () -> new CentrifugeVoid(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_VOID_ENTITY));

    public static final RegistryObject<Block> CENTRIFUGE_BASIC_INPUT = CENTRIFUGE_BLOCKS.register("centrifuge/input/item/basic", () -> new CentrifugeInput(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_BASIC_INPUT_ENTITY));
    public static final RegistryObject<Block> CENTRIFUGE_ADVANCED_INPUT = CENTRIFUGE_BLOCKS.register("centrifuge/input/item/advanced", () -> new CentrifugeInput(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ADVANCED_INPUT_ENTITY));
    public static final RegistryObject<Block> CENTRIFUGE_ELITE_INPUT = CENTRIFUGE_BLOCKS.register("centrifuge/input/item/elite", () -> new CentrifugeInput(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ELITE_INPUT_ENTITY));
    public static final RegistryObject<Block> CENTRIFUGE_ULTIMATE_INPUT = CENTRIFUGE_BLOCKS.register("centrifuge/input/item/ultimate", () -> new CentrifugeInput(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_INPUT_ENTITY));

    public static final RegistryObject<Block> CENTRIFUGE_BASIC_ENERGY_PORT = CENTRIFUGE_BLOCKS.register("centrifuge/input/energy/basic", () -> new CentrifugeEnergyPort(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_BASIC_ENERGY_PORT_ENTITY));
    public static final RegistryObject<Block> CENTRIFUGE_ADVANCED_ENERGY_PORT = CENTRIFUGE_BLOCKS.register("centrifuge/input/energy/advanced", () -> new CentrifugeEnergyPort(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ADVANCED_ENERGY_PORT_ENTITY));
    public static final RegistryObject<Block> CENTRIFUGE_ELITE_ENERGY_PORT = CENTRIFUGE_BLOCKS.register("centrifuge/input/energy/elite", () -> new CentrifugeEnergyPort(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ELITE_ENERGY_PORT_ENTITY));
    public static final RegistryObject<Block> CENTRIFUGE_ULTIMATE_ENERGY_PORT = CENTRIFUGE_BLOCKS.register("centrifuge/input/energy/ultimate", () -> new CentrifugeEnergyPort(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_ENERGY_PORT_ENTITY));

    public static final RegistryObject<Block> CENTRIFUGE_BASIC_ITEM_OUTPUT = CENTRIFUGE_BLOCKS.register("centrifuge/output/item/basic", () -> new CentrifugeItemOutput(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_BASIC_ITEM_OUTPUT_ENTITY));
    public static final RegistryObject<Block> CENTRIFUGE_ADVANCED_ITEM_OUTPUT = CENTRIFUGE_BLOCKS.register("centrifuge/output/item/advanced", () -> new CentrifugeItemOutput(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ADVANCED_ITEM_OUTPUT_ENTITY));
    public static final RegistryObject<Block> CENTRIFUGE_ELITE_ITEM_OUTPUT = CENTRIFUGE_BLOCKS.register("centrifuge/output/item/elite", () -> new CentrifugeItemOutput(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ELITE_ITEM_OUTPUT_ENTITY));
    public static final RegistryObject<Block> CENTRIFUGE_ULTIMATE_ITEM_OUTPUT = CENTRIFUGE_BLOCKS.register("centrifuge/output/item/ultimate", () -> new CentrifugeItemOutput(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_ITEM_OUTPUT_ENTITY));

    public static final RegistryObject<Block> CENTRIFUGE_BASIC_FLUID_OUTPUT = CENTRIFUGE_BLOCKS.register("centrifuge/output/fluid/basic", () -> new CentrifugeFluidOutput(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_BASIC_FLUID_OUTPUT_ENTITY));
    public static final RegistryObject<Block> CENTRIFUGE_ADVANCED_FLUID_OUTPUT = CENTRIFUGE_BLOCKS.register("centrifuge/output/fluid/advanced", () -> new CentrifugeFluidOutput(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ADVANCED_FLUID_OUTPUT_ENTITY));
    public static final RegistryObject<Block> CENTRIFUGE_ELITE_FLUID_OUTPUT = CENTRIFUGE_BLOCKS.register("centrifuge/output/fluid/elite", () -> new CentrifugeFluidOutput(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ELITE_FLUID_OUTPUT_ENTITY));
    public static final RegistryObject<Block> CENTRIFUGE_ULTIMATE_FLUID_OUTPUT = CENTRIFUGE_BLOCKS.register("centrifuge/output/fluid/ultimate", () -> new CentrifugeFluidOutput(CENTRIFUGE_PROPERTIES, ModBlockEntityTypes.CENTRIFUGE_ULTIMATE_FLUID_OUTPUT_ENTITY));
    //endregion
}
