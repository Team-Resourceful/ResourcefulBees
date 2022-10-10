package com.teamresourceful.resourcefulbees.api;

import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeehiveTier;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface IBeeCompat {

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
