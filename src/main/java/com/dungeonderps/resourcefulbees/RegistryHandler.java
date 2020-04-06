package com.dungeonderps.resourcefulbees;

import net.minecraft.entity.passive.CustomBeeEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {
	
	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.ENTITIES, ResourcefulBees.MOD_ID);

	public static void init() {
		//ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
		//BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
		ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	public static final RegistryObject<EntityType<?>> BEE = ENTITY_TYPES.register("bee", () -> EntityType.Builder.create(CustomBeeEntity::new, EntityClassification.AMBIENT)
			.size(0.7F, 0.6F)
			.build("bee"));

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
}