package com.teamresourceful.resourcefulbees.common.compat.jei.mutation;

import com.teamresourceful.resourcefulbees.api.beedata.mutation.types.Mutation;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import net.minecraft.world.entity.EntityType;

public record MutationRecipe(EntityType<?> bee, Mutation input, Mutation output, WeightedCollection<Mutation> pool) {}