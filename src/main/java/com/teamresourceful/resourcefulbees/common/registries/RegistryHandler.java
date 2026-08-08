package com.teamresourceful.resourcefulbees.common.registries;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.honey.CustomHoneyData;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.client.fluids.CustomHoneyClientFluidProperties;
import com.teamresourceful.resourcefulbees.common.blocks.CustomHoneyBlock;
import com.teamresourceful.resourcefulbees.common.entities.CustomBeeEntityType;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.fluids.CustomHoneyFluid;
import com.teamresourceful.resourcefulbees.common.fluids.CustomHoneyFluidType;
import com.teamresourceful.resourcefulbees.common.items.dispenser.ScraperDispenserBehavior;
import com.teamresourceful.resourcefulbees.common.items.dispenser.ShearsDispenserBehavior;
import com.teamresourceful.resourcefulbees.common.items.honey.CustomHoneyBottleItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.records.HiveType;
import com.teamresourceful.resourcefulbees.common.registries.custom.HoneyDataRegistry;
import com.teamresourceful.resourcefulbees.common.registries.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.*;
import com.teamresourceful.resourcefulbees.common.setup.data.honeydata.CustomHoneyBlockData;
import com.teamresourceful.resourcefulbees.common.setup.data.honeydata.fluid.CustomHoneyFluidData;
import com.teamresourceful.resourcefullib.common.codecs.maps.DispatchMapCodec;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.fluid.ResourcefulFlowingFluid;
import com.teamresourceful.resourcefullib.common.fluid.data.FluidData;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;


public final class RegistryHandler {

    private RegistryHandler() throws UtilityClassException {
        throw new UtilityClassException();
    }


    public static void init() {
        ModCreativeTabs.CREATIVE_TABS.init();
        ModFluids.FLUIDS.init();
        ModFluids.FLUID_TYPES.init();
        ModEntities.ENTITY_TYPES.init();
        ModBlocks.BLOCKS.init();
        ModItems.ITEMS.init();
        ModBlockEntityTypes.BLOCK_ENTITY_TYPES.init();
        ModRecipes.RECIPE_TYPES.init();
        ModRecipeSerializers.RECIPE_SERIALIZERS.init();
        ModMenuTypes.MENUS.init();
        ModEffects.EFFECTS.init();
        ModArguments.ARGUMENTS.init();
        //ModEnchantments.ENCHANTMENTS.init();
        ModPOIs.POIS.init();
        ModPotions.POTIONS.init();
        ModVillagerProfessions.PROFESSIONS.init();
        ModFeatures.FEATURES.init();
        ModDataComponents.COMPONENTS.init();
    }

    //Dynamic|Iterative Registration Stuff below this line

    public static void registerResourcefulHives() {
        HiveType.values().forEach(hiveType -> {
            for (int i = 1; i < 5; i++) {
                String id = "nest/" + hiveType.type() + "/" + i;
                RegistryEntry<Block> block = ModBlocks.registerHive(id, i, hiveType.properties());
                switch (i) {
                    case 4 -> ModItems.registerHiveItem(ModItems.T4_NEST_ITEMS, id, block);
                    case 3 -> ModItems.registerHiveItem(ModItems.T3_NEST_ITEMS, id, block);
                    case 2 -> ModItems.registerHiveItem(ModItems.T2_NEST_ITEMS, id, block);
                    default -> ModItems.registerHiveItem(ModItems.T1_NEST_ITEMS, id, block);
                }
            }
        });
    }

    public static void registerDynamicBees() {
        BeeRegistry.get().getBees().forEach((name, beeData) -> registerBee(name, beeData.getRenderData().sizeModifier()));
    }

    public static void registerDynamicHoney() {
        HoneyRegistry.getRegistry().getRawHoney().forEach(RegistryHandler::registerCustomHoney);
    }

