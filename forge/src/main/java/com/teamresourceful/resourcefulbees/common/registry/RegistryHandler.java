package com.teamresourceful.resourcefulbees.common.registry;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.honeydata.HoneyData;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntityType;
import com.teamresourceful.resourcefulbees.common.entity.passive.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.item.BeeSpawnEggItem;
import com.teamresourceful.resourcefulbees.common.item.dispenser.ScraperDispenserBehavior;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registry.dynamic.ModSpawnData;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;

public final class RegistryHandler {

    private RegistryHandler() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.initializeRegistries(bus);
        ModBlocks.initializeRegistries(bus);
        ModEffects.EFFECTS.register(bus);
        ModFluids.initializeRegistries(bus);
        ModEntities.ENTITY_TYPES.register(bus);
        ModBlockEntityTypes.BLOCK_ENTITY_TYPES.register(bus);
        ModEnchantments.ENCHANTMENTS.register(bus);
        ModPOIs.POIS.register(bus);
        ModPotions.POTIONS.register(bus);
        ModMenus.CONTAINER_TYPES.register(bus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(bus);
        ModRecipeTypes.RECIPE_TYPES.register(bus);
        ModVillagerProfessions.PROFESSIONS.register(bus);
        ModFeatures.FEATURES.register(bus);
        ModBiomeModifiers.MODIFIERS.register(bus);
        initOwnRegistries(bus);
    }

    private static void initOwnRegistries(IEventBus bus) {
        ModSpawnData.SPAWN_DATA.register(bus);
    }

    //Dynamic|Iterative Registration Stuff below this line

    public static void addEntityAttributes(EntityAttributeCreationEvent event) {
        ModEntities.getModBees().forEach((s, entityType) -> event.put(entityType.get(), CustomBeeEntity.createBeeAttributes(s).build()));
    }

    public static void registerDynamicBees() {
        BeeRegistry.getRegistry().getBees().forEach((name, beeData) -> registerBee(name, beeData.renderData().sizeModifier()));
    }

    public static void registerDynamicHoney() {
        HoneyRegistry.getRegistry().getRawHoney().forEach(RegistryHandler::registerHoneyBottle);
        HoneyRegistry.getRegistry().stopGeneration();
    }

    private static void registerBee(String name, float sizeModifier) {
        RegistryObject<EntityType<? extends CustomBeeEntity>> beeEntityType = ModEntities.ENTITY_TYPES.register(name + "_bee",
                () -> CustomBeeEntityType.of(name, (type, world) -> new ResourcefulBee(type, world, name), 0.7F * sizeModifier, 0.6F * sizeModifier));
        ModItems.SPAWN_EGG_ITEMS.register(name + "_bee_spawn_egg", () -> new BeeSpawnEggItem(beeEntityType, name));
        ModEntities.getModBees().put(name, beeEntityType);
    }

    private static void registerHoneyBottle(String name, JsonObject honeyData) {
        HoneyData honeyBottleData = HoneyData.codec(name).parse(JsonOps.INSTANCE, honeyData)
                .getOrThrow(false, s -> ResourcefulBees.LOGGER.error("Could not create Custom Honey Data for {} honey", name));
        if (!HoneyRegistry.getRegistry().register(name, honeyBottleData)) {
            ResourcefulBees.LOGGER.error("Duplicate honeys with name {}", name);
        }
    }

    public static void registerDispenserBehaviors() {
        DispenserBlock.registerBehavior(ModItems.SCRAPER.get().asItem(), new ScraperDispenserBehavior());
    }
}
