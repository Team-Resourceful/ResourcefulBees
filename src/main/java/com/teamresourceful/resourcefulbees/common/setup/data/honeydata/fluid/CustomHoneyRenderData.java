package com.teamresourceful.resourcefulbees.common.setup.data.honeydata.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyRenderData;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.common.color.Color;
import net.minecraft.resources.ResourceLocation;

public record CustomHoneyRenderData(
        Color color,
        ResourceLocation still,
        ResourceLocation flowing,
        ResourceLocation face,
        ResourceLocation overlay
) implements HoneyRenderData {

    private static final ResourceLocation CUSTOM_FLUID_STILL = new ResourceLocation(ModConstants.MOD_ID, "block/honey/custom_honey_still");
    private static final ResourceLocation CUSTOM_FLUID_FLOWING = new ResourceLocation(ModConstants.MOD_ID, "block/honey/custom_honey_flow");
    private static final ResourceLocation CUSTOM_FLUID_UNDERWATER = new ResourceLocation(ModConstants.MOD_ID, "textures/block/honey/custom_honey_underwater.png");

    public static final CustomHoneyRenderData DEFAULT = new CustomHoneyRenderData(Color.DEFAULT, CUSTOM_FLUID_STILL, CUSTOM_FLUID_FLOWING, CUSTOM_FLUID_FLOWING, CUSTOM_FLUID_UNDERWATER);

    public static final Codec<HoneyRenderData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Color.CODEC.fieldOf("color").forGetter(HoneyRenderData::color),
            ResourceLocation.CODEC.optionalFieldOf("still", CUSTOM_FLUID_STILL).forGetter(HoneyRenderData::still),
            ResourceLocation.CODEC.optionalFieldOf("flowing", CUSTOM_FLUID_FLOWING).forGetter(HoneyRenderData::flowing),
            ResourceLocation.CODEC.optionalFieldOf("face", CUSTOM_FLUID_FLOWING).forGetter(HoneyRenderData::face),
            ResourceLocation.CODEC.optionalFieldOf("overlay", CUSTOM_FLUID_UNDERWATER).forGetter(HoneyRenderData::overlay)
    ).apply(instance, CustomHoneyRenderData::new));
}
