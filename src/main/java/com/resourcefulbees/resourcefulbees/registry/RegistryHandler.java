package com.resourcefulbees.resourcefulbees.registry;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.block.*;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.entity.passive.ResourcefulBee;
import com.resourcefulbees.resourcefulbees.item.*;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.utils.color.Color;
import net.minecraft.block.*;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {

	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, ResourcefulBees.MOD_ID);

	public static void init() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		ModItems.ITEMS.register(bus);
		ModBlocks.BLOCKS.register(bus);
		ModFluids.FLUIDS.register(bus);
		ENTITY_TYPES.register(bus);
		ModTileEntityTypes.TILE_ENTITY_TYPES.register(bus);
		ModPOIs.POIS.register(bus);
		ModContainers.CONTAINER_TYPES.register(bus);
		ModRecipeSerializers.RECIPE_SERIALIZERS.register(bus);
		ModVillagerProfessions.PROFESSIONS.register(bus);
		ModFeatures.FEATURES.register(bus);
	}

	//Dynamic|Iterative Registration Stuff below this line

	public static void addEntityAttributes() {
		BeeRegistry.MOD_BEES.forEach((s, customBee) -> GlobalEntityTypeAttributes.put(customBee.get(), BeeEntity.createBeeAttributes().build()));
	}

	public static void registerDynamicBees() {
		BeeRegistry.getRegistry().getBees().forEach((name, customBee) -> {
			if (customBee.shouldResourcefulBeesDoForgeRegistration) {
				if (customBee.hasHoneycomb() && !customBee.hasCustomDrop()) {
					registerHoneycomb(name, customBee);
				} else if (customBee.hasHoneycomb() && customBee.hasCustomDrop() && !customBee.getName().equals(BeeConstants.OREO_BEE)) {
					fillCustomDrops(customBee);
				}
				registerBee(name, customBee);
			}
		});
	}

	private static void registerHoneycomb(String name, CustomBeeData customBeeData) {
		final RegistryObject<Block> customHoneycombBlock = ModBlocks.BLOCKS.register(name + "_honeycomb_block", () -> new HoneycombBlock(name, customBeeData.getColorData(), Block.Properties.from(Blocks.HONEYCOMB_BLOCK)));
		final RegistryObject<Item> customHoneycomb = ModItems.ITEMS.register(name + "_honeycomb", () -> new HoneycombItem(name, customBeeData.getColorData(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
		final RegistryObject<Item> customHoneycombBlockItem = ModItems.ITEMS.register(name + "_honeycomb_block", () -> new BlockItem(customHoneycombBlock.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));

		customBeeData.setCombBlockRegistryObject(customHoneycombBlock);
		customBeeData.setCombRegistryObject(customHoneycomb);
		customBeeData.setCombBlockItemRegistryObject(customHoneycombBlockItem);
	}

	private static void registerBee(String name, CustomBeeData customBeeData) {
		final RegistryObject<EntityType<? extends CustomBeeEntity>> customBeeEntity = ENTITY_TYPES.register(name + "_bee", () -> EntityType.Builder
				.<ResourcefulBee>create((type, world) -> new ResourcefulBee(type, world, customBeeData), EntityClassification.CREATURE)
				.size(0.7F, 0.6F)
				.build(name + "_bee"));

		final RegistryObject<Item> customBeeSpawnEgg = ModItems.ITEMS.register(name + "_bee_spawn_egg",
				() -> new BeeSpawnEggItem(customBeeEntity, Color.parseInt(BeeConstants.VANILLA_BEE_COLOR), 0x303030, customBeeData.getColorData(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));

		BeeRegistry.MOD_BEES.put(name, customBeeEntity);
		customBeeData.setEntityTypeRegistryID(customBeeEntity.getId());
		customBeeData.setSpawnEggItemRegistryObject(customBeeSpawnEgg);
	}

	private static void fillCustomDrops(CustomBeeData customBeeData) {
		RegistryObject<Item> customComb = RegistryObject.of(new ResourceLocation(customBeeData.getCustomCombDrop()), ForgeRegistries.ITEMS);
		RegistryObject<Item> customCombBlock = RegistryObject.of(new ResourceLocation(customBeeData.getCustomCombBlockDrop()), ForgeRegistries.ITEMS);

		customBeeData.setCombRegistryObject(customComb);
		customBeeData.setCombBlockItemRegistryObject(customCombBlock);
	}

	@SubscribeEvent
	public void onRegisterBlocks(RegistryEvent.Register<Block> event) {
		//Exists to disallow further bee registrations
		//preventing potential for NPE's
		BeeRegistry.getRegistry().denyRegistration();
	}
}
