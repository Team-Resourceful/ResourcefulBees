package com.teamresourceful.resourcefulbees.api;

import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeehiveTier;
import net.minecraft.world.item.ItemStack;

public interface IBeeCompat {

    ItemStack getHiveOutput(BeehiveTier tier);

    ItemStack getApiaryOutput(ApiaryTier tier);

    int getMaxTimeInHive();

    void nectarDroppedOff();

    void setOutOfHiveCooldown(int cooldown);
}
