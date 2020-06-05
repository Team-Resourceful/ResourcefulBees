package com.dungeonderps.resourcefulbees.registry;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.block.CentrifugeBlock;
import com.dungeonderps.resourcefulbees.block.HoneycombBlock;
import com.dungeonderps.resourcefulbees.block.beehive.Tier1BeehiveBlock;
import com.dungeonderps.resourcefulbees.block.beehive.Tier2BeehiveBlock;
import com.dungeonderps.resourcefulbees.block.beehive.Tier3BeehiveBlock;
import com.dungeonderps.resourcefulbees.block.beehive.Tier4BeehiveBlock;
import com.dungeonderps.resourcefulbees.block.beenest.*;
import com.dungeonderps.resourcefulbees.container.CentrifugeContainer;
import com.dungeonderps.resourcefulbees.entity.passive.ResourcefulBee;
import com.dungeonderps.resourcefulbees.item.*;
import com.dungeonderps.resourcefulbees.recipe.CentrifugeRecipe;
import com.dungeonderps.resourcefulbees.tileentity.CentrifugeBlockEntity;
import com.dungeonderps.resourcefulbees.tileentity.HoneycombBlockEntity;
import com.dungeonderps.resourcefulbees.tileentity.beehive.Tier1BeehiveBlockEntity;
import com.dungeonderps.resourcefulbees.tileentity.beehive.Tier2BeehiveBlockEntity;
import com.dungeonderps.resourcefulbees.tileentity.beehive.Tier3BeehiveBlockEntity;
import com.dungeonderps.resourcefulbees.tileentity.beehive.Tier4BeehiveBlockEntity;
import com.dungeonderps.resourcefulbees.tileentity.beenest.*;
import com.dungeonderps.resourcefulbees.world.BeeNestFeature;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvents;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("ConstantConditions")
public class RegistryHandler {

	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.ENTITIES, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<TileEntityType<?>>	TILE_ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<PointOfInterestType> POIS = new DeferredRegister<>(ForgeRegistries.POI_TYPES, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = new DeferredRegister<>(ForgeRegistries.CONTAINERS, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = new DeferredRegister<>(ForgeRegistries.RECIPE_SERIALIZERS, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<VillagerProfession> PROFESSIONS = new DeferredRegister<>(ForgeRegistries.PROFESSIONS, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<Feature<?>> FEATURES = new DeferredRegister<>(ForgeRegistries.FEATURES, ResourcefulBees.MOD_ID);

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
		FEATURES.register(bus);
	}

	//**************BLOCKS********************************************

	public static final RegistryObject<Block> T1_BEEHIVE = BLOCKS.register("t1_beehive", () -> new Tier1BeehiveBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(2).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> T2_BEEHIVE = BLOCKS.register("t2_beehive", () -> new Tier2BeehiveBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(2).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> T3_BEEHIVE = BLOCKS.register("t3_beehive", () -> new Tier3BeehiveBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(2).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> T4_BEEHIVE = BLOCKS.register("t4_beehive", () -> new Tier4BeehiveBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(2).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> HONEYCOMB_BLOCK = BLOCKS.register("resourceful_honeycomb_block", HoneycombBlock::new);
	public static final RegistryObject<Block> CENTRIFUGE = BLOCKS.register("centrifuge", () -> new CentrifugeBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(2).sound(SoundType.METAL)));
	public static final RegistryObject<Block> WAX_BLOCK = BLOCKS.register("wax_block", () -> new Block(Block.Properties.create(Material.CLAY).sound(SoundType.SNOW).hardnessAndResistance(0.3F)));
	public static final RegistryObject<Block> GOLD_FLOWER = BLOCKS.register("gold_flower", () -> new FlowerBlock(Effects.INVISIBILITY, 10, Block.Properties.create(Material.PLANTS).doesNotBlockMovement().hardnessAndResistance(0).sound(SoundType.PLANT)));
	public static final RegistryObject<Block> BEE_NEST = BLOCKS.register("bee_nest", () -> new BeeNestBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(0.3F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> ACACIA_BEE_NEST = BLOCKS.register("acacia_bee_nest", () -> new AcaciaBeeNestBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(0.3F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> GRASS_BEE_NEST = BLOCKS.register("grass_bee_nest", () -> new GrassBeeNestBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(0.3F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> JUNGLE_BEE_NEST = BLOCKS.register("jungle_bee_nest", () -> new JungleBeeNestBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(0.3F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> NETHER_BEE_NEST = BLOCKS.register("nether_bee_nest", () -> new NetherBeeNestBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(0.3F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> PRISMARINE_BEE_NEST = BLOCKS.register("prismarine_bee_nest", () -> new PrismarineBeeNestBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(0.3F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> PURPUR_BEE_NEST = BLOCKS.register("purpur_bee_nest", () -> new PurpurBeeNestBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(0.3F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> WITHER_BEE_NEST = BLOCKS.register("wither_bee_nest", () -> new WitherBeeNestBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(0.3F).sound(SoundType.WOOD)));

	//**************ITEMS*********************************************

	public static final RegistryObject<Item> RESOURCEFUL_HONEYCOMB = ITEMS.register("resourceful_honeycomb", ResourcefulHoneycomb::new);
	public static final RegistryObject<Item> T1_BEEHIVE_ITEM = ITEMS.register("t1_beehive",() -> new BlockItem(T1_BEEHIVE.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> T2_BEEHIVE_ITEM = ITEMS.register("t2_beehive",() -> new BlockItem(T2_BEEHIVE.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> T3_BEEHIVE_ITEM = ITEMS.register("t3_beehive",() -> new BlockItem(T3_BEEHIVE.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> T4_BEEHIVE_ITEM = ITEMS.register("t4_beehive",() -> new BlockItem(T4_BEEHIVE.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> SMOKER = ITEMS.register("smoker", Smoker::new);
	public static final RegistryObject<Item> BELLOW = ITEMS.register("bellow", () -> new Item(new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> SMOKERCAN = ITEMS.register("smoker_can", () -> new Item(new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> BEESWAX = ITEMS.register("beeswax", () -> new Item(new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> HONEYCOMB_BLOCK_ITEM = ITEMS.register("resourceful_honeycomb_block", HoneycombBlockItem::new);
	public static final RegistryObject<Item> CENTRIFUGE_ITEM = ITEMS.register("centrifuge", () -> new BlockItem(CENTRIFUGE.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> WAX_BLOCK_ITEM = ITEMS.register("wax_block", () -> new BlockItem(WAX_BLOCK.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> GOLD_FLOWER_ITEM = ITEMS.register("gold_flower", () -> new BlockItem(GOLD_FLOWER.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> BEE_JAR = ITEMS.register("bee_jar", BeeJar::new);
	public static final RegistryObject<Item> BEE_NEST_ITEM = ITEMS.register("bee_nest",() -> new BlockItem(BEE_NEST.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> ACACIA_BEE_NEST_ITEM = ITEMS.register("acacia_bee_nest",() -> new BlockItem(ACACIA_BEE_NEST.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> GRASS_BEE_NEST_ITEM = ITEMS.register("grass_bee_nest",() -> new BlockItem(GRASS_BEE_NEST.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> JUNGLE_BEE_NEST_ITEM = ITEMS.register("jungle_bee_nest",() -> new BlockItem(JUNGLE_BEE_NEST.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> NETHER_BEE_NEST_ITEM = ITEMS.register("nether_bee_nest",() -> new BlockItem(NETHER_BEE_NEST.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> PRISMARINE_BEE_NEST_ITEM = ITEMS.register("prismarine_bee_nest",() -> new BlockItem(PRISMARINE_BEE_NEST.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> PURPUR_BEE_NEST_ITEM = ITEMS.register("purpur_bee_nest",() -> new BlockItem(PURPUR_BEE_NEST.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> WITHER_BEE_NEST_ITEM = ITEMS.register("wither_bee_nest",() -> new BlockItem(WITHER_BEE_NEST.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));

	//**************TILE ENTITIES*************************************

	public static final RegistryObject<TileEntityType<?>> T1_BEEHIVE_ENTITY = TILE_ENTITY_TYPES.register("t1_beehive", () -> TileEntityType.Builder
			.create(Tier1BeehiveBlockEntity::new, T1_BEEHIVE.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> T2_BEEHIVE_ENTITY = TILE_ENTITY_TYPES.register("t2_beehive", () -> TileEntityType.Builder
			.create(Tier2BeehiveBlockEntity::new, T2_BEEHIVE.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> T3_BEEHIVE_ENTITY = TILE_ENTITY_TYPES.register("t3_beehive", () -> TileEntityType.Builder
			.create(Tier3BeehiveBlockEntity::new, T3_BEEHIVE.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> T4_BEEHIVE_ENTITY = TILE_ENTITY_TYPES.register("t4_beehive", () -> TileEntityType.Builder
			.create(Tier4BeehiveBlockEntity::new, T4_BEEHIVE.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> HONEYCOMB_BLOCK_ENTITY = TILE_ENTITY_TYPES.register("resourceful_honeycomb_block", () -> TileEntityType.Builder
			.create(HoneycombBlockEntity::new, HONEYCOMB_BLOCK.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> CENTRIFUGE_ENTITY = TILE_ENTITY_TYPES.register("centrifuge", () -> TileEntityType.Builder
			.create(CentrifugeBlockEntity::new, CENTRIFUGE.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> BEE_NEST_ENTITY = TILE_ENTITY_TYPES.register("bee_nest", () -> TileEntityType.Builder
			.create(BeeNestEntity::new, BEE_NEST.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> ACACIA_BEE_NEST_ENTITY = TILE_ENTITY_TYPES.register("acacia_bee_nest", () -> TileEntityType.Builder
			.create(AcaciaBeeNest::new, ACACIA_BEE_NEST.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> GRASS_BEE_NEST_ENTITY = TILE_ENTITY_TYPES.register("grass_bee_nest", () -> TileEntityType.Builder
			.create(GrassBeeNest::new, GRASS_BEE_NEST.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> JUNGLE_BEE_NEST_ENTITY = TILE_ENTITY_TYPES.register("jungle_bee_nest", () -> TileEntityType.Builder
			.create(JungleBeeNest::new, JUNGLE_BEE_NEST.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> NETHER_BEE_NEST_ENTITY = TILE_ENTITY_TYPES.register("nether_bee_nest", () -> TileEntityType.Builder
			.create(NetherBeeNest::new, NETHER_BEE_NEST.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> PRISMARINE_BEE_NEST_ENTITY = TILE_ENTITY_TYPES.register("prismarine_bee_nest", () -> TileEntityType.Builder
			.create(PrismarineBeeNest::new, PRISMARINE_BEE_NEST.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> PURPUR_BEE_NEST_ENTITY = TILE_ENTITY_TYPES.register("purpur_bee_nest", () -> TileEntityType.Builder
			.create(PurpurBeeNest::new, PURPUR_BEE_NEST.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> WITHER_BEE_NEST_ENTITY = TILE_ENTITY_TYPES.register("wither_bee_nest", () -> TileEntityType.Builder
			.create(WitherBeeNest::new, WITHER_BEE_NEST.get())
			.build(null));

	//**************ENTITIES******************************************

	public static final RegistryObject<EntityType<ResourcefulBee>> CUSTOM_BEE = ENTITY_TYPES.register("bee", () -> EntityType.Builder
			.create(ResourcefulBee::new, EntityClassification.CREATURE)
			.size(0.7F, 0.6F)
			.build("bee"));

	//**************POINT OF INTEREST**********************************

	public static final RegistryObject<PointOfInterestType> T1_BEEHIVE_POI = POIS.register("t1_beehive", () -> new PointOfInterestType("t1_beehive", ImmutableSet.copyOf(T1_BEEHIVE.get().getStateContainer().getValidStates()), 1, 1));

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

	public static final RegistryObject<VillagerProfession> BEEKEEPER = PROFESSIONS.register("beekeeper", () -> new VillagerProfession("beekeeper", T1_BEEHIVE_POI.get(), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ITEM_BOTTLE_FILL));

	//****************FEATURES*****************************************
	public static final RegistryObject<Feature<NoFeatureConfig>> BEE_NEST_FEATURE = FEATURES.register("bee_nest_feature", () -> new BeeNestFeature(NoFeatureConfig::deserialize));
}
