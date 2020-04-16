package com.dungeonderps.resourcefulbees;

import com.dungeonderps.resourcefulbees.block.IronBeehiveBlock;
import com.dungeonderps.resourcefulbees.item.ResourcefulHoneycomb;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.passive.CustomBeeEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Block.Properties;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.particles.ParticleType;
import net.minecraft.tileentity.IronBeehiveBlockEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RegistryHandler {
	
	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.ENTITIES, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<TileEntityType<?>>	TILE_ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<PointOfInterestType> POIS = new DeferredRegister<>(ForgeRegistries.POI_TYPES, ResourcefulBees.MOD_ID);

	private static Block.Properties IronBeehiveProperties;

	public static void init() {

		//need to do the properties here, cannot do in the constructor call due to private fields in high tier super class.
		IronBeehiveProperties = Block.Properties.create(Material.IRON).hardnessAndResistance(2).sound(SoundType.METAL);

		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
		BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
		ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
		TILE_ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
		POIS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	//**************BLOCKS********************************************

	public static final RegistryObject<Block> IRON_BEEHIVE = BLOCKS.register("iron_beehive", () -> new IronBeehiveBlock(IronBeehiveProperties));

	//**************ITEMS*********************************************

	public static final RegistryObject<Item> RESOURCEFUL_HONEYCOMB = ITEMS.register("resourceful_honeycomb", ResourcefulHoneycomb::new);
	public static final RegistryObject<Item> IRON_BEEHIVE_ITEM = ITEMS.register("iron_beehive", () -> new BlockItem(IRON_BEEHIVE.get(), new Item.Properties().group(ItemGroup.MISC)));

	//**************TILE ENTITIES*************************************

	public static final RegistryObject<TileEntityType<?>> IRON_BEEHIVE_ENTITY = TILE_ENTITY_TYPES.register("iron_beehive", () -> TileEntityType.Builder
			.create(IronBeehiveBlockEntity::new, IRON_BEEHIVE.get())
			.build(null));

	//**************ENTITIES******************************************

	public static final RegistryObject<EntityType<?>> BEE = ENTITY_TYPES.register("bee", () -> EntityType.Builder
			.create(CustomBeeEntity::new, EntityClassification.CREATURE)
			.size(0.7F, 0.6F)
			.build("bee"));


	//**************POINT OF INTEREST**********************************

	public static final RegistryObject<PointOfInterestType> IRON_BEEHIVE_POI = POIS.register("iron_beehive",
			() -> new PointOfInterestType("iron_beehive", Sets.newHashSet(IRON_BEEHIVE.get().getStateContainer().getValidStates()), 0, 1));


/*
	public static void beeItems(String name) {
		//String nameLowered = name.toLowerCase();
		//Honeycomb
		//RegistryObject<Item> HONEYCOMB = ITEMS.register(nameLowered + "_honeycomb", () -> new Item(new Item.Properties().group(ItemGroup.MISC)));
		//Honey Blocks
		//RegistryObject<Block> HONEYBLOCK = BLOCKS.register(nameLowered + "_honey_block", () -> new HoneyBlock(Block.Properties.create(Material.CLAY, MaterialColor.ADOBE).speedFactor(0.4F).jumpFactor(0.5F).notSolid().sound(SoundType.field_226947_m_)));
		//RegistryObject<Item> HONEYBLOCKITEM = ITEMS.register(nameLowered + "_honey_block",  () -> new BlockItem(HONEYBLOCK.get(), new Item.Properties().group(ItemGroup.DECORATIONS)));
		//Honeycomb Blocks
		//RegistryObject<Block> HONEYCOMBBLOCK = BLOCKS.register(nameLowered + "_honeycomb_block", () -> new Block(Block.Properties.create(Material.CLAY, MaterialColor.ADOBE).hardnessAndResistance(0.6F).sound(SoundType.CORAL)));
		//RegistryObject<Item> HONEYCOMBBLOCKITEM = ITEMS.register(nameLowered + "_honeycomb_block",  () -> new BlockItem(HONEYCOMBBLOCK.get(), new Item.Properties().group(ItemGroup.DECORATIONS)));
		//Honey Bottle
		//RegistryObject<Item> HONEYBOTTLE = ITEMS.register(nameLowered + "_honeybottle", () -> new HoneyBottleItem((new Item.Properties()).containerItem(Items.GLASS_BOTTLE).food(Foods.HONEY).group(ItemGroup.FOOD).maxStackSize(16)));
	}





	//public static final RegistryObject<Item> BEEWAX = ITEMS.register("bee_wax", () -> new Item(new Item.Properties().group(ItemGroup.DECORATIONS)));
	//TODO Change hive block to use custom give block class for bee hive implementation.
	//public static final RegistryObject<Block> INDUSTRIALHIVE = BLOCKS.register("industrial_hive", () -> new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.0F, 3.0F)));
	//public static final RegistryObject<Item> INDUSTRIALHIVEITEM = ITEMS.register("industrial_hive",  () -> new BlockItem(INDUSTRIALHIVE.get(), new Item.Properties().group(ItemGroup.DECORATIONS)));
 */
}
