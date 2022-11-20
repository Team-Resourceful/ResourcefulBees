package com.teamresourceful.resourcefulbees.api.beedata.mutation.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.util.GenericSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MutationCodec {

    private MutationCodec() {
        throw new UtilityClassError();
    }

    private static final Map<String, GenericSerializer<Mutation>> SERIALIZERS = new HashMap<>();

    static {
        register(BlockMutation.SERIALIZER);
        register(EntityMutation.SERIALIZER);
        register(FluidMutation.SERIALIZER);
        register(ItemMutation.SERIALIZER);
    }

    public static final Codec<GenericSerializer<Mutation>> TYPE_CODEC = Codec.STRING.comapFlatMap(MutationCodec::decode, GenericSerializer::id);
    public static final Codec<Mutation> CODEC = TYPE_CODEC.dispatch(Mutation::serializer, GenericSerializer<Mutation>::codec);

    private static DataResult<GenericSerializer<Mutation>> decode(String id) {
        return Optional.ofNullable(SERIALIZERS.get(id)).map(DataResult::success).orElse(DataResult.error("No mutation serializer found with id '" + id + "'."));
    }

    private static void register(GenericSerializer<Mutation> serializer) {
        SERIALIZERS.put(serializer.id(), serializer);
    }

}
