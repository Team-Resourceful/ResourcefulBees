package com.teamresourceful.resourcefulbees.api.beedata.mutation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.types.IMutation;
import com.teamresourceful.resourcefullib.common.utils.RandomCollection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record MutationData(int mutationCount, Map<IMutation, RandomCollection<IMutation>> mutations) {
    public static final MutationData DEFAULT = new MutationData(0, Collections.emptyMap());

    /**
     * A {@link Codec<MutationData>} that can be parsed to create a
     * {@link MutationData} object.
     */
    public static final Codec<MutationData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("mutationCount").orElse(10).forGetter(MutationData::mutationCount),
            Mutation.MUTATION_MAP_CODEC.fieldOf("mutations").orElse(new HashMap<>()).forGetter(MutationData::mutations)
    ).apply(instance, MutationData::new));

    /**
     * @return Returns the number of times this bee can attempt a mutation
     * before needing to enter a hive or apiary
     */
    public int getMutationCount() {
        return mutationCount;
    }

    /**
     * @return Returns <tt>true</tt> if the mutations list is not empty.
     */
    public boolean hasMutation() {
        return !mutations.isEmpty();
    }

}

