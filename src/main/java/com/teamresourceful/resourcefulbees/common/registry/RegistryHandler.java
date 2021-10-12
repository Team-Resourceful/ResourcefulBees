package com.teamresourceful.resourcefulbees.common.registry;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.honeydata.HoneyData;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.entity.passive.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.item.BeeSpawnEggItem;
import com.teamresourceful.resourcefulbees.common.item.dispenser.ScraperDispenserBehavior;
import com.teamresourceful.resourcefulbees.common.item.dispenser.ShearsDispenserBehavior;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.mixin.invokers.DispenserBlockInvoker;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.*;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class RegistryHandler {

    private RegistryHandler() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.initializeRegistries(bus);
        ModBlocks.initializeRegistries(bus);
        ModEffects.EFFECTS.register(bus);
        ModFluids.initializeRegistries(bus);
        ModEntities.ENTITY_TYPES.register(bus);
        ModBlockEntityTypes.TILE_ENTITY_TYPES.register(bus);
        ModPOIs.POIS.register(bus);
        ModPotions.POTIONS.register(bus);
        ModContainers.CONTAINER_TYPES.register(bus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(bus);
        ModVillagerProfessions.PROFESSIONS.register(bus);
        ModFeatures.FEATURES.register(bus);
    }

    //Dynamic|Iterative Registration Stuff below this line

    public static void addEntityAttributes(EntityAttributeCreationEvent event) {
        ModEntities.getModBees().forEach((s, entityType) -> event.put(entityType, CustomBeeEntity.createBeeAttributes(s).build()));
    }

    public static void registerDynamicBees() {


        BeeRegistry.getRegistry().getBees().forEach((name, beeData) -> {

            registerBee(name, beeData.getRenderData().getSizeModifier());
        });
    }

    public static void registerDynamicHoney() {
        HoneyRegistry.getRegistry().getRawHoney().forEach(RegistryHandler::registerHoneyBottle);
    }

    private static void registerBee(String name, float sizeModifier) {
        final EntityType<? extends CustomBeeEntity> beeEntityType = EntityType.Builder
                .<ResourcefulBee>of((type, world) -> new ResourcefulBee(type, world, name), ModConstants.BEE_MOB_CATEGORY)
                .sized(0.7F * sizeModifier, 0.6F * sizeModifier)
                .build(name + "_bee");
        ModEntities.ENTITY_TYPES.register(name + "_bee", () -> beeEntityType);
        ModItems.SPAWN_EGG_ITEMS.register(name + "_bee_spawn_egg", () -> new BeeSpawnEggItem(beeEntityType, name));
        ModEntities.getModBees().put(name, beeEntityType);
    }

    // TODO does this need to use the codec? - epic
    private static void registerHoneyBottle(String name, JsonObject honeyData) {
        HoneyData honeyBottleData = HoneyData.codec(name).parse(JsonOps.INSTANCE, honeyData)
                .getOrThrow(false, s -> ResourcefulBees.LOGGER.error("Could not create Custom Honey Data for {} honey", name));
        HoneyRegistry.getRegistry().registerHoney(name, honeyBottleData); //do this separately so it can be called in the reload command that is to be added
    }

    public static void registerDispenserBehaviors() {
        ShearsDispenserBehavior.setDefaultShearsDispenseBehavior(((DispenserBlockInvoker) Blocks.DISPENSER).invokeGetBehavior(new ItemStack(Items.SHEARS)));
        DispenserBlock.registerBehavior(Items.SHEARS.asItem(), new ShearsDispenserBehavior());
        DispenserBlock.registerBehavior(ModItems.SCRAPER.get().asItem(), new ScraperDispenserBehavior());
    }
}
