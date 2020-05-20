package com.dungeonderps.resourcefulbees.registry;

//import com.dungeonderps.resourcefulbees.block.CentrifugeBlock;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.block.CentrifugeBlock;
import com.dungeonderps.resourcefulbees.block.HoneycombBlock;
import com.dungeonderps.resourcefulbees.block.IronBeehiveBlock;
import com.dungeonderps.resourcefulbees.container.CentrifugeContainer;
import com.dungeonderps.resourcefulbees.item.*;
import com.dungeonderps.resourcefulbees.recipe.CentrifugeRecipe;
import com.dungeonderps.resourcefulbees.tileentity.CentrifugeBlockEntity;
import com.dungeonderps.resourcefulbees.tileentity.HoneycombBlockEntity;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.sun.corba.se.spi.ior.IORTemplate;
import net.minecraft.block.Block;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.passive.CustomBeeEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.IronBeehiveBlockEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvents;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//import com.dungeonderps.resourcefulbees.tileentity.CentrifugeBlockEntity;

public class RegistryHandler {

	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.ENTITIES, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<TileEntityType<?>>	TILE_ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<PointOfInterestType> POIS = new DeferredRegister<>(ForgeRegistries.POI_TYPES, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = new DeferredRegister<>(ForgeRegistries.CONTAINERS, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = new DeferredRegister<>(ForgeRegistries.RECIPE_SERIALIZERS, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<VillagerProfession> PROFESSIONS = new DeferredRegister<>(ForgeRegistries.PROFESSIONS, ResourcefulBees.MOD_ID);

	public static void init() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		ITEMS.register(bus);
		BLOCKS.register(bus);
		ENTITY_TYPES.register(bus);
		TILE_ENTITY_TYPES.register(bus);
		POIS.register(bus);
		CONTAINER_TYPES.register(bus);
		RECIPE_SERIALIZERS.register(bus);
		PROFESSIONS.register(bus);
	}

	//**************BLOCKS********************************************

	public static final RegistryObject<Block> IRON_BEEHIVE = BLOCKS.register("iron_beehive", () -> new IronBeehiveBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(2).sound(SoundType.METAL)));
	public static final RegistryObject<Block> HONEYCOMB_BLOCK = BLOCKS.register("resourceful_honeycomb_block", () -> new HoneycombBlock());
	public static final RegistryObject<Block> CENTRIFUGE = BLOCKS.register("centrifuge", () -> new CentrifugeBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(2).sound(SoundType.METAL)));
	public static final RegistryObject<Block> WAX_BLOCK = BLOCKS.register("wax_block", () -> new Block(Block.Properties.create(Material.CLAY).sound(SoundType.SNOW).hardnessAndResistance(0.3F)));
	public static final RegistryObject<Block> GOLD_FLOWER = BLOCKS.register("gold_flower", () -> new FlowerBlock(Effects.INVISIBILITY, 10, Block.Properties.create(Material.PLANTS).doesNotBlockMovement().hardnessAndResistance(0).sound(SoundType.PLANT)));


	//**************ITEMS*********************************************

	public static final RegistryObject<Item> RESOURCEFUL_HONEYCOMB = ITEMS.register("resourceful_honeycomb", ResourcefulHoneycomb::new);
	public static final RegistryObject<Item> IRON_BEEHIVE_ITEM = ITEMS.register("iron_beehive",() -> new BlockItem(IRON_BEEHIVE.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> SMOKER = ITEMS.register("smoker", Smoker::new);
	public static final RegistryObject<Item> BELLOW = ITEMS.register("bellow", () -> new Item(new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> SMOKERCAN = ITEMS.register("smoker_can", () -> new Item(new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> BEESWAX = ITEMS.register("beeswax", () -> new Item(new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> HONEYCOMB_BLOCK_ITEM = ITEMS.register("resourceful_honeycomb_block",  () -> new HoneycombBlockItem());
	public static final RegistryObject<Item> CENTRIFUGE_ITEM = ITEMS.register("centrifuge", () -> new BlockItem(CENTRIFUGE.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> WAX_BLOCK_ITEM = ITEMS.register("wax_block", () -> new BlockItem(WAX_BLOCK.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> GOLD_FLOWER_ITEM = ITEMS.register("gold_flower", () -> new BlockItem(GOLD_FLOWER.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> BEE_JAR = ITEMS.register("bee_jar", BeeJar::new);


	//**************TILE ENTITIES*************************************

	public static final RegistryObject<TileEntityType<?>> IRON_BEEHIVE_ENTITY = TILE_ENTITY_TYPES.register("iron_beehive", () -> TileEntityType.Builder
			.create(IronBeehiveBlockEntity::new, IRON_BEEHIVE.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> HONEYCOMB_BLOCK_ENTITY = TILE_ENTITY_TYPES.register("resourceful_honeycomb_block", () -> TileEntityType.Builder
			.create(HoneycombBlockEntity::new, HONEYCOMB_BLOCK.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> CENTRIFUGE_ENTITY = TILE_ENTITY_TYPES.register("centrifuge", () -> TileEntityType.Builder
			.create(CentrifugeBlockEntity::new, CENTRIFUGE.get())
			.build(null));

	//**************ENTITIES******************************************

	public static final RegistryObject<EntityType<CustomBeeEntity>> CUSTOM_BEE = ENTITY_TYPES.register("bee", () -> EntityType.Builder
			.create(CustomBeeEntity::new, EntityClassification.CREATURE)
			.size(0.7F, 0.6F)
			.build("bee"));


	//**************POINT OF INTEREST**********************************

	public static final RegistryObject<PointOfInterestType> IRON_BEEHIVE_POI = POIS.register("iron_beehive", () -> new PointOfInterestType("iron_beehive", ImmutableSet.copyOf(IRON_BEEHIVE.get().getStateContainer().getValidStates()), 1, 1));

	//*************SPAWN EGGS******************************************

	public static final RegistryObject<Item> BEE_SPAWN_EGG = ITEMS.register("bee_spawn_egg",
			() -> new BeeSpawnEggItem(CUSTOM_BEE, 0xffffff, 0xffffff, (new Item.Properties())));

	//****************CONTAINERS****************************************

	public static final RegistryObject<ContainerType<CentrifugeContainer>> CENTRIFUGE_CONTAINER = CONTAINER_TYPES.register("centrifuge", () -> IForgeContainerType
			.create((id,inv,c) -> new CentrifugeContainer(id, inv.player.world, c.readBlockPos(), inv)));

	//****************RECIPES********************************************

	public static final RegistryObject<IRecipeSerializer<?>> CENTRIFUGE_RECIPE = RECIPE_SERIALIZERS.register("centrifuge",
			() -> new CentrifugeRecipe.Serializer<>(CentrifugeRecipe::new));

	//****************VILLAGER PROFESSIONS*******************************

	public static final RegistryObject<VillagerProfession> BEEKEEPER = PROFESSIONS.register("beekeeper", () -> new VillagerProfession("beekeeper", IRON_BEEHIVE_POI.get(), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ITEM_BOTTLE_FILL));
}
