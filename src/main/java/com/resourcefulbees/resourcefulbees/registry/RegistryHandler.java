package com.resourcefulbees.resourcefulbees.registry;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.api.beedata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.block.CustomHoneyBlock;
import com.resourcefulbees.resourcefulbees.block.CustomHoneyFluidBlock;
import com.resourcefulbees.resourcefulbees.block.HoneycombBlock;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.effects.ModEffects;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.entity.passive.ResourcefulBee;
import com.resourcefulbees.resourcefulbees.fluids.HoneyFlowingFluid;
import com.resourcefulbees.resourcefulbees.fluids.HoneyFluidAttributes;
import com.resourcefulbees.resourcefulbees.item.BeeSpawnEggItem;
import com.resourcefulbees.resourcefulbees.item.CustomHoneyBottleItem;
import com.resourcefulbees.resourcefulbees.item.CustomHoneyBucketItem;
import com.resourcefulbees.resourcefulbees.item.HoneycombItem;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.utils.color.Color;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class RegistryHandler {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, ResourcefulBees.MOD_ID);

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(bus);
        ModEffects.EFFECTS.register(bus);
        ModBlocks.BLOCKS.register(bus);
        ModFluids.FLUIDS.register(bus);
        ENTITY_TYPES.register(bus);
        ModTileEntityTypes.TILE_ENTITY_TYPES.register(bus);
        ModPOIs.POIS.register(bus);
        ModPotions.POTIONS.register(bus);
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
                } else if (customBee.hasHoneycomb() && customBee.hasCustomDrop() && !customBee.isEasterEggBee()) {
                    fillCustomDrops(customBee);
                }
                registerBee(name, customBee);
            }
        });
    }

    public static void registerDynamicHoney() {
        BeeRegistry.getRegistry().getHoneyBottles().forEach((name, honeyData) -> {
            if (honeyData.shouldResourcefulBeesDoForgeRegistration) {
                registerHoneyBottle(name, honeyData);
            }
        });
    }

    private static Map<String, RegistryObject<FlowingFluid>> stillFluids = new HashMap<>();
    private static Map<String, RegistryObject<FlowingFluid>> flowingFluids = new HashMap<>();
    private static Map<String, RegistryObject<Item>> honeyBuckets = new HashMap<>();
    private static Map<String, RegistryObject<FlowingFluidBlock>> fluidBlocks = new HashMap<>();

    private static void registerHoneyBottle(String name, HoneyBottleData honeyData) {
        final RegistryObject<Item> customHoneyBottle = ModItems.ITEMS.register(name + "_honey_bottle", () -> new CustomHoneyBottleItem(honeyData.getProperties(), honeyData));

        honeyData.setHoneyBottleRegistryObject(customHoneyBottle);

        if (Config.HONEY_GENERATE_BLOCKS.get() && honeyData.doGenerateHoneyBlock()) {
            final RegistryObject<Block> customHoneyBlock = ModBlocks.BLOCKS.register(name + "_honey_block", () -> new CustomHoneyBlock(honeyData));
            final RegistryObject<Item> customHoneyBlockItem = ModItems.ITEMS.register(name + "_honey_block", () -> new BlockItem(customHoneyBlock.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));

            honeyData.setHoneyBlockRegistryObject(customHoneyBlock);
            honeyData.setHoneyBlockItemRegistryObject(customHoneyBlockItem);
        }

        if (Config.HONEY_GENERATE_FLUIDS.get() && honeyData.doGenerateHoneyFluid()) {
            stillFluids.put(name, ModFluids.FLUIDS.register(name + "_honey", () -> new HoneyFlowingFluid.Source(makeProperties(name, honeyData), honeyData)));
            flowingFluids.put(name, ModFluids.FLUIDS.register(name + "_honey_flowing", () -> new HoneyFlowingFluid.Flowing(makeProperties(name, honeyData), honeyData)));
            honeyBuckets.put(name, ModItems.ITEMS.register(name + "_honey_fluid_bucket", () -> new CustomHoneyBucketItem(stillFluids.get(name), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES).containerItem(Items.BUCKET).maxStackSize(1), honeyData)));
            fluidBlocks.put(name, ModBlocks.BLOCKS.register(name + "_honey", () -> new CustomHoneyFluidBlock(stillFluids.get(name), Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops(), honeyData)));

            honeyData.setHoneyStillFluidRegistryObject(stillFluids.get(name));
            honeyData.setHoneyFlowingFluidRegistryObject(flowingFluids.get(name));
            honeyData.setHoneyBucketItemRegistryObject(honeyBuckets.get(name));
            honeyData.setHoneyFluidBlockRegistryObject(fluidBlocks.get(name));
        }
    }

    private static ForgeFlowingFluid.Properties makeProperties(String name, HoneyBottleData honeyData) {
        HoneyFluidAttributes.Builder builder;
        builder = HoneyFluidAttributes.builder(ModFluids.CUSTOM_FLUID_STILL, ModFluids.CUSTOM_FLUID_FLOWING, honeyData);
        builder.overlay(ModFluids.CUSTOM_FLUID_OVERLAY);
        builder.sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY);
        builder.density(1300);
        builder.temperature(300);
        builder.viscosity(1800);

        return new ForgeFlowingFluid.Properties(stillFluids.get(name), flowingFluids.get(name), builder)
                .bucket(honeyBuckets.get(name))
                .block(fluidBlocks.get(name))
                .tickRate(20);
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
