package com.teamresourceful.resourcefulbees;

import com.teamresourceful.resourcefulbees.common.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.compat.top.TopCompat;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.platform.client.events.lifecycle.ClientLoadingCompletedEvent;
import com.teamresourceful.resourcefulbees.platform.common.events.*;
import com.teamresourceful.resourcefulbees.platform.common.events.lifecycle.CommonSetupEvent;
import com.teamresourceful.resourcefulbees.platform.common.events.lifecycle.GameServerStartedEvent;
import com.teamresourceful.resourcefulbees.platform.common.events.lifecycle.LoadingCompletedEvent;
import com.teamresourceful.resourcefulbees.platform.common.events.lifecycle.ServerGoingToStartEvent;
import com.teamresourceful.resourcefulbees.platform.common.events.registry.RegisterRepositorySourceEvent;
import com.teamresourceful.resourcefulbees.platform.common.recipe.ingredient.forge.ForgeIngredientHelper;
import com.teamresourceful.resourcefulbees.platform.common.resources.conditions.forge.ConditionRegistryImpl;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

@Mod(ModConstants.MOD_ID)
public class ResourcefulBeesForge {

    public ResourcefulBeesForge() {
        ResourcefulBees.init();

        Object2IntMap<Item> burnables = new Object2IntOpenHashMap<>();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener((EntityAttributeCreationEvent event) ->
            RegisterEntityAttributesEvent.EVENT.fire(new RegisterEntityAttributesEvent(event::put))
        );
        modEventBus.addListener((FMLCommonSetupEvent event) -> {
            event.enqueueWork(CommonSetupEvent::fire);
            ForgeIngredientHelper.init();
            ConditionRegistryImpl.freeze();
            RegisterBurnablesEvent.EVENT.fire(new RegisterBurnablesEvent((item, value) -> burnables.put(item.asItem(), value)));
        });
        modEventBus.addListener((FMLLoadCompleteEvent event) -> {
            if (FMLLoader.getDist().isClient()) {
                ClientLoadingCompletedEvent.fire();
            }
            LoadingCompletedEvent.fire();
        });
        modEventBus.addListener((AddPackFindersEvent event) ->
            RegisterRepositorySourceEvent.EVENT.fire(new RegisterRepositorySourceEvent(event.getPackType(), event::addRepositorySource))
        );
        modEventBus.addListener(this::onInterModEnqueue);

        modEventBus.addListener((SpawnPlacementRegisterEvent event) ->
            RegisterSpawnPlacementsEvent.EVENT.fire(new RegisterSpawnPlacementsEvent(new RegisterSpawnPlacementsEvent.Registry() {
                @Override
                public <T extends Entity> void register(EntityType<T> entityType, SpawnPlacements.@Nullable Type placementType, Heightmap.@Nullable Types heightmap, SpawnPlacements.SpawnPredicate<T> predicate) {
                    event.register(entityType, placementType, heightmap, predicate, SpawnPlacementRegisterEvent.Operation.REPLACE);
                }
            }))
        );

        MinecraftForge.EVENT_BUS.addListener((ServerStartedEvent event) ->
            GameServerStartedEvent.EVENT.fire(new GameServerStartedEvent(event.getServer()))
        );
        MinecraftForge.EVENT_BUS.addListener((ServerAboutToStartEvent event) ->
            ServerGoingToStartEvent.EVENT.fire(new ServerGoingToStartEvent(event.getServer(), event.getServer().registryAccess()))
        );
        MinecraftForge.EVENT_BUS.addListener((VillagerTradesEvent event) ->
            RegisterVillagerTradesEvent.EVENT.fire(new RegisterVillagerTradesEvent((listing, i) -> event.getTrades().get(i).add(listing), event.getType()))
        );
        MinecraftForge.EVENT_BUS.addListener((OnDatapackSyncEvent event) ->
            SyncedDatapackEvent.EVENT.fire(new SyncedDatapackEvent(event.getPlayerList(), event.getPlayer()))
        );
        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent event) ->
            CommandRegisterEvent.EVENT.fire(new CommandRegisterEvent(event.getDispatcher(), event.getCommandSelection(), event.getBuildContext()))
        );
        MinecraftForge.EVENT_BUS.addListener((AddReloadListenerEvent event) ->
            RegisterReloadListenerEvent.EVENT.fire(new RegisterReloadListenerEvent(event::addListener, event.getServerResources()))
        );
        MinecraftForge.EVENT_BUS.addListener((RecipesUpdatedEvent event) ->
            UpdateEvent.EVENT.fire(new UpdateEvent(UpdateEvent.UpdateType.RECIPE))
        );
        MinecraftForge.EVENT_BUS.addListener((TagsUpdatedEvent event) ->
            UpdateEvent.EVENT.fire(new UpdateEvent(UpdateEvent.UpdateType.TAG))
        );
        MinecraftForge.EVENT_BUS.addListener((FurnaceFuelBurnTimeEvent event) -> {
            if (burnables.containsKey(event.getItemStack().getItem())) {
                event.setBurnTime(burnables.getInt(event.getItemStack().getItem()));
            }
        });
        MinecraftForge.EVENT_BUS.addListener((BonemealEvent event) -> {
            BlockBonemealedEvent newEvent = new BlockBonemealedEvent(event.getEntity(), event.getLevel(), event.getPos(), event.getBlock(), event.getStack(), new AtomicBoolean(event.isCanceled()));
            BlockBonemealedEvent.EVENT.fire(newEvent);
            if (newEvent.isCanceled()) {
                event.setCanceled(true);
            }
        });

        if (FMLEnvironment.dist.isClient()) {
            ResourcefulBeesForgeClient.init();
        }

        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onInterModEnqueue(InterModEnqueueEvent event) {
        if (ModList.get().isLoaded("theoneprobe"))
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TopCompat::new);
    }
}
