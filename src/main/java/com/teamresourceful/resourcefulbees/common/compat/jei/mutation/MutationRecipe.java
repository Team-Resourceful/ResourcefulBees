package com.teamresourceful.resourcefulbees.common.compat.jei.mutation;

import com.teamresourceful.resourcefulbees.api.beedata.mutation.types.IMutation;
import com.teamresourceful.resourcefulbees.common.utils.RandomCollection;
import net.minecraft.world.entity.EntityType;

public record MutationRecipe(EntityType<?> bee, IMutation input, IMutation output, RandomCollection<IMutation> pool) {}