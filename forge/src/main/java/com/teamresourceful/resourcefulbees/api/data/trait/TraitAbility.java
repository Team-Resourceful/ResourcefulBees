package com.teamresourceful.resourcefulbees.api.data.trait;

import com.teamresourceful.resourcefulbees.client.util.displays.ItemDisplay;
import com.teamresourceful.resourcefulbees.common.entity.passive.ResourcefulBee;
import net.minecraft.network.chat.Component;

public interface TraitAbility extends ItemDisplay {

    Component getTitle();

    Component getDescription();

    default boolean canRun() {
        return true;
    }

    void run(ResourcefulBee bee);
}
