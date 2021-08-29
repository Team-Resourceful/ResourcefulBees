package com.teamresourceful.resourcefulbees.common.registry;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.honeydata.HoneyBottleData;
import com.teamresourceful.resourcefulbees.common.block.CustomHoneyBlock;
import com.teamresourceful.resourcefulbees.common.block.CustomHoneyFluidBlock;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.entity.passive.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.fluids.CustomHoneyFluid;
import com.teamresourceful.resourcefulbees.common.fluids.HoneyFluidAttributes;
import com.teamresourceful.resourcefulbees.common.item.BeeSpawnEggItem;
import com.teamresourceful.resourcefulbees.common.item.CustomHoneyBottleItem;
import com.teamresourceful.resourcefulbees.common.item.CustomHoneyBucketItem;
import com.teamresourceful.resourcefulbees.common.item.dispenser.ScraperDispenserBehavior;
import com.teamresourceful.resourcefulbees.common.item.dispenser.ShearsDispenserBehavior;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.mixin.invokers.DispenserBlockInvoker;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.*;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.HashMap;
import java.util.Map;

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

    private static final Map<String, RegistryObject<FlowingFluid>> STILL_FLUIDS = new HashMap<>();
    private static final Map<String, RegistryObject<FlowingFluid>> FLOWING_FLUIDS = new HashMap<>();
    private static final Map<String, RegistryObject<Item>> HONEY_BUCKETS = new HashMap<>();
    private static final Map<String, RegistryObject<FlowingFluidBlock>> FLUID_BLOCKS = new HashMap<>();

    private static ForgeFlowingFluid.Properties makeProperties(String name, HoneyBottleData honeyData) {
        HoneyFluidAttributes.Builder builder;
        builder = HoneyFluidAttributes.builder(ModFluids.CUSTOM_FLUID_STILL, ModFluids.CUSTOM_FLUID_FLOWING, honeyData);
        builder.overlay(ModFluids.CUSTOM_FLUID_OVERLAY);
        builder.sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY);
        builder.density(1300);
        builder.temperature(300);
        builder.viscosity(1800);

        return new ForgeFlowingFluid.Properties(STILL_FLUIDS.get(name), FLOWING_FLUIDS.get(name), builder)
                .bucket(HONEY_BUCKETS.get(name))
                .block(FLUID_BLOCKS.get(name))
                .tickRate(20);
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
        HoneyBottleData honeyBottleData = HoneyBottleData.CODEC.parse(JsonOps.INSTANCE, honeyData)
                .getOrThrow(false, s -> ResourcefulBees.LOGGER.error("Could not create Custom Honey Data for {} honey", name));
        final RegistryObject<Item> customHoneyBottle = ModItems.HONEY_BOTTLE_ITEMS.register(name + "_honey_bottle", () -> new CustomHoneyBottleItem(honeyBottleData.getProperties(), honeyBottleData));

        honeyBottleData.setHoneyBottleRegistryObject(customHoneyBottle);
        honeyBottleData.setName(name);

        if (CommonConfig.HONEY_GENERATE_BLOCKS.get() && honeyBottleData.doGenerateHoneyBlock()) {
            final RegistryObject<Block> customHoneyBlock = ModBlocks.HONEY_BLOCKS.register(name + "_honey_block", () -> new CustomHoneyBlock(honeyBottleData));
            final RegistryObject<Item> customHoneyBlockItem = ModItems.HONEY_BLOCK_ITEMS.register(name + "_honey_block", () -> new BlockItem(customHoneyBlock.get(), new Item.Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));

            honeyBottleData.setHoneyBlockRegistryObject(customHoneyBlock);
            honeyBottleData.setHoneyBlockItemRegistryObject(customHoneyBlockItem);
        }

        if (CommonConfig.HONEY_GENERATE_FLUIDS.get() && honeyBottleData.doGenerateHoneyFluid()) {
            STILL_FLUIDS.put(name, ModFluids.STILL_HONEY_FLUIDS.register(name + "_honey", () -> new CustomHoneyFluid.Source(makeProperties(name, honeyBottleData), honeyBottleData)));
            FLOWING_FLUIDS.put(name, ModFluids.FLOWING_HONEY_FLUIDS.register(name + "_honey_flowing", () -> new CustomHoneyFluid.Flowing(makeProperties(name, honeyBottleData), honeyBottleData)));
            HONEY_BUCKETS.put(name, ModItems.HONEY_BUCKET_ITEMS.register(name + "_honey_fluid_bucket", () -> new CustomHoneyBucketItem(STILL_FLUIDS.get(name), new Item.Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES).craftRemainder(Items.BUCKET).stacksTo(1), honeyBottleData)));
            FLUID_BLOCKS.put(name, ModBlocks.HONEY_FLUID_BLOCKS.register(name + "_honey", () -> new CustomHoneyFluidBlock(STILL_FLUIDS.get(name), AbstractBlock.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops(), honeyBottleData)));

            honeyBottleData.setHoneyStillFluidRegistryObject(STILL_FLUIDS.get(name));
            honeyBottleData.setHoneyFlowingFluidRegistryObject(FLOWING_FLUIDS.get(name));
            honeyBottleData.setHoneyBucketItemRegistryObject(HONEY_BUCKETS.get(name));
            honeyBottleData.setHoneyFluidBlockRegistryObject(FLUID_BLOCKS.get(name));
        }
        HoneyRegistry.getRegistry().registerHoney(name, honeyBottleData); //do this separately so it can be called in the reload command that is to be added
    }

    public static void registerDispenserBehaviors() {
        ShearsDispenserBehavior.setDefaultShearsDispenseBehavior(((DispenserBlockInvoker) Blocks.DISPENSER).invokeGetBehavior(new ItemStack(Items.SHEARS)));
        DispenserBlock.registerBehavior(Items.SHEARS.asItem(), new ShearsDispenserBehavior());
        DispenserBlock.registerBehavior(ModItems.SCRAPER.get().asItem(), new ScraperDispenserBehavior());
    }
}
