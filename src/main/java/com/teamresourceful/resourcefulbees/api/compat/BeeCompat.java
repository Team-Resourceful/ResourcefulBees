package com.teamresourceful.resourcefulbees.api.compat;


import com.teamresourceful.resourcefulbees.api.tiers.ApiaryTier;
import com.teamresourceful.resourcefulbees.api.tiers.BeehiveTier;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface BeeCompat {

    ItemStack resourcefulBees$getHiveOutput(BeehiveTier tier);

    default Optional<ItemStack> getOptionalHiveOutput(BeehiveTier tier) {
        ItemStack stack = resourcefulBees$getHiveOutput(tier);
        if (stack.isEmpty()) return Optional.empty();
        return Optional.of(stack);
    }

    ItemStack resourcefulBees$getApiaryOutput(ApiaryTier tier);

    default Optional<ItemStack> getOptionalApiaryOutput(ApiaryTier tier) {
        ItemStack stack = resourcefulBees$getApiaryOutput(tier);
        if (stack.isEmpty()) return Optional.empty();
        return Optional.of(stack);
    }

    int resourcefulBees$getMaxTimeInHive();

    void resourcefulBees$nectarDroppedOff();

    void resourcefulBees$setOutOfHiveCooldown(int cooldown);
}
