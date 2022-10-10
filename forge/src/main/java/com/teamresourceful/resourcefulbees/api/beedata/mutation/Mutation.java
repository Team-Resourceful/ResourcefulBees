package com.teamresourceful.resourcefulbees.api.beedata.mutation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.types.IMutation;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.types.MutationCodec;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record Mutation(IMutation input, WeightedCollection<IMutation> outputs) {

    public static final Codec<Mutation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MutationCodec.CODEC.fieldOf("input").forGetter(Mutation::input),
            CodecExtras.weightedCollection(MutationCodec.CODEC, IMutation::weight).fieldOf("outputs").forGetter(Mutation::outputs)
    ).apply(instance, Mutation::new));

    public static final Codec<Map<IMutation, WeightedCollection<IMutation>>> MUTATION_MAP_CODEC = Mutation.CODEC.listOf().comapFlatMap(Mutation::convertToMap, Mutation::convertToList);

    private static DataResult<Map<IMutation, WeightedCollection<IMutation>>> convertToMap(List<Mutation> list) {
        return DataResult.success(list.stream().collect(Collectors.toMap(Mutation::input, Mutation::outputs)));
    }

    private static List<Mutation> convertToList(Map<IMutation, WeightedCollection<IMutation>> map) {
        return map.entrySet().stream().map(entry -> new Mutation(entry.getKey(), entry.getValue())).toList();
    }
}
