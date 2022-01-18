package com.teamresourceful.resourcefulbees.common.utils;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TextComponentCodec {

    private TextComponentCodec()  {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final Codec<Component> CODEC = Codec.PASSTHROUGH.comapFlatMap(TextComponentCodec::decodeData, TextComponentCodec::encodeData);

    private static DataResult<Component> decodeData(Dynamic<?> dynamic) {
        if (dynamic.getValue() instanceof JsonElement element) {
            MutableComponent component = Component.Serializer.fromJson(element);
            return component != null ? DataResult.success(component) : DataResult.error("component became null");
        }
        return DataResult.error("value was some how not a JsonElement");
    }

    private static Dynamic<JsonElement> encodeData(Component component) {
        return new Dynamic<>(JsonOps.INSTANCE, Component.Serializer.toJsonTree(component));
    }

}
