package com.teamresourceful.resourcefulbees.platform.client.events;

import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public record RegisterRendererEvent(EntityRegistrar entity, BlockRegistrar block) {

    public static final EventHelper<RegisterRendererEvent> EVENT = new EventHelper<>();

    public <T extends Entity> void register(EntityType<? extends T> entityType, EntityRendererProvider<T> renderer) {
        entity.register(entityType, renderer);
    }

    public <T extends BlockEntity> void register(BlockEntityType<? extends T> blockEntityType, BlockEntityRendererProvider<T> renderer) {
        block.register(blockEntityType, renderer);
    }

    @FunctionalInterface
    public interface EntityRegistrar {

        <T extends Entity> void register(EntityType<? extends T> type, EntityRendererProvider<T> renderer);
    }

    @FunctionalInterface
    public interface BlockRegistrar {

        <T extends BlockEntity> void register(BlockEntityType<? extends T> type, BlockEntityRendererProvider<T> renderer);
    }
}
