package com.teamresourceful.resourcefulbees.api.data.bee.mutation;

import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeData;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;

import java.util.Map;

public interface BeeMutationData extends BeeData<BeeMutationData> {

    int count();

    Map<MutationType, WeightedCollection<MutationType>> mutations();

    default boolean hasMutation() {
        return !mutations().isEmpty();
    }
}
