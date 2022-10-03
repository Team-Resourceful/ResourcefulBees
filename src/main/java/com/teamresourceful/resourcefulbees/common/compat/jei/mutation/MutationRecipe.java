package com.teamresourceful.resourcefulbees.common.compat.jei.mutation;

import com.teamresourceful.resourcefulbees.api.beedata.mutation.types.IMutation;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import net.minecraft.world.entity.EntityType;

public record MutationRecipe(EntityType<?> bee, IMutation input, IMutation output, WeightedCollection<IMutation> pool) {}