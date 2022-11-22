package com.teamresourceful.resourcefulbees.common.compat.jei.mutation;

import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import net.minecraft.world.entity.EntityType;

public record MutationRecipe(EntityType<?> bee, MutationType input, MutationType output, WeightedCollection<MutationType> pool) {}