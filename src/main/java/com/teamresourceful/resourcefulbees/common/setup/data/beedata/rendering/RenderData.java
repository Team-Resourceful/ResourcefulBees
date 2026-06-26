package com.teamresourceful.resourcefulbees.common.setup.data.beedata.rendering;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeDataSerializer;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeColorData;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeLayerData;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeLayerTexture;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeRenderData;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import net.minecraft.resources.Identifier;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public record RenderData(Set<BeeLayerData> layers, BeeColorData colorData, Identifier model, BeeLayerTexture texture, Identifier animation, float sizeModifier) implements BeeRenderData {

    private static final Identifier BASE_MODEL = ModIdentifier.of("base");
    private static final Identifier BASE_ANIMATION = ModIdentifier.of("bee");

    private static final BeeRenderData DEFAULT = new RenderData(Collections.emptySet(), ColorData.DEFAULT, BASE_MODEL, LayerTexture.MISSING_TEXTURE, BASE_ANIMATION, 1.0f);

    private static final Codec<BeeRenderData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CodecExtras.linkedSet(LayerData.CODEC).fieldOf("layers").orElse(new HashSet<>()).forGetter(BeeRenderData::layers),
            ColorData.CODEC.optionalFieldOf("ColorData", ColorData.DEFAULT).forGetter(BeeRenderData::colorData),
            Identifier.CODEC.optionalFieldOf("model", BASE_MODEL).forGetter(BeeRenderData::model),
            LayerTexture.CODEC.optionalFieldOf("texture", LayerTexture.MISSING_TEXTURE).forGetter(BeeRenderData::texture),
            Identifier.CODEC.optionalFieldOf("animation", BASE_ANIMATION).forGetter(BeeRenderData::animation),
            Codec.floatRange(0.5f, 2.0f).optionalFieldOf("sizeModifier", 1.0f).forGetter(BeeRenderData::sizeModifier)
    ).apply(instance, RenderData::new));

    public static final BeeDataSerializer<BeeRenderData> SERIALIZER = BeeDataSerializer.of(ModIdentifier.of("rendering"), 1, id -> CODEC, DEFAULT);

    @Override
    public BeeDataSerializer<BeeRenderData> serializer() {
        return SERIALIZER;
    }
}
