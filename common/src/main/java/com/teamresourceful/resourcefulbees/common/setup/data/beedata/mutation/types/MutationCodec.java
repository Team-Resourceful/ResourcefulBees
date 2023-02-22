package com.teamresourceful.resourcefulbees.common.setup.data.beedata.mutation.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.util.GenericSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MutationCodec {

    private MutationCodec() {
        throw new UtilityClassError();
    }

    private static final Map<String, GenericSerializer<MutationType>> SERIALIZERS = new HashMap<>();

    static {
        register(BlockMutation.SERIALIZER);
        register(EntityMutation.SERIALIZER);
        register(FluidMutation.SERIALIZER);
        register(ItemMutation.SERIALIZER);
    }

    public static final Codec<GenericSerializer<MutationType>> TYPE_CODEC = Codec.STRING.comapFlatMap(MutationCodec::decode, GenericSerializer::id);
    public static final Codec<MutationType> CODEC = TYPE_CODEC.dispatch(MutationType::serializer, GenericSerializer::codec);

    private static DataResult<GenericSerializer<MutationType>> decode(String id) {
        return Optional.ofNullable(SERIALIZERS.get(id)).map(DataResult::success).orElse(DataResult.error("No mutation serializer found with id '" + id + "'."));
    }

    private static void register(GenericSerializer<MutationType> serializer) {
        SERIALIZERS.put(serializer.id(), serializer);
    }

}
