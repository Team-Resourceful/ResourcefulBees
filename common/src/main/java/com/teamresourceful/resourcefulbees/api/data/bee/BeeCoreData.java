package com.teamresourceful.resourcefulbees.api.data.bee;

import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeData;
import com.teamresourceful.resourcefulbees.api.data.honeycomb.OutputVariation;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Optional;

public interface BeeCoreData extends BeeData<BeeCoreData> {

    String honeycomb();

    HolderSet<Block> flowers();

    default boolean hasFlowers() {
        return flowers().size() > 0;
    }

    HolderSet<EntityType<?>> entityFlowers();

    default boolean hasEntityFlower() {
        return entityFlowers().size() > 0;
    }

    int maxTimeInHive();

    List<MutableComponent> lore();

    default Optional<OutputVariation> getHoneycombData() {
        return Optional.ofNullable(ResourcefulBeesAPI.getRegistry().getHoneycombRegistry().getOutputVariation(honeycomb()));
    }
}
