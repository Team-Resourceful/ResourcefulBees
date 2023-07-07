package com.teamresourceful.resourcefulbees.common;

import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
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
import com.teamresourceful.resourcefulbees.platform.common.events.lifecycle.CommonSetupEvent;
import com.teamresourceful.resourcefulbees.platform.common.events.lifecycle.GameServerStartedEvent;
import com.teamresourceful.resourcefulbees.platform.common.events.lifecycle.LoadingCompletedEvent;
import com.teamresourceful.resourcefulbees.platform.common.util.ModUtils;
import com.teamresourceful.resourcefulconfig.common.config.Configurator;

public class ResourcefulBees {

    public static final Configurator CONFIGURATOR = new Configurator();

    public static void init() {
        CONFIGURATOR.registerConfig(GeneralConfig.class);
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
            CONFIGURATOR.saveConfig(GeneralConfig.class);
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
    }
}
