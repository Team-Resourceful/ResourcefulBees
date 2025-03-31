package com.teamresourceful.resourcefulbees.common.modcompat.jei.mutation;

import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import net.minecraft.world.entity.EntityType;

import java.text.NumberFormat;

public record MutationRecipe(EntityType<?> bee, MutationType input, MutationType output, WeightedCollection<MutationType> pool) {

    public String displayFormattedWeight() {
        return NumberFormat.getPercentInstance().format(pool().getAdjustedWeight(output().weight()));
    }

    public String displayFormattedChance() {
        return NumberFormat.getPercentInstance().format(output().chance());
    }
}