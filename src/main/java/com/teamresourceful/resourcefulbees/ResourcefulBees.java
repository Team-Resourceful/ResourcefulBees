package com.teamresourceful.resourcefulbees;


import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.common.commands.ResourcefulBeesCommand;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.data.TagGenerator;
import com.teamresourceful.resourcefulbees.common.enchantments.HiveBreakHandler;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
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
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBiomeModifiers;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModConditions;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModIngredientTypes;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModStructures;
import com.teamresourceful.resourcefulbees.common.setup.DataSetup;
import com.teamresourceful.resourcefulbees.common.setup.GameSetup;
import com.teamresourceful.resourcefulbees.common.setup.MissingRegistrySetup;
import com.teamresourceful.resourcefulbees.common.setup.data.BeeSetup;
import com.teamresourceful.resourcefulbees.common.setup.data.HoneySetup;
import com.teamresourceful.resourcefulbees.common.setup.data.HoneycombSetup;
import com.teamresourceful.resourcefulbees.common.setup.data.TraitSetup;
import com.teamresourceful.resourcefulbees.common.world.gen.GoldenFlower;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ModConstants.MOD_ID)
public class ResourcefulBees {

    public ResourcefulBees(IEventBus modEventBus, ModContainer modContainer) {
        RegistryHandler.init();
        GameSetup.initEvents();
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

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onLoadingCompleted);
        modEventBus.addListener(GameSetup::registerAttributes);
        modEventBus.addListener(GameSetup::registerRepositorySources);
        modEventBus.addListener(GameSetup::registerCapabilities);
        modEventBus.addListener(GameSetup::initSpawns);
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
        //IngredientHelper.registerIngredient(BeeJarIngredient.SERIALIZER);
        GameSetup.initPotionRecipes();
        GameSetup.initArguments();
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        //if (event.getServer().isDedicatedServer()){

            BeeRegistry.getRegistry().regenerateCustomBeeData(event.getServer().registryAccess());
        //}
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

    //public static void onCommonSetup(CommonSetupEvent event) {
//        NetworkHandler.init();
//        RegistryHandler.registerDispenserBehaviors();
//        GameSetup.initPotionRecipes();
//        GameSetup.initArguments();
}
