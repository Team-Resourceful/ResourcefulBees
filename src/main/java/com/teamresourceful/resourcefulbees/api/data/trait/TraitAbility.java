package com.teamresourceful.resourcefulbees.api.data.trait;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.Bee;

public interface TraitAbility {

    Component getTitle();

    Component getDescription();

    default boolean canRun() {
        return true;
    }

    void run(Bee bee);
}
