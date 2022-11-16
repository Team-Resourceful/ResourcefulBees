package com.teamresourceful.resourcefulbees.api.beedata.mutation.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MutationCodec {

    private MutationCodec() {
        throw new UtilityClassError();
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

    private static DataResult<IMutationSerializer> decode(String id) {
        return Optional.ofNullable(SERIALIZERS.get(id)).map(DataResult::success).orElse(DataResult.error("No mutation serializer found with id '" + id + "'."));
    }

    private static void register(IMutationSerializer serializer) {
        SERIALIZERS.put(serializer.id(), serializer);
    }

}
