package com.teamresourceful.resourcefulbees.api.trait;

import com.teamresourceful.resourcefulbees.common.entity.passive.ResourcefulBee;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public interface TraitAbility {

    ItemStack getIcon();

    Component getTitle();

    Component getDescription();

    default boolean canRun() {
        return true;
    }

    void run(ResourcefulBee bee);
}
