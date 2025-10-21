package com.teamresourceful.resourcefulbees.api.data.trait;

import com.teamresourceful.resourcefulbees.client.util.displays.ItemDisplay;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.Bee;

public interface TraitAbility extends ItemDisplay {

    Component getTitle();

    Component getDescription();

    default boolean canRun() {
        return true;
    }

    void run(Bee bee);
}
