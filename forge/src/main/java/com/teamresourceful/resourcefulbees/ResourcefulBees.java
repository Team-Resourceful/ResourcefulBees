package com.teamresourceful.resourcefulbees;

import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.client.data.LangGeneration;
import com.teamresourceful.resourcefulbees.client.event.ClientEventHandlers;
import com.teamresourceful.resourcefulbees.client.pets.PetLoader;
import com.teamresourceful.resourcefulbees.common.compat.top.TopCompat;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.data.DataGen;
import com.teamresourceful.resourcefulbees.common.data.DataPackLoader;
import com.teamresourceful.resourcefulbees.common.data.DataSetup;
import com.teamresourceful.resourcefulbees.common.data.RecipeBuilder;
import com.teamresourceful.resourcefulbees.common.entity.villager.Beekeeper;
import com.teamresourceful.resourcefulbees.common.init.*;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.defaults.DefaultApiaryTiers;
import com.teamresourceful.resourcefulbees.common.lib.defaults.DefaultBeehiveTiers;
import com.teamresourceful.resourcefulbees.common.lib.tools.ModValidation;
import com.teamresourceful.resourcefulbees.common.modcompat.base.ModCompatHelper;
import com.teamresourceful.resourcefulbees.common.network.ForgeNetworkHandler;
import com.teamresourceful.resourcefulbees.common.recipe.ingredients.AmountSensitiveIngredient;
import com.teamresourceful.resourcefulbees.common.recipe.ingredients.BeeJarIngredient;
import com.teamresourceful.resourcefulbees.common.recipe.ingredients.FilledBeeJarIngredient;
import com.teamresourceful.resourcefulbees.common.recipe.ingredients.NBTAmountSensitiveIngredient;
import com.teamresourceful.resourcefulbees.common.registries.custom.*;
import com.teamresourceful.resourcefulbees.common.registry.RegistryHandler;
import com.teamresourceful.resourcefulbees.common.registry.custom.DefaultTraitAbilities;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneycombRegistry;
import com.teamresourceful.resourcefulbees.common.registries.dynamic.ModSpawnData;
import com.teamresourceful.resourcefulbees.common.setup.GameSetup;
import com.teamresourceful.resourcefulbees.common.setup.MissingRegistrySetup;
import com.teamresourceful.resourcefulbees.platform.common.events.BlockBonemealedEvent;
import com.teamresourceful.resourcefulbees.platform.common.events.CommandRegisterEvent;
import com.teamresourceful.resourcefulbees.platform.common.events.SyncedDatapackEvent;
import com.teamresourceful.resourcefulbees.platform.common.resources.conditions.forge.ConditionRegistryImpl;
import com.teamresourceful.resourcefulconfig.client.ConfigScreen;
import com.teamresourceful.resourcefulconfig.common.config.Configurator;
import com.teamresourceful.resourcefulconfig.common.config.ResourcefulConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import software.bernie.geckolib3.GeckoLib;

import java.util.concurrent.atomic.AtomicBoolean;

@Mod(ModConstants.MOD_ID)
public class ResourcefulBees {

    private static final Configurator CONFIGURATOR = new Configurator(true);

