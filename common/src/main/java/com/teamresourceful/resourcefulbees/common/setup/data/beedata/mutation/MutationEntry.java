package com.teamresourceful.resourcefulbees.common.setup.data.beedata.mutation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.BeeMutationEntry;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
import com.teamresourceful.resourcefulbees.common.setup.data.beedata.mutation.types.MutationCodec;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record MutationEntry(MutationType input, WeightedCollection<MutationType> outputs) implements BeeMutationEntry {

    public static final Codec<BeeMutationEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MutationCodec.CODEC.fieldOf("input").forGetter(BeeMutationEntry::input),
            CodecExtras.weightedCollection(MutationCodec.CODEC, MutationType::weight).fieldOf("outputs").forGetter(BeeMutationEntry::outputs)
    ).apply(instance, MutationEntry::new));

    public static final Codec<Map<MutationType, WeightedCollection<MutationType>>> MUTATION_MAP_CODEC = MutationEntry.CODEC.listOf().comapFlatMap(MutationEntry::convertToMap, MutationEntry::convertToList);

    private static DataResult<Map<MutationType, WeightedCollection<MutationType>>> convertToMap(List<BeeMutationEntry> list) {
        return DataResult.success(list.stream().collect(Collectors.toMap(BeeMutationEntry::input, BeeMutationEntry::outputs)));
    }

    private static List<BeeMutationEntry> convertToList(Map<MutationType, WeightedCollection<MutationType>> map) {
        List<BeeMutationEntry> list = new ArrayList<>();
        map.forEach((input, outputs) -> list.add(new MutationEntry(input, outputs)));
        return Collections.unmodifiableList(list);
    }

}
