package com.teamresourceful.resourcefulbees;


import com.mojang.logging.LogUtils;
import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.common.config.Config;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.data.DataGen;
import com.teamresourceful.resourcefulbees.common.items.BeeSpawnEggItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.defaults.DefaultApiaryTiers;
import com.teamresourceful.resourcefulbees.common.lib.defaults.DefaultBeehiveTiers;
import com.teamresourceful.resourcefulbees.common.lib.tools.ModValidation;
import com.teamresourceful.resourcefulbees.common.modcompat.base.ModCompatHelper;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefulbees.common.registries.RegistryHandler;
import com.teamresourceful.resourcefulbees.common.registries.custom.*;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.setup.DataSetup;
import com.teamresourceful.resourcefulbees.common.setup.GameSetup;
import com.teamresourceful.resourcefulbees.common.setup.MissingRegistrySetup;
import com.teamresourceful.resourcefulbees.common.setup.data.BeeSetup;
import com.teamresourceful.resourcefulbees.common.setup.data.HoneySetup;
import com.teamresourceful.resourcefulbees.common.setup.data.HoneycombSetup;
import com.teamresourceful.resourcefulbees.common.setup.data.TraitSetup;
import com.teamresourceful.resourcefulbees.mixin.common.SpawnEggItemAccessor;
import com.teamresourceful.resourcefulbees.platform.common.events.RegisterIngredientsEvent;
import com.teamresourceful.resourcefulbees.platform.common.events.lifecycle.CommonSetupEvent;
import com.teamresourceful.resourcefulbees.platform.common.events.lifecycle.GameServerStartedEvent;
import com.teamresourceful.resourcefulbees.platform.common.events.lifecycle.LoadingCompletedEvent;
import com.teamresourceful.resourcefulbees.platform.common.util.ModUtils;
import com.teamresourceful.resourcefullib.common.recipe.ingredient.IngredientHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ModConstants.MOD_ID)
public class ResourcefulBees {

    //public static final Configurator CONFIGURATOR = Configurator.initialize();

























    // Define mod id in a common place for everything to reference
    public static final String MODID = "resourcefulbees";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "resourcefulbees" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "resourcefulbees" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "resourcefulbees" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a new Block with the id "resourcefulbees:example_block", combining the namespace and path
    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    // Creates a new BlockItem with the id "resourcefulbees:example_block", combining the namespace and path
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

    // Creates a new food item with the id "resourcefulbees:example_id", nutrition 1 and saturation 2
    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    // Creates a creative tab with the id "resourcefulbees:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.resourcefulbees")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public ResourcefulBees(IEventBus modEventBus, ModContainer modContainer) {
        //CONFIGURATOR.registerConfig(GeneralConfig.class);
        ModConstants.forceInit();
        RegistryHandler.init();
        GameSetup.initEvents();
        DefaultBeehiveTiers.loadDefaults();
        DefaultApiaryTiers.loadDefaults();
        DataSetup.setupInitializers(ResourcefulBeesAPI.getInitializers());
        DataSetup.setupInitializers(ResourcefulBeesAPI.getHoneyInitalizers());
        ResourcefulBeesAPI.getEvents().registerCondition(DataSetup::setupRegister);
        BeeDataRegistry.init();
        HoneyDataRegistry.init();
        LoadConditionRegistry.init();
        //GeckoLib.initialize();
        GameSetup.initPaths();
        ModCompatHelper.registerCompats();

        ResourcefulBeesAPI.getRegistry().setBeeRegistry(BeeRegistry.getRegistry());
        ResourcefulBeesAPI.getRegistry().setTraitRegistry(TraitRegistry.getRegistry());
        ResourcefulBeesAPI.getRegistry().setTraitAbilityRegistry(DefaultTraitAbilities.registerDefaultAbilities(TraitAbilityRegistry.getRegistry()));
        ResourcefulBeesAPI.getRegistry().setHoneycombRegistry(HoneycombRegistry.getRegistry());
        ResourcefulBeesAPI.getRegistry().setHoneyRegistry(HoneyRegistry.getRegistry());

        HoneycombSetup.setupHoneycombs();
        BeeSetup.setupBees();
        RegistryHandler.registerDynamicBees();
        HoneySetup.setupHoney();
        RegistryHandler.registerDynamicHoney();

        ModValidation.init();
        GameSetup.init();

        GameServerStartedEvent.EVENT.addListener(ResourcefulBees::onServerStarted);
        LoadingCompletedEvent.EVENT.addListener(ResourcefulBees::onLoadingCompleted);
        CommonSetupEvent.EVENT.addListener(ResourcefulBees::onCommonSetup);




        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ResourcefulBees) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(EXAMPLE_BLOCK_ITEM);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    public static void onServerStarted(GameServerStartedEvent event) {
        if (event.server().isDedicatedServer()){
            BeeRegistry.getRegistry().regenerateCustomBeeData(event.server().registryAccess());
        }
    }

    public static void onLoadingCompleted(LoadingCompletedEvent event) {
        TraitAbilityRegistry.getRegistry().close();
        TraitSetup.buildCustomTraits();
        TraitRegistry.getRegistry().close();
        DataGen.generateCommonData();
        HoneycombRegistry.getRegistry().regenerateVariationData();
        MissingRegistrySetup.checkMissingRegistries();
        if (ModUtils.isProduction()) {
            GeneralConfig.generateDefaults = false;
            //CONFIGURATOR.saveConfig(GeneralConfig.class);
        }
    }

    public static void onCommonSetup(CommonSetupEvent event) {
        NetworkHandler.init();
        RegistryHandler.registerDispenserBehaviors();
        GameSetup.initPotionRecipes();
        GameSetup.initArguments();

        ModItems.SPAWN_EGG_ITEMS.boundStream()
                .filter(item -> item instanceof BeeSpawnEggItem)
                .map(item -> (BeeSpawnEggItem) item)
                .forEach(egg -> {
                    egg.registerDispenserBehavior();
                    SpawnEggItemAccessor.getById().put(egg.getDefaultType(), egg);
                });

        SpawnEggItemAccessor.getById().remove(null);

        RegisterIngredientsEvent.EVENT.fire(new RegisterIngredientsEvent(IngredientHelper::registerIngredient));
    }
}
