package com.teamresourceful.resourcefulbees;


import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.common.commands.ResourcefulBeesCommand;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.data.TagGenerator;
import com.teamresourceful.resourcefulbees.common.enchantments.HiveBreakHandler;
import com.teamresourceful.resourcefulbees.common.items.locator.DimensionalBeeHolder;
import com.teamresourceful.resourcefulbees.common.lib.constants.BreederConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.FlowHiveTranslations;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.ItemTranslations;
import com.teamresourceful.resourcefulbees.common.lib.defaults.DefaultApiaryTiers;
import com.teamresourceful.resourcefulbees.common.lib.defaults.DefaultBeehiveTiers;
import com.teamresourceful.resourcefulbees.common.lib.defaults.DefaultHiveTypes;
import com.teamresourceful.resourcefulbees.common.lib.tools.ModValidation;
import com.teamresourceful.resourcefulbees.common.lib.util.ModUtils;
import com.teamresourceful.resourcefulbees.common.modcompat.base.ModCompatHelper;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefulbees.common.registries.RegistryHandler;
import com.teamresourceful.resourcefulbees.common.registries.custom.*;
import com.teamresourceful.resourcefulbees.common.registries.dynamic.ModSpawnData;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.*;
import com.teamresourceful.resourcefulbees.common.setup.DataSetup;
import com.teamresourceful.resourcefulbees.common.setup.GameSetup;
import com.teamresourceful.resourcefulbees.common.setup.MissingRegistrySetup;
import com.teamresourceful.resourcefulbees.common.setup.data.BeeSetup;
import com.teamresourceful.resourcefulbees.common.setup.data.HoneySetup;
import com.teamresourceful.resourcefulbees.common.setup.data.HoneycombSetup;
import com.teamresourceful.resourcefulbees.common.setup.data.TraitSetup;
import com.teamresourceful.resourcefulbees.common.world.gen.GoldenFlower;
import com.teamresourceful.resourcefulbees.common.world.workers.LevelWorkEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.tooltip.TooltipLocation;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterTooltipAppendersEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ModConstants.MOD_ID)
public class ResourcefulBees {

