package com.teamresourceful.resourcefulbees;


import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.common.commands.ResourcefulBeesCommand;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.data.TagGenerator;
import com.teamresourceful.resourcefulbees.common.effect.BeekeepersResolveEffect;
import com.teamresourceful.resourcefulbees.common.enchantments.HiveBreakHandler;
import com.teamresourceful.resourcefulbees.common.items.locator.DimensionalBeeHolder;
import com.teamresourceful.resourcefulbees.common.lib.constants.BreederConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.FlowHiveTranslations;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.ItemTranslations;
import com.teamresourceful.resourcefulbees.common.lib.defaults.*;
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
import com.teamresourceful.resourcefulbees.common.setup.data.*;
import com.teamresourceful.resourcefulbees.common.world.workers.LevelWorkEvents;
import com.teamresourceful.resourcefulbees.gametest.ModGameTest;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.tooltip.TooltipLocation;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterTooltipAppendersEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ModConstants.MOD_ID)
public class ResourcefulBees {

    public ResourcefulBees(IEventBus modEventBus) {
        RegistryHandler.init();
        //load default data
        DefaultHiveTypes.loadDefaults();
        DefaultBeehiveTiers.loadDefaults();
        DefaultApiaryTiers.loadDefaults();
        //setup initializers
        DataSetup.setupInitializers(ResourcefulBeesAPI.getInitializers());
        DataSetup.setupInitializers(ResourcefulBeesAPI.getHoneyInitializers());
        BeeDataRegistry.init();
        HoneyDataRegistry.init();
        GameSetup.initPaths();
        ModCompatHelper.registerCompatibleMods();

        ResourcefulBeesAPI.getRegistry().setBeeRegistry(BeeRegistry.getRegistry());
        ResourcefulBeesAPI.getRegistry().setTraitRegistry(TraitRegistry.getRegistry());
        ResourcefulBeesAPI.getRegistry().setTraitAbilityRegistry(DefaultTraitAbilities.registerDefaultAbilities(TraitAbilityRegistry.getRegistry()));
        ResourcefulBeesAPI.getRegistry().setHoneycombRegistry(HoneycombRegistry.getRegistry());
        ResourcefulBeesAPI.getRegistry().setHoneyRegistry(HoneyRegistry.getRegistry());


        RegistryHandler.registerResourcefulHives();
        BeeSetup.setupBees();
        RegistryHandler.registerDynamicBees();
        HoneySetup.setupHoney();
        RegistryHandler.registerDynamicHoney();

        ModValidation.init();

        NeoForge.EVENT_BUS.addListener(ResourcefulBeesCommand::registerCommand);
        NeoForge.EVENT_BUS.addListener(ModSpawnData::initialize);
        NeoForge.EVENT_BUS.addListener(ModStructures::onServerAboutToStart);
        NeoForge.EVENT_BUS.addListener(HiveBreakHandler::onBlockDrops);
        NeoForge.EVENT_BUS.addListener(DimensionalBeeHolder::onDatapackSync);
        NeoForge.EVENT_BUS.addListener(LevelWorkEvents::onLevelTick);
        NeoForge.EVENT_BUS.addListener(BeekeepersResolveEffect::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(BeekeepersResolveEffect::invulnerabilityEvent);
        NeoForge.EVENT_BUS.addListener(this::registerTraitReloadListener);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::onConstructMod);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onLoadingCompleted);
        modEventBus.addListener(GameSetup::registerAttributes);
        modEventBus.addListener(GameSetup::registerRepositorySources);
        modEventBus.addListener(GameSetup::registerCapabilities);
        modEventBus.addListener(GameSetup::initSpawns);
        modEventBus.addListener(this::addTooltipsProviders);
        modEventBus.addListener(this::addBlockEntityBlocks);
        ModIngredientTypes.register(modEventBus);
        ModBiomeModifiers.init(modEventBus);
        ModAttachments.ATTACHMENTS.register(modEventBus);

        ModGameTest.register(modEventBus);
        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ResourcefulBees) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);
    }

    private void registerTraitReloadListener(AddServerReloadListenersEvent event) {
        event.addListener(ModIdentifier.of("traits"), new TraitReloadListener());
    }

    private void onConstructMod(FMLConstructModEvent event) {
        event.enqueueWork(() -> {
            HoneycombSetup.setupHoneycombs();
            HoneycombRegistry.registerJavaHoneycombs();
        });
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        NetworkHandler.init();
        event.enqueueWork(RegistryHandler::registerDispenserBehaviors);
        GameSetup.initArguments();
    }

    private void onLoadingCompleted(FMLLoadCompleteEvent event) {
        DefaultTraitAbilityTypes.registerDefaults();
        TraitSetup.prepareTraitResources();
        TagGenerator.generateCommonData();
        HoneycombRegistry.getRegistry().regenerateVariationData();
        MissingRegistrySetup.checkMissingRegistries();
        if (ModUtils.isProduction()) {
            GeneralConfig.generateDefaults = false;
        }
    }

    public void addBlockEntityBlocks(BlockEntityTypeAddBlocksEvent event) {
        event.modify(BlockEntityTypes.SIGN, ModBlocks.WAXED_SIGN.get(), ModBlocks.WAXED_WALL_SIGN.get());
        event.modify(BlockEntityTypes.HANGING_SIGN, ModBlocks.WAXED_HANGING_SIGN.get(), ModBlocks.WAXED_WALL_HANGING_SIGN.get());
        event.modify(BlockEntityTypes.SHELF, ModBlocks.WAXED_SHELF.get());
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

        event.registerAppender(
                TooltipLocation.HEAD,
                (stack, context, display, player, tooltipFlag, builder) ->
                        stack.addToTooltip(ModDataComponents.POLLEN_RECIPE, context, display, builder, tooltipFlag)
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
