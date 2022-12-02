package com.teamresourceful.resourcefulbees.api.compat;


import com.teamresourceful.resourcefulbees.common.lib.builders.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.lib.builders.BeehiveTier;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface BeeCompat {

    ItemStack getHiveOutput(BeehiveTier tier);

    default Optional<ItemStack> getOptionalHiveOutput(BeehiveTier tier) {
        ItemStack stack = getHiveOutput(tier);
        if (stack.isEmpty()) return Optional.empty();
        return Optional.of(stack);
    }

    ItemStack getApiaryOutput(ApiaryTier tier);

    default Optional<ItemStack> getOptionalApiaryOutput(ApiaryTier tier) {
        ItemStack stack = getApiaryOutput(tier);
        if (stack.isEmpty()) return Optional.empty();
        return Optional.of(stack);
    }

    int getMaxTimeInHive();

    void nectarDroppedOff();

    void setOutOfHiveCooldown(int cooldown);
}
