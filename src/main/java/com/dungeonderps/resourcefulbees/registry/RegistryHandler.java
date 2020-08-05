package com.dungeonderps.resourcefulbees.registry;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.block.*;
import com.dungeonderps.resourcefulbees.container.*;
import com.dungeonderps.resourcefulbees.entity.passive.ResourcefulBee;
import com.dungeonderps.resourcefulbees.item.*;
import com.dungeonderps.resourcefulbees.lib.BeeConstants;
import com.dungeonderps.resourcefulbees.recipe.CentrifugeRecipe;
import com.dungeonderps.resourcefulbees.tileentity.*;
import com.dungeonderps.resourcefulbees.world.BeeNestFeature;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.passive.BeeEntity;
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

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<TileEntityType<?>>	TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<PointOfInterestType> POIS = DeferredRegister.create(ForgeRegistries.POI_TYPES, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(ForgeRegistries.PROFESSIONS, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, ResourcefulBees.MOD_ID);

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

	//region**************BLOCKS********************************************

	public static final RegistryObject<Block> T1_BEEHIVE = BLOCKS.register("t1_beehive", () -> new TieredBeehiveBlock(1,1.0F, Block.Properties.create(Material.WOOD).hardnessAndResistance(2).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> T2_BEEHIVE = BLOCKS.register("t2_beehive", () -> new TieredBeehiveBlock(2, 1.5F, Block.Properties.create(Material.WOOD).hardnessAndResistance(2).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> T3_BEEHIVE = BLOCKS.register("t3_beehive", () -> new TieredBeehiveBlock(3, 2.0F, Block.Properties.create(Material.WOOD).hardnessAndResistance(2).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> T4_BEEHIVE = BLOCKS.register("t4_beehive", () -> new TieredBeehiveBlock(4, 4.0F, Block.Properties.create(Material.WOOD).hardnessAndResistance(2).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> HONEYCOMB_BLOCK = BLOCKS.register("resourceful_honeycomb_block", HoneycombBlock::new);
	public static final RegistryObject<Block> CENTRIFUGE = BLOCKS.register("centrifuge", () -> new CentrifugeBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(2).sound(SoundType.METAL)));
	public static final RegistryObject<Block> WAX_BLOCK = BLOCKS.register("wax_block", () -> new Block(Block.Properties.create(Material.CLAY).sound(SoundType.SNOW).hardnessAndResistance(0.3F)));
	public static final RegistryObject<Block> PREVIEW_BLOCK = BLOCKS.register("preview_block", () -> new Block(Block.Properties.create(Material.MISCELLANEOUS).sound(SoundType.GLASS)));
	public static final RegistryObject<Block> ERRORED_PREVIEW_BLOCK = BLOCKS.register("error_preview_block", () -> new Block(Block.Properties.create(Material.MISCELLANEOUS).sound(SoundType.GLASS)));
	public static final RegistryObject<Block> GOLD_FLOWER = BLOCKS.register("gold_flower", () -> new FlowerBlock(Effects.INVISIBILITY, 10, Block.Properties.create(Material.PLANTS).doesNotBlockMovement().hardnessAndResistance(0).sound(SoundType.PLANT)));
	public static final RegistryObject<Block> BEE_NEST = BLOCKS.register("bee_nest", () -> new TieredBeehiveBlock(0, 0.5F,Block.Properties.create(Material.WOOD).hardnessAndResistance(0.3F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> ACACIA_BEE_NEST = BLOCKS.register("acacia_bee_nest", () -> new TieredBeehiveBlock(0, 0.5F, Block.Properties.create(Material.WOOD).hardnessAndResistance(0.3F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> GRASS_BEE_NEST = BLOCKS.register("grass_bee_nest", () -> new TieredBeehiveBlock(0, 0.5F, Block.Properties.create(Material.WOOD).hardnessAndResistance(0.3F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> JUNGLE_BEE_NEST = BLOCKS.register("jungle_bee_nest", () -> new TieredBeehiveBlock(0, 0.5F, Block.Properties.create(Material.WOOD).hardnessAndResistance(0.3F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> NETHER_BEE_NEST = BLOCKS.register("nether_bee_nest", () -> new TieredBeehiveBlock(0, 0.5F, Block.Properties.create(Material.WOOD).hardnessAndResistance(0.3F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> PRISMARINE_BEE_NEST = BLOCKS.register("prismarine_bee_nest", () -> new TieredBeehiveBlock(0, 0.5F, Block.Properties.create(Material.WOOD).hardnessAndResistance(0.3F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> PURPUR_BEE_NEST = BLOCKS.register("purpur_bee_nest", () -> new TieredBeehiveBlock(0, 0.5F, Block.Properties.create(Material.WOOD).hardnessAndResistance(0.3F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> WITHER_BEE_NEST = BLOCKS.register("wither_bee_nest", () -> new TieredBeehiveBlock(0, 0.5F, Block.Properties.create(Material.WOOD).hardnessAndResistance(0.3F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> T1_APIARY_BLOCK = BLOCKS.register("t1_apiary", () -> new ApiaryBlock(5, 5, 6));
	public static final RegistryObject<Block> T2_APIARY_BLOCK = BLOCKS.register("t2_apiary", () -> new ApiaryBlock(6, 5, 6));
	public static final RegistryObject<Block> T3_APIARY_BLOCK = BLOCKS.register("t3_apiary", () -> new ApiaryBlock(7, 6, 8));
	public static final RegistryObject<Block> T4_APIARY_BLOCK = BLOCKS.register("t4_apiary", () -> new ApiaryBlock(8, 6, 8));
	public static final RegistryObject<Block> APIARY_STORAGE_BLOCK = BLOCKS.register("apiary_storage", () -> new ApiaryStorageBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(.3F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> MECHANICAL_CENTRIFUGE = BLOCKS.register("mechanical_centrifuge", () -> new MechanicalCentrifugeBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(2).sound(SoundType.METAL).notSolid()));
	//endregion

	//region**************ITEMS*********************************************

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
	public static final RegistryObject<Item> MECHANICAL_CENTRIFUGE_ITEM = ITEMS.register("mechanical_centrifuge", () -> new BlockItem(MECHANICAL_CENTRIFUGE.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
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
	public static final RegistryObject<Item> T1_APIARY_ITEM = ITEMS.register("t1_apiary",() -> new BlockItem(T1_APIARY_BLOCK.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> T2_APIARY_ITEM = ITEMS.register("t2_apiary",() -> new BlockItem(T2_APIARY_BLOCK.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> T3_APIARY_ITEM = ITEMS.register("t3_apiary",() -> new BlockItem(T3_APIARY_BLOCK.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> T4_APIARY_ITEM = ITEMS.register("t4_apiary",() -> new BlockItem(T4_APIARY_BLOCK.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> APIARY_STORAGE_ITEM = ITEMS.register("apiary_storage",() -> new BlockItem(APIARY_STORAGE_BLOCK.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> IRON_STORAGE_UPGRADE = ITEMS.register("iron_storage_upgrade", () -> new UpgradeItem(UpgradeItem.builder()
			.upgradeType(BeeConstants.NBT_STORAGE_UPGRADE)
			.upgradeModification(BeeConstants.NBT_SLOT_UPGRADE, 27F)
			.build()));
	public static final RegistryObject<Item> GOLD_STORAGE_UPGRADE = ITEMS.register("gold_storage_upgrade", () -> new UpgradeItem(UpgradeItem.builder()
			.upgradeType(BeeConstants.NBT_STORAGE_UPGRADE)
			.upgradeModification(BeeConstants.NBT_SLOT_UPGRADE, 54F)
			.build()));
	public static final RegistryObject<Item> DIAMOND_STORAGE_UPGRADE = ITEMS.register("diamond_storage_upgrade", () -> new UpgradeItem(UpgradeItem.builder()
			.upgradeType(BeeConstants.NBT_STORAGE_UPGRADE)
			.upgradeModification(BeeConstants.NBT_SLOT_UPGRADE, 81F)
			.build()));
	public static final RegistryObject<Item> EMERALD_STORAGE_UPGRADE = ITEMS.register("emerald_storage_upgrade", () -> new UpgradeItem(UpgradeItem.builder()
			.upgradeType(BeeConstants.NBT_STORAGE_UPGRADE)
			.upgradeModification(BeeConstants.NBT_SLOT_UPGRADE, 108F)
			.build()));
	//endregion

	//region**************TILE ENTITIES*************************************

	public static final RegistryObject<TileEntityType<?>> TIERED_BEEHIVE_TILE_ENTITY = TILE_ENTITY_TYPES.register("tiered_beehive", () -> TileEntityType.Builder
			.create(TieredBeehiveTileEntity::new, T1_BEEHIVE.get(), T2_BEEHIVE.get(), T3_BEEHIVE.get(), T4_BEEHIVE.get(), ACACIA_BEE_NEST.get(), GRASS_BEE_NEST.get(), JUNGLE_BEE_NEST.get(), NETHER_BEE_NEST.get(), PRISMARINE_BEE_NEST.get(), PURPUR_BEE_NEST.get(), WITHER_BEE_NEST.get(), BEE_NEST.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> HONEYCOMB_BLOCK_ENTITY = TILE_ENTITY_TYPES.register("resourceful_honeycomb_block", () -> TileEntityType.Builder
			.create(HoneycombTileEntity::new, HONEYCOMB_BLOCK.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> CENTRIFUGE_ENTITY = TILE_ENTITY_TYPES.register("centrifuge", () -> TileEntityType.Builder
			.create(CentrifugeTileEntity::new, CENTRIFUGE.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> MECHANICAL_CENTRIFUGE_ENTITY = TILE_ENTITY_TYPES.register("mechanical_centrifuge", () -> TileEntityType.Builder
			.create(MechanicalCentrifugeTileEntity::new, MECHANICAL_CENTRIFUGE.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> APIARY_TILE_ENTITY = TILE_ENTITY_TYPES.register("apiary", () -> TileEntityType.Builder
			.create(ApiaryTileEntity::new, T1_APIARY_BLOCK.get(), T2_APIARY_BLOCK.get(), T3_APIARY_BLOCK.get(), T4_APIARY_BLOCK.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> APIARY_STORAGE_TILE_ENTITY = TILE_ENTITY_TYPES.register("apiary_storage", () -> TileEntityType.Builder
			.create(ApiaryStorageTileEntity::new, APIARY_STORAGE_BLOCK.get())
			.build(null));
	//endregion

	//region**************ENTITIES******************************************

	public static final RegistryObject<EntityType<ResourcefulBee>> CUSTOM_BEE = ENTITY_TYPES.register("bee", () -> EntityType.Builder
			.create(ResourcefulBee::new, EntityClassification.CREATURE)
			.size(0.7F, 0.6F)
			.build("bee"));
	//endregion

	//region**************POINT OF INTEREST*********************************

	public static final RegistryObject<PointOfInterestType> TIERED_BEEHIVE_POI = POIS.register("t1_beehive", () -> new PointOfInterestType("t1_beehive", ImmutableSet.copyOf(T1_BEEHIVE.get().getStateContainer().getValidStates()), 1, 1));
	//endregion

	//region**************SPAWN EGGS****************************************

	public static final RegistryObject<Item> BEE_SPAWN_EGG = ITEMS.register("bee_spawn_egg",
			() -> new BeeSpawnEggItem(CUSTOM_BEE, 0xffffff, 0xffffff, (new Item.Properties())));
	//endregion

	//region**************CONTAINERS****************************************

	public static final RegistryObject<ContainerType<CentrifugeContainer>> CENTRIFUGE_CONTAINER = CONTAINER_TYPES.register("centrifuge", () -> IForgeContainerType
			.create((id,inv,c) -> new CentrifugeContainer(id, inv.player.world, c.readBlockPos(), inv)));

	public static final RegistryObject<ContainerType<MechanicalCentrifugeContainer>> MECHANICAL_CENTRIFUGE_CONTAINER = CONTAINER_TYPES.register("mechanical_centrifuge", () -> IForgeContainerType
			.create((id,inv,c) -> new MechanicalCentrifugeContainer(id, inv.player.world, c.readBlockPos(), inv)));

	public static final RegistryObject<ContainerType<UnvalidatedApiaryContainer>> UNVALIDATED_APIARY_CONTAINER = CONTAINER_TYPES.register("unvalidated_apiary", () -> IForgeContainerType
			.create((id,inv,c) -> new UnvalidatedApiaryContainer(id, inv.player.world, c.readBlockPos(), inv)));

	public static final RegistryObject<ContainerType<ValidatedApiaryContainer>> VALIDATED_APIARY_CONTAINER = CONTAINER_TYPES.register("validated_apiary", () -> IForgeContainerType
			.create((id,inv,c) -> new ValidatedApiaryContainer(id, inv.player.world, c.readBlockPos(), inv)));

	public static final RegistryObject<ContainerType<ApiaryStorageContainer>> APIARY_STORAGE_CONTAINER = CONTAINER_TYPES.register("apiary_storage", () -> IForgeContainerType
			.create((id,inv,c) -> new ApiaryStorageContainer(id, inv.player.world, c.readBlockPos(), inv)));
	//endregion

	//region****************RECIPES*****************************************

	public static final RegistryObject<IRecipeSerializer<?>> CENTRIFUGE_RECIPE = RECIPE_SERIALIZERS.register("centrifuge",
			() -> new CentrifugeRecipe.Serializer<>(CentrifugeRecipe::new));
	//endregion

	//region****************VILLAGER PROFESSIONS****************************

	public static final RegistryObject<VillagerProfession> BEEKEEPER = PROFESSIONS.register("beekeeper", () -> new VillagerProfession("beekeeper", TIERED_BEEHIVE_POI.get(), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ITEM_BOTTLE_FILL));
	//endregion

	//region****************FEATURES****************************************

	public static final RegistryObject<Feature<NoFeatureConfig>> BEE_NEST_FEATURE = FEATURES.register("bee_nest_feature", () -> new BeeNestFeature(NoFeatureConfig.field_236558_a_));
	//endregion

	public static void addEntityAttributes() {
		GlobalEntityTypeAttributes.put(CUSTOM_BEE.get(), BeeEntity.func_234182_eX_().create());
	}
}
