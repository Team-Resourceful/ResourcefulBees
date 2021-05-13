package com.resourcefulbees.resourcefulbees.registry;

import com.google.gson.JsonObject;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.HoneycombData;
import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.block.ColoredHoneyBlock;
import com.resourcefulbees.resourcefulbees.block.CustomHoneyFluidBlock;
import com.resourcefulbees.resourcefulbees.block.HoneycombBlock;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.entity.passive.ResourcefulBee;
import com.resourcefulbees.resourcefulbees.fluids.HoneyFlowingFluid;
import com.resourcefulbees.resourcefulbees.fluids.HoneyFluidAttributes;
import com.resourcefulbees.resourcefulbees.item.BeeSpawnEggItem;
import com.resourcefulbees.resourcefulbees.item.CustomHoneyBottleItem;
import com.resourcefulbees.resourcefulbees.item.CustomHoneyBucketItem;
import com.resourcefulbees.resourcefulbees.item.HoneycombItem;
import com.resourcefulbees.resourcefulbees.lib.HoneycombTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
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
        ModEntities.getModBees().forEach((s, customBee) -> event.put(customBee.get(), CustomBeeEntity.createBeeAttributes(s).build()));
    }

    public static void registerDynamicBees() {
        BeeRegistry.getRegistry().getBees().forEach((name, beeData) -> {
            HoneycombData honeyData = beeData.getHoneycombData();
            if (honeyData.getHoneycombType().equals(HoneycombTypes.DEFAULT)) {
                registerHoneycomb(name, honeyData);
            }
            registerBee(name,beeData.getRawData(), beeData.getRenderData().getSizeModifier());
        });
    }

    public static void registerDynamicHoney() {
        BeeRegistry.getRegistry().getHoneyBottles().forEach((name, honeyData) -> {
            if (honeyData.shouldResourcefulBeesDoForgeRegistration()) {
                registerHoneyBottle(name, honeyData);
            }
        });
    }

    private static final Map<String, RegistryObject<FlowingFluid>> stillFluids = new HashMap<>();
    private static final Map<String, RegistryObject<FlowingFluid>> flowingFluids = new HashMap<>();
    private static final Map<String, RegistryObject<Item>> honeyBuckets = new HashMap<>();
    private static final Map<String, RegistryObject<LiquidBlock>> fluidBlocks = new HashMap<>();

    private static void registerHoneyBottle(String name, HoneyBottleData honeyData) {
        final RegistryObject<Item> customHoneyBottle = ModItems.ITEMS.register(name + "_honey_bottle", () -> new CustomHoneyBottleItem(honeyData.getProperties(), honeyData));

        honeyData.setHoneyBottleRegistryObject(customHoneyBottle);

        if (Config.HONEY_GENERATE_BLOCKS.get() && honeyData.doGenerateHoneyBlock()) {
            final RegistryObject<Block> customHoneyBlock = ModBlocks.BLOCKS.register(name + "_honey_block", () -> new ColoredHoneyBlock(honeyData));
            final RegistryObject<Item> customHoneyBlockItem = ModItems.ITEMS.register(name + "_honey_block", () -> new BlockItem(customHoneyBlock.get(), new Item.Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));

            honeyData.setHoneyBlockRegistryObject(customHoneyBlock);
            honeyData.setHoneyBlockItemRegistryObject(customHoneyBlockItem);
        }

        if (Config.HONEY_GENERATE_FLUIDS.get() && honeyData.doGenerateHoneyFluid()) {
            stillFluids.put(name, ModFluids.FLUIDS.register(name + "_honey", () -> new HoneyFlowingFluid.Source(makeProperties(name, honeyData), honeyData)));
            flowingFluids.put(name, ModFluids.FLUIDS.register(name + "_honey_flowing", () -> new HoneyFlowingFluid.Flowing(makeProperties(name, honeyData), honeyData)));
            honeyBuckets.put(name, ModItems.ITEMS.register(name + "_honey_fluid_bucket", () -> new CustomHoneyBucketItem(stillFluids.get(name), new Item.Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES).craftRemainder(Items.BUCKET).stacksTo(1), honeyData)));
            fluidBlocks.put(name, ModBlocks.BLOCKS.register(name + "_honey", () -> new CustomHoneyFluidBlock(stillFluids.get(name), BlockBehaviour.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops(), honeyData)));

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
        builder.sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY);
        builder.density(1300);
        builder.temperature(300);
        builder.viscosity(1800);

        return new ForgeFlowingFluid.Properties(stillFluids.get(name), flowingFluids.get(name), builder)
                .bucket(honeyBuckets.get(name))
                .block(fluidBlocks.get(name))
                .tickRate(20);
    }

    private static void registerHoneycomb(String name, HoneycombData honeycombData) {
        final RegistryObject<Block> customHoneycombBlock = ModBlocks.BLOCKS.register(name + "_honeycomb_block", () -> new HoneycombBlock(name, honeycombData, BlockBehaviour.Properties.copy(Blocks.HONEYCOMB_BLOCK)));
        ModItems.ITEMS.register(name + "_honeycomb", () -> new HoneycombItem(name, honeycombData, new Item.Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
        ModItems.ITEMS.register(name + "_honeycomb_block", () -> new BlockItem(customHoneycombBlock.get(), new Item.Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
    }

    private static void registerBee(String name, JsonObject beeData, float sizeModifier) {
        final RegistryObject<EntityType<? extends CustomBeeEntity>> customBeeEntity = ENTITY_TYPES.register(name + "_bee", () -> EntityType.Builder
                .<ResourcefulBee>of((type, world) -> new ResourcefulBee(type, world, name, beeData), MobCategory.CREATURE)
                .sized(0.7F * sizeModifier, 0.6F * sizeModifier)
                .build(name + "_bee"));

        ModItems.ITEMS.register(name + "_bee_spawn_egg",
                () -> new BeeSpawnEggItem(customBeeEntity, 0xffcc33, 0x303030, name, new Item.Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));

        ModEntities.getModBees().put(name, customBeeEntity);
    }

    @SubscribeEvent
    public void onRegisterBlocks(RegistryEvent.Register<Block> event) {
        //Exists to disallow further bee registrations
        //preventing potential for NPE's
        BeeRegistry.getRegistry().denyRegistration();
    }
}
