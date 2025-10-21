package com.teamresourceful.resourcefulbees.common.items.upgrade;

import net.minecraft.world.item.ItemStack;

public interface IntegerUpgrade extends Upgrade {
    int getUpgradeTier(ItemStack stack);
}
