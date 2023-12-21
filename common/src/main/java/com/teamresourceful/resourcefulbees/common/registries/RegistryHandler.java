package com.teamresourceful.resourcefulbees.common.registries;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.honey.CustomHoneyData;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.blocks.CustomHoneyBlock;
import com.teamresourceful.resourcefulbees.common.blocks.CustomHoneyFluidBlock;
import com.teamresourceful.resourcefulbees.common.entities.CustomBeeEntityType;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.fluids.CustomHoneyFluid;
import com.teamresourceful.resourcefulbees.common.fluids.HoneyFluidInformation;
import com.teamresourceful.resourcefulbees.common.items.BeeSpawnEggItem;
import com.teamresourceful.resourcefulbees.common.items.dispenser.ScraperDispenserBehavior;
import com.teamresourceful.resourcefulbees.common.items.honey.CustomHoneyBottleItem;
import com.teamresourceful.resourcefulbees.common.items.honey.CustomHoneyBucketItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registries.custom.HoneyDataRegistry;
import com.teamresourceful.resourcefulbees.common.registries.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.*;
import com.teamresourceful.resourcefulbees.common.setup.data.honeydata.CustomHoneyBlockData;
import com.teamresourceful.resourcefulbees.common.setup.data.honeydata.fluid.CustomHoneyFluidData;
import com.teamresourceful.resourcefulbees.common.subsystems.RegistrySubsystem;
import com.teamresourceful.resourcefullib.common.codecs.maps.DispatchMapCodec;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import earth.terrarium.botarium.common.registry.fluid.FluidData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.ServiceLoader;

public final class RegistryHandler {

    private RegistryHandler() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static void init() {
        ServiceLoader.load(RegistrySubsystem.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .forEach(RegistrySubsystem::init);

        ItemGroupResourcefulBees.register();
        ModFluidProperties.PROPERTIES.initialize();
        ModFluids.FLUIDS.init();
        ModEntities.ENTITY_TYPES.init();
        ModBlocks.BLOCKS.init();
        ModItems.ITEMS.init();
        ModBlockEntityTypes.BLOCK_ENTITY_TYPES.init();
        ModRecipes.RECIPE_TYPES.init();
        ModRecipeSerializers.RECIPE_SERIALIZERS.init();
        ModMenuTypes.MENUS.init();
        ModEffects.EFFECTS.init();
        ModArguments.ARGUMENTS.init();
        ModEnchantments.ENCHANTMENTS.init();
        ModPOIs.POIS.init();
        ModPotions.POTIONS.init();
        ModVillagerProfessions.PROFESSIONS.init();
        ModFeatures.FEATURES.init();
    }

    //Dynamic|Iterative Registration Stuff below this line

    public static void registerDynamicBees() {
        BeeRegistry.get().getBees().forEach((name, beeData) -> registerBee(name, beeData.getRenderData().sizeModifier()));
    }

    public static void registerDynamicHoney() {
        HoneyRegistry.getRegistry().getRawHoney().forEach(RegistryHandler::registerHoneyBottle);
    }

    private static void registerBee(String name, float sizeModifier) {
        RegistryEntry<EntityType<? extends CustomBeeEntity>> beeEntityType = ModEntities.BEES.register(name + "_bee",
                () -> CustomBeeEntityType.of(name, (type, world) -> new ResourcefulBee(type, world, name), 0.7F * sizeModifier, 0.6F * sizeModifier));
        ModItems.SPAWN_EGG_ITEMS.register(name + "_bee_spawn_egg", () -> new BeeSpawnEggItem(beeEntityType, name));
        ModEntities.getModBees().put(name, beeEntityType);
    }

    private static void registerHoneyBottle(String id, JsonObject honeyData) {
        var data = new DispatchMapCodec<>(ResourceLocation.CODEC, HoneyDataRegistry.codec(id))
                .parse(JsonOps.INSTANCE, honeyData)
                .getOrThrow(false, s -> ModConstants.LOGGER.error("Could not create Honey Data for {} honey", id));
        try {
            HoneyDataRegistry.INSTANCE.check(data.values());
        }catch (Exception e) {
            ModConstants.LOGGER.error("Could not create Honey Data for {} honey", id);
            throw e;
        }
        CustomHoneyData customHoneyData = ResourcefulBeesAPI.getHoneyInitalizers().data(id, data);
        if (!HoneyRegistry.getRegistry().register(id, customHoneyData)) {
            ModConstants.LOGGER.error("Duplicate honeys with name {}", id);
        } else {
            registerHoneyBlock(id, customHoneyData);
            registerHoneyBottle(id, customHoneyData);
            registerHoneyFluid(id, customHoneyData);
        }
    }

    private static void registerHoneyBlock(String name, CustomHoneyData input) {
        input.getOptionalData(CustomHoneyBlockData.SERIALIZER).ifPresent(data -> {
            RegistryEntry<Block> block = ModBlocks.HONEY_BLOCKS.register(name + "_honey_block", () -> new CustomHoneyBlock(data));
            ModItems.HONEY_BLOCK_ITEMS.register(name + "_honey_block", () -> new BlockItem(block.get(), new Item.Properties()));
        });
    }

    private static void registerHoneyBottle(String name, CustomHoneyData data) {
        ModItems.HONEY_BOTTLE_ITEMS.register(name + "_honey_bottle", () -> new CustomHoneyBottleItem(data.getBottleData()));
    }

    private static void registerHoneyFluid(String name, CustomHoneyData data) {
        data.getOptionalData(CustomHoneyFluidData.SERIALIZER).ifPresent(fluidData -> {
            FluidData fluidD = ModFluidProperties.PROPERTIES.register(new HoneyFluidInformation(name + "_honey", fluidData.renderData(), fluidData.fluidAttributesData()));
            ModFluids.STILL_HONEY_FLUIDS.register(name + "_honey", () -> new CustomHoneyFluid.Source(fluidData, fluidD));
            ModFluids.FLOWING_HONEY_FLUIDS.register(name + "_honey_flowing", () -> new CustomHoneyFluid.Flowing(fluidData, fluidD));
            ModItems.HONEY_BUCKET_ITEMS.register(name + "_honey_bucket", () -> new CustomHoneyBucketItem(fluidD, fluidData));
            ModBlocks.HONEY_FLUID_BLOCKS.register(name + "_honey", () -> new CustomHoneyFluidBlock(fluidD, ModBlocks.HONEY_FLUID_BLOCK_PROPERTIES, fluidData));
        });
    }

    public static void registerDispenserBehaviors() {
        DispenserBlock.registerBehavior(ModItems.SCRAPER.get().asItem(), new ScraperDispenserBehavior());
    }
}
