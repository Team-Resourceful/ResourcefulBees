package com.teamresourceful.resourcefulbees;

import com.teamresourceful.resourcefulbees.common.ResourcefulBees;
import com.teamresourceful.resourcefulbees.platform.common.events.*;
import com.teamresourceful.resourcefulbees.platform.common.events.lifecycle.CommonSetupEvent;
import com.teamresourceful.resourcefulbees.platform.common.events.lifecycle.GameServerStartedEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;

public class ResourcefulBeesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ResourcefulBees.init();

        RegisterEntityAttributesEvent.EVENT.fire(new RegisterEntityAttributesEvent(FabricDefaultAttributeRegistry::register));
        CommonSetupEvent.fire();
        RegisterBurnablesEvent.EVENT.fire(new RegisterBurnablesEvent(FuelRegistry.INSTANCE::add));
        RegisterSpawnPlacementsEvent.EVENT.fire(new RegisterSpawnPlacementsEvent(new RegisterSpawnPlacementsEvent.Registry() {
            @Override
            public <T extends Mob> void register(EntityType<T> entityType, @NotNull SpawnPlacements.Type placementType, @NotNull Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> predicate) {
               SpawnPlacements.register(entityType, placementType, heightmap, predicate);
            }
        }));
        ServerLifecycleEvents.SERVER_STARTED.register(server ->
            GameServerStartedEvent.EVENT.fire(new GameServerStartedEvent(server))
        );
        ServerLifecycleEvents.SERVER_STARTING.register(server ->
            GameServerStartedEvent.EVENT.fire(new GameServerStartedEvent(server))
        );
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) ->
            SyncedDatapackEvent.EVENT.fire(new SyncedDatapackEvent(player))
        );
        CommandRegistrationCallback.EVENT.register((dispatcher, context, env) ->
            CommandRegisterEvent.EVENT.fire(new CommandRegisterEvent(dispatcher, env, context))
        );
        CommonLifecycleEvents.TAGS_LOADED.register((access, client) ->
            UpdateEvent.EVENT.fire(new UpdateEvent(UpdateEvent.UpdateType.TAG))
        );
        //TODO recipes updated
    }
}
