package com.teamresourceful.resourcefulbees.common.utils;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;

public class TextComponentCodec {

    private TextComponentCodec()  {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final Codec<ITextComponent> CODEC = Codec.PASSTHROUGH.comapFlatMap(TextComponentCodec::decodeData, TextComponentCodec::encodeData);

    private static DataResult<ITextComponent> decodeData(Dynamic<?> dynamic) {
        Object object = dynamic.getValue();
        if (object instanceof JsonElement) {
            IFormattableTextComponent component = ITextComponent.Serializer.fromJson(((JsonElement) object));
            return component != null ? DataResult.success(component) : DataResult.error("component became null");
        } else {
            return DataResult.error("value was some how not a JsonElement");
        }
    }

    private static Dynamic<JsonElement> encodeData(ITextComponent component) {
        return new Dynamic<>(JsonOps.INSTANCE, ITextComponent.Serializer.toJsonTree(component));
    }

}
