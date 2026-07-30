package com.teamresourceful.resourcefulbees.common.setup.data.beedata.mutation.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
import com.teamresourceful.resourcefulbees.common.util.GenericSerializer;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MutationCodec {

    private MutationCodec() throws UtilityClassException {
        throw new UtilityClassException();
    }

    private static final Map<String, GenericSerializer<? extends MutationType>> SERIALIZERS = new HashMap<>();

    static {
        register(BlockMutation.SERIALIZER);
        register(EntityMutation.SERIALIZER);
        register(FluidMutation.SERIALIZER);
        register(ItemMutation.SERIALIZER);
    }

    public static final Codec<GenericSerializer<? extends MutationType>> TYPE_CODEC = Codec.STRING.comapFlatMap(MutationCodec::decode, GenericSerializer::id);
    public static final Codec<MutationType> CODEC = TYPE_CODEC.dispatch(MutationType::serializer, GenericSerializer::codec);
    public static final StreamCodec<ByteBuf, GenericSerializer<? extends MutationType>> TYPE_STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(
            s -> MutationCodec.decode(s).result().orElseThrow(), GenericSerializer::id);

    public static final StreamCodec<RegistryFriendlyByteBuf, MutationType> STREAM_CODEC =
            new StreamCodec<>() {

                @Override
                public MutationType decode(RegistryFriendlyByteBuf buffer) {
                    GenericSerializer<? extends MutationType> serializer =
                            TYPE_STREAM_CODEC.decode(buffer);

                    return decodeMutation(buffer, serializer);
                }

                @Override
                public void encode(
                        RegistryFriendlyByteBuf buffer,
                        MutationType mutation
                ) {
                    GenericSerializer<? extends MutationType> serializer =
                            mutation.serializer();

                    TYPE_STREAM_CODEC.encode(buffer, serializer);
                    encodeMutation(buffer, serializer, mutation);
                }
            };

    private static <T extends MutationType> T decodeMutation(RegistryFriendlyByteBuf buffer, GenericSerializer<T> serializer) {
        return serializer.streamCodec().decode(buffer);
    }

    @SuppressWarnings("unchecked")
    private static <T extends MutationType> void encodeMutation(RegistryFriendlyByteBuf buffer, GenericSerializer<T> serializer, MutationType mutation) {
        serializer.streamCodec().encode(
                buffer,
                (T) mutation
        );
    }

    private static DataResult<GenericSerializer<? extends MutationType>> decode(String id) {

        GenericSerializer<? extends MutationType> serializer = SERIALIZERS.get(id);

        if (serializer == null) {
            return DataResult.error(
                    () -> "No mutation serializer found with id '" + id + "'."
            );
        }

        return DataResult.success(serializer);
    }

    private static void register(GenericSerializer<? extends MutationType> serializer) {
        SERIALIZERS.put(serializer.id(), serializer);
    }

}
