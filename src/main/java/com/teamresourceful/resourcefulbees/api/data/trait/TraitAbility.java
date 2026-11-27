package com.teamresourceful.resourcefulbees.api.data.trait;

import com.teamresourceful.resourcefulbees.client.util.displays.ItemDisplay;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public interface TraitAbility extends ItemDisplay {

    Component getTitle();

    Component getDescription();

    default boolean canRun() {
        return true;
    }

    void run(ResourcefulBee bee);

    default void onPollinationComplete(ResourcefulBee bee, BlockPos flowerPos) {

    }

    default boolean ignoresBlockSlowdown(ResourcefulBee bee, BlockState state) {
        return false;
    }
}
