package com.teamresourceful.resourcefulbees.common.registry;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.honey.CustomHoneyData;
import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyRenderData;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.block.CustomHoneyBlock;
import com.teamresourceful.resourcefulbees.common.block.CustomHoneyFluidBlock;
import com.teamresourceful.resourcefulbees.common.data.DispatchMapCodec;
import com.teamresourceful.resourcefulbees.common.data.honeydata.CustomHoneyBlockData;
import com.teamresourceful.resourcefulbees.common.data.honeydata.fluid.CustomHoneyFluidAttributesData;
import com.teamresourceful.resourcefulbees.common.data.honeydata.fluid.CustomHoneyFluidData;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntityType;
import com.teamresourceful.resourcefulbees.common.entity.passive.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.fluids.CustomHoneyFluid;
import com.teamresourceful.resourcefulbees.common.fluids.HoneyFluidRenderProperties;
import com.teamresourceful.resourcefulbees.common.item.BeeSpawnEggItem;
import com.teamresourceful.resourcefulbees.common.item.CustomHoneyBottleItem;
import com.teamresourceful.resourcefulbees.common.item.CustomHoneyBucketItem;
import com.teamresourceful.resourcefulbees.common.item.dispenser.ScraperDispenserBehavior;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.RegistryEntry;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneyDataRegistry;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

public final class RegistryHandler {

    private RegistryHandler() {
        throw new UtilityClassError();
    }

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.init();
        ModBlocks.BLOCKS.init();
        ModEffects.EFFECTS.init();
        ModFluids.FLUIDS.init();
        ModFluids.FLUID_TYPES.register(bus);
        ModEntities.ENTITY_TYPES.init();
        ModBlockEntityTypes.BLOCK_ENTITY_TYPES.init();
        ModEnchantments.ENCHANTMENTS.init();
        ModPOIs.POIS.init();
        ModPotions.POTIONS.init();
        ModMenus.CONTAINER_TYPES.init();
        ModRecipeSerializers.RECIPE_SERIALIZERS.init();
        ModRecipeTypes.RECIPE_TYPES.init();
        ModVillagerProfessions.PROFESSIONS.init();
        ModFeatures.FEATURES.init();
        ModBiomeModifiers.MODIFIERS.register(bus);
    }

    //Dynamic|Iterative Registration Stuff below this line

    public static void addEntityAttributes(EntityAttributeCreationEvent event) {
        ModEntities.getModBees().forEach((s, entityType) -> event.put(entityType.get(), CustomBeeEntity.createBeeAttributes(s).build()));
    }

    public static void registerDynamicBees() {
        BeeRegistry.get().getBees().forEach((name, beeData) -> registerBee(name, beeData.getRenderData().sizeModifier()));
    }

    public static void registerDynamicHoney() {
        HoneyRegistry.getRegistry().getRawHoney().forEach(RegistryHandler::registerHoneyBottle);
    }

    private static void registerBee(String name, float sizeModifier) {
        RegistryEntry<EntityType<? extends CustomBeeEntity>> beeEntityType = ModEntities.ENTITY_TYPES.register(name + "_bee",
                () -> CustomBeeEntityType.of(name, (type, world) -> new ResourcefulBee(type, world, name), 0.7F * sizeModifier, 0.6F * sizeModifier));
        ModItems.SPAWN_EGG_ITEMS.register(name + "_bee_spawn_egg", () -> new BeeSpawnEggItem(beeEntityType, name));
        ModEntities.getModBees().put(name, beeEntityType);
    }

    private static void registerHoneyBottle(String id, JsonObject honeyData) {
        var data = new DispatchMapCodec<>(ResourceLocation.CODEC, HoneyDataRegistry.codec(id))
                .parse(JsonOps.INSTANCE, honeyData)
                .getOrThrow(false, s -> ResourcefulBees.LOGGER.error("Could not create Honey Data for {} honey", id));
        try {
            HoneyDataRegistry.INSTANCE.check(data.values());
        }catch (Exception e) {
            ResourcefulBees.LOGGER.error("Could not create Honey Data for {} honey", id);
            throw e;
        }
        CustomHoneyData customHoneyData = ResourcefulBeesAPI.getHoneyInitalizers().data(id, data);
        if (!HoneyRegistry.getRegistry().register(id, customHoneyData)) {
            ResourcefulBees.LOGGER.error("Duplicate honeys with name {}", id);
        } else {
            registerHoneyBlock(id, customHoneyData);
            registerHoneyBottle(id, customHoneyData);
            registerHoneyFluid(id, customHoneyData);
        }
    }

    private static void registerHoneyBlock(String name, CustomHoneyData input) {
        input.getOptionalData(CustomHoneyBlockData.SERIALIZER).ifPresent(data -> {
            RegistryEntry<Block> block = ModBlocks.HONEY_BLOCKS.register(name + "_honey_block", () -> new CustomHoneyBlock(data));
            ModItems.HONEY_BLOCK_ITEMS.register(name + "_honey_block", () -> new BlockItem(block.get(), new Item.Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES_HONEY)));
        });
    }

    private static void registerHoneyBottle(String name, CustomHoneyData data) {
        ModItems.HONEY_BOTTLE_ITEMS.register(name + "_honey_bottle", () -> new CustomHoneyBottleItem(data.getBottleData()));
    }

    private static void registerHoneyFluid(String name, CustomHoneyData data) {
        data.getOptionalData(CustomHoneyFluidData.SERIALIZER).ifPresent(fluidData -> {
            ForgeFlowingFluid.Properties[] properties = {null};

            RegistryObject<FluidType> fluidType = ModFluids.FLUID_TYPES.register(name + "_honey", () -> honeyFluid(CustomHoneyFluidAttributesData.getProperties(fluidData.fluidAttributesData()), fluidData.renderData()));
            RegistryEntry<FlowingFluid> stillFluidRegistry = ModFluids.STILL_HONEY_FLUIDS.register(name + "_honey", () -> new CustomHoneyFluid.Source(properties[0], fluidData));
            RegistryEntry<FlowingFluid> flowingFluidRegistry = ModFluids.FLOWING_HONEY_FLUIDS.register(name + "_honey_flowing", () -> new CustomHoneyFluid.Flowing(properties[0], fluidData));
            RegistryEntry<Item> fluidBucketRegistry = ModItems.HONEY_BUCKET_ITEMS.register(name + "_honey_bucket", () -> new CustomHoneyBucketItem(stillFluidRegistry, new Item.Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES_HONEY).craftRemainder(Items.BUCKET).stacksTo(1), fluidData));
            RegistryEntry<LiquidBlock> blockFluidRegistry = ModBlocks.HONEY_FLUID_BLOCKS.register(name + "_honey", () -> new CustomHoneyFluidBlock(stillFluidRegistry, ModBlocks.HONEY_FLUID_BLOCK_PROPERTIES, fluidData));

            properties[0] = new ForgeFlowingFluid.Properties(fluidType, stillFluidRegistry, flowingFluidRegistry)
                    .bucket(fluidBucketRegistry)
                    .block(blockFluidRegistry)
                    .tickRate(20);
        });
    }

    private static FluidType honeyFluid(FluidType.Properties properties, HoneyRenderData renderData) {
        // We are forced to use an anon object here because forge calls initClient in base constructor therefore making it so we cant pass data in.
        return new FluidType(properties) {
            @Override
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new HoneyFluidRenderProperties(renderData));
            }
        };
    }

    public static void registerDispenserBehaviors() {
        DispenserBlock.registerBehavior(ModItems.SCRAPER.get().asItem(), new ScraperDispenserBehavior());
    }
}
