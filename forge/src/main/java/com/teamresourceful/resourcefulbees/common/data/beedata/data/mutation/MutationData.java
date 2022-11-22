package com.teamresourceful.resourcefulbees.common.data.beedata.data.mutation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.BeeMutationData;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeDataSerializer;
import com.teamresourceful.resourcefulbees.common.util.ModResourceLocation;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record MutationData(int count, Map<MutationType, WeightedCollection<MutationType>> mutations) implements BeeMutationData {

    private static final BeeMutationData DEFAULT = new MutationData(0, Collections.emptyMap());
    private static final Codec<BeeMutationData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("mutationCount").orElse(10).forGetter(BeeMutationData::count),
            MutationEntry.MUTATION_MAP_CODEC.fieldOf("mutations").orElse(new HashMap<>()).forGetter(BeeMutationData::mutations)
    ).apply(instance, MutationData::new));

    public static final BeeDataSerializer<BeeMutationData> SERIALIZER = BeeDataSerializer.of(new ModResourceLocation("mutation"), 1, id -> CODEC, DEFAULT);

    @Override
    public BeeDataSerializer<BeeMutationData> serializer() {
        return SERIALIZER;
    }
}