    public ResourcefulBees(IEventBus modEventBus, ModContainer modContainer) {
        RegistryHandler.init();
        //load default data
        DefaultHiveTypes.loadDefaults();
        DefaultBeehiveTiers.loadDefaults();
        DefaultApiaryTiers.loadDefaults();
        //setup initializers
        DataSetup.setupInitializers(ResourcefulBeesAPI.getInitializers());
        DataSetup.setupInitializers(ResourcefulBeesAPI.getHoneyInitializers());
        ResourcefulBeesAPI.getEvents().registerCondition(DataSetup::setupRegister);
        BeeDataRegistry.init();
        HoneyDataRegistry.init();
        LoadConditionRegistry.init();
        GameSetup.initPaths();
        ModCompatHelper.registerCompats();

        ResourcefulBeesAPI.getRegistry().setBeeRegistry(BeeRegistry.getRegistry());
        ResourcefulBeesAPI.getRegistry().setTraitRegistry(TraitRegistry.getRegistry());
        ResourcefulBeesAPI.getRegistry().setTraitAbilityRegistry(DefaultTraitAbilities.registerDefaultAbilities(TraitAbilityRegistry.getRegistry()));
        ResourcefulBeesAPI.getRegistry().setHoneycombRegistry(HoneycombRegistry.getRegistry());
        ResourcefulBeesAPI.getRegistry().setHoneyRegistry(HoneyRegistry.getRegistry());


        RegistryHandler.registerResourcefulHives();
        HoneycombSetup.setupHoneycombs();
        BeeSetup.setupBees();
        RegistryHandler.registerDynamicBees();
        HoneySetup.setupHoney();
        RegistryHandler.registerDynamicHoney();

        ModValidation.init();

        NeoForge.EVENT_BUS.addListener(ResourcefulBeesCommand::registerCommand);
        NeoForge.EVENT_BUS.addListener(ModSpawnData::initialize);
        NeoForge.EVENT_BUS.addListener(GoldenFlower::onBonemeal);
        NeoForge.EVENT_BUS.addListener(ModStructures::onServerAboutToStart);
        NeoForge.EVENT_BUS.addListener(HiveBreakHandler::onBlockDrops);
        NeoForge.EVENT_BUS.addListener(ModBrewingRecipes::register);
        NeoForge.EVENT_BUS.addListener(DimensionalBeeHolder::onDatapackSync);
        NeoForge.EVENT_BUS.addListener(LevelWorkEvents::onLevelTick);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onLoadingCompleted);
        modEventBus.addListener(GameSetup::registerAttributes);
        modEventBus.addListener(GameSetup::registerRepositorySources);
        modEventBus.addListener(GameSetup::registerCapabilities);
        modEventBus.addListener(GameSetup::initSpawns);
        modEventBus.addListener(this::addTooltipsProviders);
        ModIngredientTypes.register(modEventBus);
        ModBiomeModifiers.init(modEventBus);
        ModConditions.init(modEventBus);


        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ResourcefulBees) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        NetworkHandler.init();
        event.enqueueWork(RegistryHandler::registerDispenserBehaviors);
        GameSetup.initArguments();
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        BeeRegistry.getRegistry().regenerateCustomBeeData(event.getServer().registryAccess());
    }

    private void onLoadingCompleted(FMLLoadCompleteEvent event) {
        TraitAbilityRegistry.getRegistry().close();
        TraitSetup.buildCustomTraits();
        TraitRegistry.getRegistry().close();
        TagGenerator.generateCommonData();
        HoneycombRegistry.getRegistry().regenerateVariationData();
        MissingRegistrySetup.checkMissingRegistries();
        if (ModUtils.isProduction()) {
            GeneralConfig.generateDefaults = false;
        }
    }

    @SubscribeEvent
    public void syncRecipes(OnDatapackSyncEvent event) {
        event.sendRecipes(ModRecipes.HIVE_RECIPE_TYPE.get());
        event.sendRecipes(ModRecipes.BREEDER_RECIPE_TYPE.get());
        event.sendRecipes(ModRecipes.CENTRIFUGE_RECIPE_TYPE.get());
        event.sendRecipes(ModRecipes.SOLIDIFICATION_RECIPE_TYPE.get());
        event.sendRecipes(ModRecipes.HONEY_GEN_RECIPE_TYPE.get());
        event.sendRecipes(ModRecipes.FLOW_HIVE_RECIPE_TYPE.get());
        event.sendRecipes(ModRecipes.MUTATION_RECIPE_TYPE.get());
    }

    public void addTooltipsProviders(RegisterTooltipAppendersEvent event) {
        event.registerComponentAppenderAfterAll(
                ModDataComponents.BEE_BOX_OCCUPANTS,
                (stack, context, display, player, flag, consumer) ->
                        stack.addToTooltip(ModDataComponents.BEE_BOX_OCCUPANTS, context, display, consumer, flag)
        );

        event.registerComponentAppenderAfterAll(
                ModDataComponents.APIARY_BEES,
                (stack, context, display, player, flag, consumer) ->
                        stack.addToTooltip(ModDataComponents.APIARY_BEES, context, display, consumer, flag)
        );

        event.registerComponentAppenderAfterAll(
                ModDataComponents.HIVE_BEES,
                (stack, context, display, player, flag, consumer) ->
                        stack.addToTooltip(ModDataComponents.HIVE_BEES, context, display, consumer, flag)
        );

        event.registerComponentAppenderAfterAll(
                ModDataComponents.BEEHIVE_TIER,
                (stack, context, display, player, flag, consumer) ->
                        stack.addToTooltip(ModDataComponents.BEEHIVE_TIER, context, display, consumer, flag)
        );

        event.registerComponentAppenderAfterAll(
                ModDataComponents.APIARY_TIER,
                (stack, context, display, player, flag, consumer) ->
                        stack.addToTooltip(ModDataComponents.APIARY_TIER, context, display, consumer, flag)
        );
        
        event.registerAppender(TooltipLocation.POST_CUSTOM, (stack, context, display, player, tooltipFlag, builder) -> {
            if (stack.is(ModItems.SMOKER.get())) {
                builder.accept(ItemTranslations.SMOKER_TOOLTIP.withStyle(ChatFormatting.GOLD));
                builder.accept(ItemTranslations.SMOKER_TOOLTIP1.withStyle(ChatFormatting.GOLD));
            } else if (stack.is(ModItems.SCRAPER.get())) {
                builder.accept(ItemTranslations.SCRAPER_TOOLTIP.withStyle(ChatFormatting.GOLD));
                builder.accept(ItemTranslations.SCRAPER_TOOLTIP_1.withStyle(ChatFormatting.GOLD));
            } else if (stack.is(ModItems.BREEDER_ITEM.get())) {
                builder.accept(Component.translatable(ItemTranslations.BREEDER_TOOLTIP_1, BreederConstants.DEFAULT_BREEDER_TIME).withStyle(ChatFormatting.GOLD));
                builder.accept(ItemTranslations.BREEDER_TOOLTIP_2.withStyle(ChatFormatting.GOLD));
            } else if (stack.is(ModItems.ENDER_BEECON_ITEM.get())) {
                builder.accept(ItemTranslations.BEECON_TOOLTIP.withStyle(ChatFormatting.LIGHT_PURPLE));
                builder.accept(ItemTranslations.BEECON_TOOLTIP_1.withStyle(ChatFormatting.LIGHT_PURPLE));
            } else if (stack.is(ModItems.FLOW_HIVE.get())) {
                builder.accept(FlowHiveTranslations.INFO.withStyle(ChatFormatting.GOLD));
                builder.accept(FlowHiveTranslations.HARVEST.withStyle(ChatFormatting.GOLD));
                builder.accept(FlowHiveTranslations.CAPACITY.withStyle(ChatFormatting.GOLD));
            } else if (stack.is(ModItems.HONEY_GENERATOR_ITEM.get())) {
                builder.accept(ItemTranslations.GEN_TOOLTIP.withStyle(ChatFormatting.GOLD));
            }
        });


    }
}
