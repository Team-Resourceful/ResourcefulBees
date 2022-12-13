package com.teamresourceful.resourcefulbees.api.data.bee.mutation;

import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeData;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Map;

public interface BeeMutationData extends BeeData<BeeMutationData> {

    int count();

    ResourceLocation id();

    Map<MutationType, WeightedCollection<MutationType>> mutations(Level level);

    boolean hasMutation(Level level);
}
