package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.block.*;
import com.teamresourceful.resourcefulbees.common.block.multiblocks.apiary.ApiaryBlock;
import com.teamresourceful.resourcefulbees.common.block.multiblocks.apiary.ApiaryBreederBlock;
import com.teamresourceful.resourcefulbees.common.block.multiblocks.apiary.ApiaryStorageBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.tileentity.HoneyTankTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.potion.Effects;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModBlocks {

    private ModBlocks() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ResourcefulBees.MOD_ID);
    public static final DeferredRegister<Block> NEST_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ResourcefulBees.MOD_ID);
    public static final DeferredRegister<Block> HONEYCOMB_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ResourcefulBees.MOD_ID);
    public static final DeferredRegister<Block> HONEY_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ResourcefulBees.MOD_ID);
    public static final DeferredRegister<Block> HONEY_FLUID_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ResourcefulBees.MOD_ID);

    public static void initializeRegistries(IEventBus bus) {
        BLOCKS.register(bus);
        NEST_BLOCKS.register(bus);
        HONEYCOMB_BLOCKS.register(bus);
        HONEY_BLOCKS.register(bus);
        HONEY_FLUID_BLOCKS.register(bus);
    }


    private static final AbstractBlock.Properties CENTRIFUGE_PROPERTIES = AbstractBlock.Properties.of(Material.METAL).strength(2).sound(SoundType.METAL);
    private static final AbstractBlock.Properties NEST_PROPERTIES = AbstractBlock.Properties.of(Material.WOOD).strength(1F).sound(SoundType.WOOD);

    private static AbstractBlock.Properties makeNestProperty(Material material, MaterialColor color, SoundType soundType){
        return AbstractBlock.Properties.of(material, color).strength(1.0F).sound(soundType);
    }

    private static Supplier<Block> createWoodNest() {
        return () -> new TieredBeehiveBlock(0, 0.5F, NEST_PROPERTIES);
    }

    private static Supplier<Block> createNest(Material material, MaterialColor materialColor, SoundType soundType) {
        return () -> new TieredBeehiveBlock(0, 0.5F, makeNestProperty(material, materialColor, soundType));
    }

    public static final RegistryObject<Block> WAX_BLOCK = BLOCKS.register("wax_block", () -> new Block(AbstractBlock.Properties.of(Material.CLAY).sound(SoundType.SNOW).strength(0.3F)));
    public static final RegistryObject<Block> PREVIEW_BLOCK = BLOCKS.register("preview_block", () -> new Block(AbstractBlock.Properties.of(Material.DECORATION).sound(SoundType.GLASS)));
    public static final RegistryObject<Block> ERRORED_PREVIEW_BLOCK = BLOCKS.register("error_preview_block", () -> new Block(AbstractBlock.Properties.of(Material.DECORATION).sound(SoundType.GLASS)));
    public static final RegistryObject<Block> GOLD_FLOWER = BLOCKS.register("gold_flower", () -> new FlowerBlock(Effects.INVISIBILITY, 10, AbstractBlock.Properties.of(Material.PLANT).noCollission().strength(0).sound(SoundType.GRASS)));
    public static final RegistryObject<Block> OAK_BEE_NEST = NEST_BLOCKS.register("bee_nest", createWoodNest());
    public static final RegistryObject<Block> BIRCH_BEE_NEST = NEST_BLOCKS.register("birch_bee_nest", createWoodNest());
    public static final RegistryObject<Block> BROWN_MUSHROOM_BEE_NEST = NEST_BLOCKS.register("brown_mushroom_bee_nest", createNest(Material.WOOD, MaterialColor.DIRT, SoundType.WOOD));
    public static final RegistryObject<Block> CRIMSON_BEE_NEST = NEST_BLOCKS.register("crimson_bee_nest", createNest(Material.NETHER_WOOD, MaterialColor.CRIMSON_STEM, SoundType.STEM));
    public static final RegistryObject<Block> CRIMSON_NYLIUM_BEE_NEST = NEST_BLOCKS.register("crimson_nylium_bee_nest", createNest(Material.NETHER_WOOD, MaterialColor.CRIMSON_NYLIUM, SoundType.STEM));
    public static final RegistryObject<Block> DARK_OAK_BEE_NEST = NEST_BLOCKS.register("dark_oak_bee_nest", createWoodNest());
    public static final RegistryObject<Block> RED_MUSHROOM_BEE_NEST = NEST_BLOCKS.register("red_mushroom_bee_nest", createNest(Material.WOOD, MaterialColor.COLOR_RED, SoundType.WOOD));
    public static final RegistryObject<Block> SPRUCE_BEE_NEST = NEST_BLOCKS.register("spruce_bee_nest", createWoodNest());
    public static final RegistryObject<Block> WARPED_BEE_NEST = NEST_BLOCKS.register("warped_bee_nest", createNest(Material.NETHER_WOOD, MaterialColor.WARPED_STEM, SoundType.STEM));
    public static final RegistryObject<Block> WARPED_NYLIUM_BEE_NEST = NEST_BLOCKS.register("warped_nylium_bee_nest", createNest(Material.NETHER_WOOD, MaterialColor.WARPED_NYLIUM, SoundType.STEM));
    public static final RegistryObject<Block> ACACIA_BEE_NEST = NEST_BLOCKS.register("acacia_bee_nest", createWoodNest());
    public static final RegistryObject<Block> GRASS_BEE_NEST = NEST_BLOCKS.register("grass_bee_nest", createNest(Material.GRASS, MaterialColor.GRASS, SoundType.GRASS));
    public static final RegistryObject<Block> JUNGLE_BEE_NEST = NEST_BLOCKS.register("jungle_bee_nest", createWoodNest());
    public static final RegistryObject<Block> NETHER_BEE_NEST = NEST_BLOCKS.register("nether_bee_nest", createNest(Material.STONE, MaterialColor.NETHER, SoundType.NETHERRACK));
    public static final RegistryObject<Block> PRISMARINE_BEE_NEST = NEST_BLOCKS.register("prismarine_bee_nest", createNest(Material.STONE, MaterialColor.DIAMOND, SoundType.STONE));
    public static final RegistryObject<Block> PURPUR_BEE_NEST = NEST_BLOCKS.register("purpur_bee_nest", createNest(Material.STONE, MaterialColor.COLOR_MAGENTA, SoundType.STONE));
    public static final RegistryObject<Block> WITHER_BEE_NEST = NEST_BLOCKS.register("wither_bee_nest", createNest(Material.STONE, MaterialColor.COLOR_BLACK, SoundType.BASALT));
    public static final RegistryObject<Block> T1_APIARY_BLOCK = BLOCKS.register("t1_apiary", () -> new ApiaryBlock(1, 5, 6));
    public static final RegistryObject<Block> T2_APIARY_BLOCK = BLOCKS.register("t2_apiary", () -> new ApiaryBlock(2, 5, 6));
    public static final RegistryObject<Block> T3_APIARY_BLOCK = BLOCKS.register("t3_apiary", () -> new ApiaryBlock(3, 6, 8));
    public static final RegistryObject<Block> T4_APIARY_BLOCK = BLOCKS.register("t4_apiary", () -> new ApiaryBlock(4, 6, 8));
    public static final RegistryObject<Block> APIARY_BREEDER_BLOCK = BLOCKS.register("apiary_breeder", () -> new ApiaryBreederBlock(NEST_PROPERTIES));
    public static final RegistryObject<Block> APIARY_STORAGE_BLOCK = BLOCKS.register("apiary_storage", () -> new ApiaryStorageBlock(NEST_PROPERTIES));
    public static final RegistryObject<Block> HONEY_GENERATOR = BLOCKS.register("honey_generator", () -> new HoneyGenerator(CENTRIFUGE_PROPERTIES));
    public static final RegistryObject<FlowingFluidBlock> HONEY_FLUID_BLOCK = BLOCKS.register("honey_fluid_block", () -> new FlowingFluidBlock(ModFluids.HONEY_STILL, AbstractBlock.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops()));
    public static final RegistryObject<Block> CREATIVE_GEN = BLOCKS.register("creative_gen", () -> new CreativeGen(CENTRIFUGE_PROPERTIES));
    public static final RegistryObject<Block> ACCELERATOR = BLOCKS.register("accelerator", () -> new AcceleratorBlock(CENTRIFUGE_PROPERTIES));
    public static final RegistryObject<Block> ENDER_BEECON = BLOCKS.register("ender_beecon", () -> new EnderBeecon(EnderBeecon.PROPERTIES));
    public static final RegistryObject<Block> PURPUR_HONEY_TANK = BLOCKS.register("purpur_honey_tank", () -> new HoneyTank(HoneyTank.PURPUR, HoneyTankTileEntity.TankTier.PURPUR));
    public static final RegistryObject<Block> NETHER_HONEY_TANK = BLOCKS.register("nether_honey_tank", () -> new HoneyTank(HoneyTank.NETHER, HoneyTankTileEntity.TankTier.NETHER));
    public static final RegistryObject<Block> WOODEN_HONEY_TANK = BLOCKS.register("wooden_honey_tank", () -> new HoneyTank(HoneyTank.WOODEN, HoneyTankTileEntity.TankTier.WOODEN));
    public static final RegistryObject<Block> HONEY_CONGEALER = BLOCKS.register("honey_congealer", () -> new HoneyCongealer(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.GLASS).strength(1.5f).requiresCorrectToolForDrops().harvestTool(ToolType.PICKAXE).harvestLevel(1)));
    public static final RegistryObject<Block> HONEY_PIPE = BLOCKS.register("honey_pipe", () -> new HoneyPipe(AbstractBlock.Properties.of(Material.METAL).sound(SoundType.METAL).strength(1.5f).requiresCorrectToolForDrops().harvestTool(ToolType.PICKAXE).harvestLevel(1)));
    public static final RegistryObject<Block> BOTTOMLESS_HONEY_POT = BLOCKS.register("bottomless_honey_pot", () -> new BottomlessHoneyPot(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f).requiresCorrectToolForDrops().harvestLevel(1).harvestTool(ToolType.PICKAXE)));
}