package com.teamresourceful.resourcefulbees.common.registries;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.honey.CustomHoneyData;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.blocks.CustomHoneyBlock;
import com.teamresourceful.resourcefulbees.common.entities.CustomBeeEntityType;
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
import com.teamresourceful.resourcefullib.common.fluid.ResourcefulBucketItem;
import com.teamresourceful.resourcefullib.common.fluid.data.FluidData;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
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
        ModPOIs.POIS.init();
        ModPotions.POTIONS.init();
        ModVillagerProfessions.PROFESSIONS.init();
        ModFeatures.FEATURE_TYPES.init();
        ModDataComponents.COMPONENTS.init();
        ModDataComponentPredicates.PREDICATES.init();
        ModLootConditions.LOOT_CONDITIONS.init();
        ModLootFunctions.LOOT_FUNCTIONS.init();
    }

    //Dynamic|Iterative Registration Stuff below this line

    public static void registerResourcefulHives() {
        HiveType.values().forEach(hiveType -> {
            for (int i = 1; i < 5; i++) {
                String id = "nest/" + hiveType.type() + "/" + i;
                RegistryEntry<Block> block = ModBlocks.registerHive(id, i, hiveType.properties());

                switch (i) {
                    case 4 -> ModItems.registerHiveItem(ModItems.T4_NEST_ITEMS, id, block, i);
                    case 3 -> ModItems.registerHiveItem(ModItems.T3_NEST_ITEMS, id, block, i);
                    case 2 -> ModItems.registerHiveItem(ModItems.T2_NEST_ITEMS, id, block, i);
                    case 1 ->  {
                        ModItems.registerHiveItem(ModItems.T1_NEST_ITEMS, id, block, i);
                        hiveType.cacheTierOneNest(block);
                    }
                    default -> throw new IllegalStateException("Only 4 hive tiers exist, please report to github!");
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
        var bee = ModEntities.ENTITY_TYPES.register(
                name,
                () -> CustomBeeEntityType.<ResourcefulBee>of(
                        name,
                        (type, level) -> new ResourcefulBee(type, level, name),
                        0.7F * sizeModifier,
                        0.6F * sizeModifier
                )
        );

        ModEntities.addBee(name, bee);
        Identifier eggId = name.withSuffix("_spawn_egg");

        ModItems.SPAWN_EGG_ITEMS.register(
                eggId,
                () -> new SpawnEggItem(
                        new Item.Properties()
                                .setId(ResourceKey.create(Registries.ITEM, eggId))
                                .spawnEgg(bee.get())
                                .component(ModDataComponents.FALLBACK_ITEM_MODEL.get(), ModIdentifier.of("bee_spawn_egg"))
                )
        );
    }

    private static void registerCustomHoney(Identifier id, JsonObject honeyData) {
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

    private static void registerHoneyBlock(Identifier name, CustomHoneyData input) {
        input.getOptionalData(CustomHoneyBlockData.SERIALIZER)
                .ifPresent(data -> {
                    Identifier id = name.withSuffix("_honey_block");

                    var block = ModBlocks.HONEY_BLOCKS.register(id, () -> new CustomHoneyBlock(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, id)), data));

                    ModItems.HONEY_BLOCK_ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties()
                            .setId(ResourceKey.create(Registries.ITEM, id))
                            .useBlockDescriptionPrefix()
                            .component(ModDataComponents.FALLBACK_ITEM_MODEL.get(), ModIdentifier.of("honey_block"))));
                });
    }

    private static void registerHoneyBottle(Identifier name, CustomHoneyData data) {
        Identifier id = name.withSuffix("_honey_bottle");
        ModItems.HONEY_BOTTLE_ITEMS.register(id, () -> new CustomHoneyBottleItem(new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .component(ModDataComponents.FALLBACK_ITEM_MODEL.get(), ModIdentifier.of("honey_bottle")),
                data.getBottleData()));
    }

    private static void registerHoneyFluid(Identifier name, CustomHoneyData data) {
        data.getOptionalData(CustomHoneyFluidData.SERIALIZER)
                .ifPresent(fluidData -> {
                    Identifier typeId = name.withSuffix("_honey");
                    Identifier sourceId = name.withSuffix("_honey_fluid_source");
                    Identifier flowingId = name.withSuffix("_honey_fluid_flowing");
                    Identifier bucketId = name.withSuffix("_honey_bucket");
                    Identifier blockId = name.withSuffix("_honey_fluid_block");

                    var fluidType = ModFluids.FLUID_TYPES.register(typeId, CustomHoneyFluidType.create(fluidData.fluidAttributesData()));

                    FluidData fluidTypeData = fluidType.get();

                    ModFluids.STILL_HONEY_FLUIDS.register(sourceId, () -> new CustomHoneyFluid.Still(fluidData, fluidTypeData));
                    ModFluids.FLOWING_HONEY_FLUIDS.register(flowingId, () -> new CustomHoneyFluid.Flowing(fluidData, fluidTypeData));

                    ModItems.HONEY_BUCKET_ITEMS.register(bucketId, () -> new ResourcefulBucketItem(fluidTypeData, new Item.Properties()
                            .setId(ResourceKey.create(Registries.ITEM, bucketId))
                            .stacksTo(1)
                            .component(ModDataComponents.FALLBACK_ITEM_MODEL.get(), ModIdentifier.of("honey_bucket"))));
                    ModBlocks.registerHoneyFluidBlock(blockId, fluidData, fluidTypeData);
                });
    }

    public static void registerDispenserBehaviors() {
        DispenserBlock.registerBehavior(ModItems.SCRAPER.get().asItem(), new ScraperDispenserBehavior());
        DispenseItemBehavior vanillaShearsBehavior = new ShearsDispenseItemBehavior();
        DispenserBlock.registerBehavior(Items.SHEARS, new ShearsDispenserBehavior(vanillaShearsBehavior));
    }
}