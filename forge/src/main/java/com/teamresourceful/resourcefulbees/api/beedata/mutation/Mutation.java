package com.teamresourceful.resourcefulbees.api.beedata.mutation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.types.MutationCodec;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record Mutation(com.teamresourceful.resourcefulbees.api.beedata.mutation.types.Mutation input, WeightedCollection<com.teamresourceful.resourcefulbees.api.beedata.mutation.types.Mutation> outputs) {

    public static final Codec<Mutation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MutationCodec.CODEC.fieldOf("input").forGetter(com.teamresourceful.resourcefulbees.api.beedata.mutation.Mutation::input),
            CodecExtras.weightedCollection(MutationCodec.CODEC, com.teamresourceful.resourcefulbees.api.beedata.mutation.types.Mutation::weight).fieldOf("outputs").forGetter(com.teamresourceful.resourcefulbees.api.beedata.mutation.Mutation::outputs)
    ).apply(instance, com.teamresourceful.resourcefulbees.api.beedata.mutation.Mutation::new));

    public static final Codec<Map<com.teamresourceful.resourcefulbees.api.beedata.mutation.types.Mutation, WeightedCollection<com.teamresourceful.resourcefulbees.api.beedata.mutation.types.Mutation>>> MUTATION_MAP_CODEC = com.teamresourceful.resourcefulbees.api.beedata.mutation.Mutation.CODEC.listOf().comapFlatMap(com.teamresourceful.resourcefulbees.api.beedata.mutation.Mutation::convertToMap, com.teamresourceful.resourcefulbees.api.beedata.mutation.Mutation::convertToList);

    private static DataResult<Map<com.teamresourceful.resourcefulbees.api.beedata.mutation.types.Mutation, WeightedCollection<com.teamresourceful.resourcefulbees.api.beedata.mutation.types.Mutation>>> convertToMap(List<Mutation> list) {
        return DataResult.success(list.stream().collect(Collectors.toMap(com.teamresourceful.resourcefulbees.api.beedata.mutation.Mutation::input, com.teamresourceful.resourcefulbees.api.beedata.mutation.Mutation::outputs)));
    }

    private static List<Mutation> convertToList(Map<com.teamresourceful.resourcefulbees.api.beedata.mutation.types.Mutation, WeightedCollection<com.teamresourceful.resourcefulbees.api.beedata.mutation.types.Mutation>> map) {
        return map.entrySet().stream().map(entry -> new Mutation(entry.getKey(), entry.getValue())).toList();
    }
}
