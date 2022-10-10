package com.teamresourceful.resourcefulbees.common.item.upgrade;

import net.minecraft.world.item.ItemStack;

public interface IIntUpgrade extends IUpgrade{
    int getUpgradeTier(ItemStack stack);
}
