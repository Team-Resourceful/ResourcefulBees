package com.teamresourceful.resourcefulbees.api.beedata.mutation.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.utils.RandomCollection;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MutationCodec {

    private MutationCodec() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }

    public static final Codec<IMutation> CODEC = Codec.PASSTHROUGH.comapFlatMap(MutationCodec::decodeMutation, MutationCodec::encodeMutation);
    public static final Codec<RandomCollection<IMutation>> RANDOM_COLLECTION_CODEC = CodecUtils.createSetCodec(MutationCodec.CODEC).comapFlatMap(MutationCodec::convertToRandomCollection, MutationCodec::convertToSet);


    private static DataResult<? extends IMutation> decodeMutation(Dynamic<?> dynamic){
        Object object = dynamic.getValue();
        if (object instanceof JsonObject jsonObject) {
            String type = jsonObject.get("type").getAsString();
            return switch (type.toLowerCase(Locale.ROOT)) {
                case "item" ->  ItemMutation.CODEC.parse(dynamic);
                case "block" -> BlockMutation.CODEC.parse(dynamic);
                case "entity" -> EntityMutation.CODEC.parse(dynamic);
                case "fluid" -> FluidMutation.CODEC.parse(dynamic);
                default -> DataResult.error("Invalid Mutation Type " + type);
            };
        } else {
            return DataResult.error("value was some how not a JsonObject");
        }
    }

    private static Dynamic<JsonElement> encodeMutation(IMutation mutation) {
        return new Dynamic<>(JsonOps.INSTANCE, mutation.toJson());
    }

    private static DataResult<RandomCollection<IMutation>> convertToRandomCollection(Set<IMutation> set) {
        return DataResult.success(set.stream().collect(RandomCollection.getCollector(IMutation::weight)));
    }

    private static Set<IMutation> convertToSet(RandomCollection<IMutation> randomCollection) {
        return new HashSet<>(randomCollection.getMap().values());
    }

}
