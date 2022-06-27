package com.teamresourceful.resourcefulbees.api.beedata.mutation.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.utils.RandomCollection;

import java.util.*;

public final class MutationCodec {

    private MutationCodec() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }

    private static final Map<String, IMutationSerializer> SERIALIZERS = new HashMap<>();

    static {
        register(BlockMutation.SERIALIZER);
        register(EntityMutation.SERIALIZER);
        register(FluidMutation.SERIALIZER);
        register(ItemMutation.SERIALIZER);
    }

    public static final Codec<IMutationSerializer> TYPE_CODEC = Codec.STRING.comapFlatMap(MutationCodec::decode, IMutationSerializer::id);
    public static final Codec<IMutation> CODEC = TYPE_CODEC.dispatch(IMutation::serializer, IMutationSerializer::codec);
    public static final Codec<RandomCollection<IMutation>> RANDOM_COLLECTION_CODEC = CodecUtils.createSetCodec(MutationCodec.CODEC).comapFlatMap(MutationCodec::convertToRandomCollection, MutationCodec::convertToSet);

    private static DataResult<IMutationSerializer> decode(String id) {
        return Optional.ofNullable(SERIALIZERS.get(id)).map(DataResult::success).orElse(DataResult.error("No mutation serializer found with id '" + id + "'."));
    }

    private static DataResult<RandomCollection<IMutation>> convertToRandomCollection(Set<IMutation> set) {
        return DataResult.success(set.stream().collect(RandomCollection.getCollector(IMutation::weight)));
    }

    private static Set<IMutation> convertToSet(RandomCollection<IMutation> randomCollection) {
        return new HashSet<>(randomCollection.getMap().values());
    }

    private static void register(IMutationSerializer serializer) {
        SERIALIZERS.put(serializer.id(), serializer);
    }

}
