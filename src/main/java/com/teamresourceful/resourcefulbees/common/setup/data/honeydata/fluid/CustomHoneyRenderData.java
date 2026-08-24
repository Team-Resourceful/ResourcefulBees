package com.teamresourceful.resourcefulbees.common.setup.data.honeydata.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyRenderData;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefullib.common.color.Color;
import net.minecraft.resources.Identifier;

public record CustomHoneyRenderData(
        Color color,
        Identifier still,
        Identifier flowing,
        Identifier face,
        Identifier overlay
) implements HoneyRenderData {

    private static final Identifier CUSTOM_FLUID_STILL = ModIdentifier.of("block/honey/custom_honey_still");
    private static final Identifier CUSTOM_FLUID_FLOWING = ModIdentifier.of("block/honey/custom_honey_flow");
    private static final Identifier CUSTOM_FLUID_UNDERWATER = ModIdentifier.of("textures/block/honey/custom_honey_underwater.png");

    public static final CustomHoneyRenderData DEFAULT = new CustomHoneyRenderData(Color.DEFAULT, CUSTOM_FLUID_STILL, CUSTOM_FLUID_FLOWING, CUSTOM_FLUID_FLOWING, CUSTOM_FLUID_UNDERWATER);

    public static final Codec<HoneyRenderData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Color.CODEC.fieldOf("color").forGetter(HoneyRenderData::color),
            Identifier.CODEC.optionalFieldOf("still", CUSTOM_FLUID_STILL).forGetter(HoneyRenderData::still),
            Identifier.CODEC.optionalFieldOf("flowing", CUSTOM_FLUID_FLOWING).forGetter(HoneyRenderData::flowing),
            Identifier.CODEC.optionalFieldOf("face", CUSTOM_FLUID_FLOWING).forGetter(HoneyRenderData::face),
            Identifier.CODEC.optionalFieldOf("overlay", CUSTOM_FLUID_UNDERWATER).forGetter(HoneyRenderData::overlay)
    ).apply(instance, CustomHoneyRenderData::new));
}