    public ResourcefulBees() {
        ModConstants.forceInit();

        if (!FMLLoader.isProduction()) {
            ModValidation.IS_RUNNING_IN_IDE = true;
        }
        CONFIGURATOR.registerConfig(GeneralConfig.class);

        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> {
                    ResourcefulConfig config = CONFIGURATOR.getConfig(GeneralConfig.class);
                    if (config == null) {
                        return null;
                    }
                    return new ConfigScreen(null, config);
                })
        );

        GameSetup.initEvents();

        DefaultBeehiveTiers.loadDefaults();
        DefaultApiaryTiers.loadDefaults();

        DataSetup.setupInitializers(ResourcefulBeesAPI.getInitializers());
        DataSetup.setupInitializers(ResourcefulBeesAPI.getHoneyInitalizers());
        ResourcefulBeesAPI.getEvents().registerCondition(DataSetup::setupRegister);

        BeeDataRegistry.init();
        HoneyDataRegistry.init();
        LoadConditionRegistry.init();

        GeckoLib.initialize();
        ModSetup.initialize();
        RegistryHandler.init();
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
        if (FMLLoader.isProduction()) {
            GeneralConfig.generateDefaults = false;
            CONFIGURATOR.saveConfig(GeneralConfig.class);
        }

        FMLJavaModLoadingContext.get().getModEventBus().addListener(RegistryHandler::addEntityAttributes);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOW, this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInterModEnqueue);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onPackFinders);

        MinecraftForge.EVENT_BUS.addListener(this::serverLoaded);
        MinecraftForge.EVENT_BUS.addListener(Beekeeper::setupBeekeeper);
        MinecraftForge.EVENT_BUS.addListener((OnDatapackSyncEvent event) ->
            SyncedDatapackEvent.EVENT.fire(new SyncedDatapackEvent(event.getPlayerList(), event.getPlayer()))
        );

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientEventHandlers::clientStuff);

        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent event) ->
            CommandRegisterEvent.EVENT.fire(new CommandRegisterEvent(event.getDispatcher(), event.getCommandSelection(), event.getBuildContext()))
        );

        MinecraftForge.EVENT_BUS.register(this);
        ModValidation.init();

        GameSetup.initSerializersAndConditions();
    }

    @SubscribeEvent
    public void serverLoaded(ServerStartedEvent event) {
        if (event.getServer().isDedicatedServer()){
            BeeRegistry.getRegistry().regenerateCustomBeeData(event.getServer().registryAccess());
        }
    }

    @SubscribeEvent
    public void serverAboutToStart(ServerAboutToStartEvent event) {
        ModSpawnData.initialize(event.getServer());
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(RegistryHandler::registerDispenserBehaviors);
        ForgeNetworkHandler.init();
        MinecraftForge.EVENT_BUS.register(new RecipeBuilder());
        CraftingHelper.register(new ResourceLocation(ModConstants.MOD_ID, "amount_sensitive"), AmountSensitiveIngredient.Serializer.INSTANCE);
        CraftingHelper.register(new ResourceLocation(ModConstants.MOD_ID, "nbt_amount_sensitive"), NBTAmountSensitiveIngredient.Serializer.INSTANCE);
        CraftingHelper.register(new ResourceLocation(ModConstants.MOD_ID, "beejar"), BeeJarIngredient.Serializer.INSTANCE);
        CraftingHelper.register(new ResourceLocation(ModConstants.MOD_ID, "any_filled_bee_jar"), FilledBeeJarIngredient.Serializer.INSTANCE);
        GameSetup.initPotionRecipes();
        GameSetup.initArguments();
        ConditionRegistryImpl.freeze();
        MinecraftForge.EVENT_BUS.addListener((BonemealEvent bonemealEvent) -> {
            BlockBonemealedEvent newEvent = new BlockBonemealedEvent(
                    bonemealEvent.getEntity(),
                    bonemealEvent.getLevel(),
                    bonemealEvent.getPos(),
                    bonemealEvent.getBlock(),
                    bonemealEvent.getStack(),
                    new AtomicBoolean(bonemealEvent.isCanceled())
            );
            BlockBonemealedEvent.EVENT.fire(newEvent);
            if (newEvent.isCanceled()) {
                bonemealEvent.setCanceled(true);
            }
        });
    }

    @SubscribeEvent
    public void onInterModEnqueue(InterModEnqueueEvent event) {
        if (ModList.get().isLoaded("theoneprobe"))
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TopCompat::new);
    }

    @SubscribeEvent
    public void loadComplete(FMLLoadCompleteEvent event) {
        TraitAbilityRegistry.getRegistry().close();
        TraitSetup.buildCustomTraits();
        TraitRegistry.getRegistry().close();
        BeeSetup.registerBeePlacements();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> LangGeneration::generateEnglishLang);
        DataGen.generateCommonData();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> PetLoader::loadAPI);
        HoneycombRegistry.getRegistry().regenerateVariationData();

        MissingRegistrySetup.checkMissingRegistries();
    }

    @SubscribeEvent
    public void onPackFinders(AddPackFindersEvent event) {
        if (event.getPackType().equals(PackType.SERVER_DATA)) {
            event.addRepositorySource(DataPackLoader.INSTANCE);
        }
    }
}
