package com.teamresourceful.resourcefulbees;

import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.client.data.LangGeneration;
import com.teamresourceful.resourcefulbees.client.event.ClientEventHandlers;
import com.teamresourceful.resourcefulbees.client.pets.PetLoader;
import com.teamresourceful.resourcefulbees.common.compat.top.TopCompat;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.data.DataGen;
import com.teamresourceful.resourcefulbees.common.data.DataPackLoader;
import com.teamresourceful.resourcefulbees.common.events.ItemEventHandler;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.defaults.DefaultApiaryTiers;
import com.teamresourceful.resourcefulbees.common.lib.defaults.DefaultBeehiveTiers;
import com.teamresourceful.resourcefulbees.common.lib.tools.ModValidation;
import com.teamresourceful.resourcefulbees.common.modcompat.base.ModCompatHelper;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefulbees.common.registries.RegistryHandler;
import com.teamresourceful.resourcefulbees.common.registries.custom.*;
import com.teamresourceful.resourcefulbees.common.setup.DataSetup;
import com.teamresourceful.resourcefulbees.common.setup.GameSetup;
import com.teamresourceful.resourcefulbees.common.setup.MissingRegistrySetup;
import com.teamresourceful.resourcefulbees.common.setup.data.BeeSetup;
import com.teamresourceful.resourcefulbees.common.setup.data.HoneySetup;
import com.teamresourceful.resourcefulbees.common.setup.data.HoneycombSetup;
import com.teamresourceful.resourcefulbees.common.setup.data.TraitSetup;
import com.teamresourceful.resourcefulbees.platform.common.events.*;
import com.teamresourceful.resourcefulbees.platform.common.events.lifecycle.ServerGoingToStartEvent;
import com.teamresourceful.resourcefulbees.platform.common.recipe.ingredient.forge.ForgeIngredientHelper;
import com.teamresourceful.resourcefulbees.platform.common.resources.conditions.forge.ConditionRegistryImpl;
import com.teamresourceful.resourcefulconfig.common.config.Configurator;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLib;

@Mod(ModConstants.MOD_ID)
public class ResourcefulBees {

    public static final Configurator CONFIGURATOR = new Configurator();

    public ResourcefulBees() {
        ModConstants.forceInit();

        if (!FMLLoader.isProduction()) {
            ModValidation.IS_RUNNING_IN_IDE = true;
        }
        CONFIGURATOR.registerConfig(GeneralConfig.class);

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
        GameSetup.initPaths();
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

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener((EntityAttributeCreationEvent event) -> RegisterEntityAttributesEvent.EVENT.fire(new RegisterEntityAttributesEvent(event::put)));
        modEventBus.addListener(EventPriority.LOW, this::setup);
        modEventBus.addListener(this::onInterModEnqueue);
        modEventBus.addListener(this::loadComplete);
        modEventBus.addListener(this::onPackFinders);

        modEventBus.addListener((SpawnPlacementRegisterEvent event) ->
                RegisterSpawnPlacementsEvent.EVENT.fire(new RegisterSpawnPlacementsEvent(new RegisterSpawnPlacementsEvent.Registry() {
                    @Override
                    public <T extends Entity> void register(EntityType<T> entityType, SpawnPlacements.@Nullable Type placementType, Heightmap.@Nullable Types heightmap, SpawnPlacements.SpawnPredicate<T> predicate) {
                        event.register(entityType, placementType, heightmap, predicate, SpawnPlacementRegisterEvent.Operation.REPLACE);
                    }
                }))
        );

        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        forgeEventBus.addListener(this::serverLoaded);
        forgeEventBus.addListener((VillagerTradesEvent event) ->
                RegisterVillagerTradesEvent.EVENT.fire(new RegisterVillagerTradesEvent((listing, i) -> event.getTrades().get(i).add(listing), event.getType()))
        );
        forgeEventBus.addListener((OnDatapackSyncEvent event) ->
            SyncedDatapackEvent.EVENT.fire(new SyncedDatapackEvent(event.getPlayerList(), event.getPlayer()))
        );

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientEventHandlers::clientStuff);

        forgeEventBus.addListener((RegisterCommandsEvent event) ->
            CommandRegisterEvent.EVENT.fire(new CommandRegisterEvent(event.getDispatcher(), event.getCommandSelection(), event.getBuildContext()))
        );

        forgeEventBus.register(this);
        ModValidation.init();

        GameSetup.init();
        ItemEventHandler.init();

        MinecraftForge.EVENT_BUS.addListener((AddReloadListenerEvent event) ->
                RegisterReloadListenerEvent.EVENT.fire(new RegisterReloadListenerEvent(event::addListener, event.getServerResources()))
        );
    }

    @SubscribeEvent
    public void serverLoaded(ServerStartedEvent event) {
        if (event.getServer().isDedicatedServer()){
            BeeRegistry.getRegistry().regenerateCustomBeeData(event.getServer().registryAccess());
        }
    }

    @SubscribeEvent
    public void serverAboutToStart(ServerAboutToStartEvent event) {
        ServerGoingToStartEvent.EVENT.fire(new ServerGoingToStartEvent(event.getServer(), event.getServer().registryAccess()));
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(RegistryHandler::registerDispenserBehaviors);
        NetworkHandler.init();
        ForgeIngredientHelper.init();
        GameSetup.initPotionRecipes();
        GameSetup.initArguments();
        ConditionRegistryImpl.freeze();
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
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> LangGeneration::generateEnglishLang);
        DataGen.generateCommonData();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> PetLoader::loadAPI);
        HoneycombRegistry.getRegistry().regenerateVariationData();
        MissingRegistrySetup.checkMissingRegistries();
        if (FMLLoader.isProduction()) {
            GeneralConfig.generateDefaults = false;
            CONFIGURATOR.saveConfig(GeneralConfig.class);
        }
    }

    @SubscribeEvent
    public void onPackFinders(AddPackFindersEvent event) {
        if (event.getPackType().equals(PackType.SERVER_DATA)) {
            event.addRepositorySource(DataPackLoader.INSTANCE);
        }
    }
}