    private static void registerBee(Identifier name, float sizeModifier) {
        RegistryEntry<EntityType<? extends CustomBeeEntity>> beeEntityType = ModEntities.BEES.register(
                name.getPath(),
                () -> CustomBeeEntityType.of(
                        name,
                        (type, level) -> new ResourcefulBee(type, level, name),
                        0.7F * sizeModifier,
                        0.6F * sizeModifier
                )
        );
        ModItems.SPAWN_EGG_ITEMS.register(name.getPath() + "_spawn_egg", SpawnEggItem::new, () -> new Item.Properties().spawnEgg(beeEntityType.get()).component(ModDataComponents.FALLBACK_ITEM_MODEL.get(), ModIdentifier.of("bee_spawn_egg")));
        ModEntities.getModBees().put(name, beeEntityType);
    }

    private static void registerCustomHoney(String id, JsonObject honeyData) {
        var data = new DispatchMapCodec<>(Identifier.CODEC, HoneyDataRegistry.codec(id))
                .parse(JsonOps.INSTANCE, honeyData)
                .resultOrPartial(message -> ModConstants.LOGGER.error("Could not create Honey Data for {} honey: {}", id, message))
                .orElseThrow(() -> new IllegalStateException("Failed to parse honey data for '" + id + "'"));
        try {
            HoneyDataRegistry.INSTANCE.check(data.values());
        } catch (Exception e) {
            ModConstants.LOGGER.error("Could not create Honey Data for {} honey", id);
            throw e;
        }
        CustomHoneyData customHoneyData = ResourcefulBeesAPI.getHoneyInitializers().data(id, data);
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
            RegistryEntry<Block> block = ModBlocks.HONEY_BLOCKS.register(
                    name + "_honey_block",
                    properties -> new CustomHoneyBlock(properties, data),
                    BlockBehaviour.Properties::of
            );
            ModItems.HONEY_BLOCK_ITEMS.register(
                    name + "_honey_block",
                    properties -> new BlockItem(block.get(), properties),
                    () -> new Item.Properties().component(ModDataComponents.FALLBACK_ITEM_MODEL.get(), ModIdentifier.of("honey_block"))
            );
        });
    }

    private static void registerHoneyBottle(String name, CustomHoneyData data) {
        ModItems.HONEY_BOTTLE_ITEMS.register(
                name + "_honey_bottle",
                properties -> new CustomHoneyBottleItem(properties, data.getBottleData()),
                () -> new Item.Properties().component(ModDataComponents.FALLBACK_ITEM_MODEL.get(), ModIdentifier.of("honey_bottle"))
        );
    }

    private static void registerHoneyFluid(String name, CustomHoneyData data) {
        data.getOptionalData(CustomHoneyFluidData.SERIALIZER).ifPresent(fluidData -> {
            RegistryEntry<FluidData> fluidType = ModFluids.FLUID_TYPES.register(name + "_honey", CustomHoneyFluidType.create(fluidData.fluidAttributesData()));
            ModFluids.STILL_HONEY_FLUIDS.register(name + "_honey_fluid_source", () -> new CustomHoneyFluid.Still(fluidData, fluidType.get()));
            ModFluids.FLOWING_HONEY_FLUIDS.register(name + "_honey_fluid_flowing", () -> new CustomHoneyFluid.Flowing(fluidData, fluidType.get()));
            ModFluids.CLIENT_FLUID_PROPERTIES.register(name + "_honey", () -> CustomHoneyClientFluidProperties.create(fluidData.renderData()));
            ModItems.HONEY_BUCKET_ITEMS.register(name + "_honey_bucket", properties -> new BucketItem(fluidType.get().still().get(), properties), () -> new Item.Properties().stacksTo(1).component(ModDataComponents.FALLBACK_ITEM_MODEL.get(), ModIdentifier.of("honey_bucket")));
            ModBlocks.registerHoneyFluidBlock(name + "_honey_fluid_block", fluidData, fluidType.get());
        });
    }

    public static void registerDispenserBehaviors() {
        DispenserBlock.registerBehavior(ModItems.SCRAPER.get().asItem(), new ScraperDispenserBehavior());
        DispenseItemBehavior vanillaShearsBehavior = new ShearsDispenseItemBehavior();
        DispenserBlock.registerBehavior(Items.SHEARS, new ShearsDispenserBehavior(vanillaShearsBehavior));
    }
}