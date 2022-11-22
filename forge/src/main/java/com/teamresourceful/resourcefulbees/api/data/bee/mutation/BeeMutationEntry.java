package com.teamresourceful.resourcefulbees.api.data.bee.mutation;

import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;

public interface BeeMutationEntry {

    MutationType input();

    WeightedCollection<MutationType> outputs();
}
