package com.teamresourceful.resourcefulbees.common.data.beedata.data.rendering;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeColorData;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeLayerData;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeLayerTexture;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeRenderData;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeDataSerializer;
import com.teamresourceful.resourcefulbees.common.util.ModResourceLocation;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public record RenderData(Set<BeeLayerData> layers, BeeColorData colorData, ResourceLocation model, BeeLayerTexture texture, ResourceLocation animations, float sizeModifier) implements BeeRenderData {

    private static final ResourceLocation BASE_MODEL = new ResourceLocation(ResourcefulBees.MOD_ID, "geo/base.geo.json");
    private static final ResourceLocation BASE_ANIMATION = new ResourceLocation(ResourcefulBees.MOD_ID, "animations/bee.animation.json");

    private static final BeeRenderData DEFAULT = new RenderData(Collections.emptySet(), ColorData.DEFAULT, BASE_MODEL, LayerTexture.MISSING_TEXTURE, BASE_ANIMATION, 1.0f);

    private static final Codec<BeeRenderData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CodecExtras.linkedSet(LayerData.CODEC).fieldOf("layers").orElse(new HashSet<>()).forGetter(BeeRenderData::layers),
            ColorData.CODEC.fieldOf("ColorData").orElse(ColorData.DEFAULT).forGetter(BeeRenderData::colorData),
            ResourceLocation.CODEC.fieldOf("model").orElse(BASE_MODEL).forGetter(BeeRenderData::model),
            LayerTexture.CODEC.fieldOf("texture").orElse(LayerTexture.MISSING_TEXTURE).forGetter(BeeRenderData::texture),
            ResourceLocation.CODEC.fieldOf("animation").orElse(BASE_ANIMATION).forGetter(BeeRenderData::animations),
            Codec.floatRange(0.5f, 2.0f).fieldOf("sizeModifier").orElse(1.0f).forGetter(BeeRenderData::sizeModifier)
    ).apply(instance, RenderData::new));

    public static final BeeDataSerializer<BeeRenderData> SERIALIZER = BeeDataSerializer.of(new ModResourceLocation("rendering"), 1, id -> CODEC, DEFAULT);

    @Override
    public BeeDataSerializer<BeeRenderData> serializer() {
        return SERIALIZER;
    }
}
