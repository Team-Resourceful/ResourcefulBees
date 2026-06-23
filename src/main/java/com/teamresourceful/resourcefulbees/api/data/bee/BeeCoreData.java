package com.teamresourceful.resourcefulbees.api.data.bee;

import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeData;
import com.teamresourceful.resourcefulbees.api.data.honeycomb.OutputVariation;
import com.teamresourceful.resourcefulbees.api.data.shared.RegistryPredicate;
import com.teamresourceful.resourcefulbees.api.registry.HoneycombRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Optional;

@NullMarked
public interface BeeCoreData extends BeeData<BeeCoreData> {

    String honeycomb();

    RegistryPredicate<Block> blockFlowers();

    default boolean hasBlockFlowers() {
        return blockFlowers().populated();
    }

    default boolean isBlockFlower(BlockState state) {
        return blockFlowers().test(state);
    }

    RegistryPredicate<EntityType<?>> entityFlowers();

    default boolean hasEntityFlower() {
        return entityFlowers().populated();
    }

    default boolean isEntityFlower(EntityType<?> entityType) {
        return entityFlowers().test(entityType.builtInRegistryHolder());
    }

    int maxTimeInHive();

    List<Component> lore();

    default Optional<OutputVariation> getHoneycombData() {
        return Optional.ofNullable(HoneycombRegistry.get().getHoneycomb(honeycomb()));
    }
}
