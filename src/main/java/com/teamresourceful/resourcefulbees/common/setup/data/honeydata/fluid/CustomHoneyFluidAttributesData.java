package com.teamresourceful.resourcefulbees.common.setup.data.honeydata.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyFluidAttributesData;
import com.teamresourceful.resourcefullib.common.item.LazyHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public record CustomHoneyFluidAttributesData(
        int lightLevel,
        int density,
        int temperature,
        int viscosity,
        float fallDistanceModifier,
        double motionScale,
        boolean canPushEntities,
        boolean canSwimIn,
        boolean canDrownIn,
        boolean canExtinguish,
        boolean canConvertToSource,
        boolean supportsBoating,
        boolean canHydrate,
        LazyHolder<SoundEvent> bucketFill,
        LazyHolder<SoundEvent> bucketEmpty
) implements HoneyFluidAttributesData {

    private static final Codec<LazyHolder<SoundEvent>> SOUND_CODEC = ResourceLocation.CODEC.xmap(LazyHolder.map(BuiltInRegistries.SOUND_EVENT), LazyHolder::getId);
    public static final CustomHoneyFluidAttributesData DEFAULT = new CustomHoneyFluidAttributesData(1, 1000, 300, 1000, 0.5f, 0.014, true, true, true, false, false, false, false, LazyHolder.of(BuiltInRegistries.SOUND_EVENT, SoundEvents.BUCKET_FILL), LazyHolder.of(BuiltInRegistries.SOUND_EVENT, SoundEvents.BUCKET_EMPTY));
    public static final Codec<HoneyFluidAttributesData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(0, 15).fieldOf("lightLevel").orElse(1).forGetter(HoneyFluidAttributesData::lightLevel),
            Codec.INT.optionalFieldOf("density", 1000).forGetter(HoneyFluidAttributesData::density),
            Codec.INT.optionalFieldOf("temperature", 300).forGetter(HoneyFluidAttributesData::temperature),
            Codec.INT.optionalFieldOf("viscosity", 1000).forGetter(HoneyFluidAttributesData::viscosity),
            Codec.FLOAT.optionalFieldOf("fallDistanceModifier", 0.5f).forGetter(HoneyFluidAttributesData::fallDistanceModifier),
            Codec.DOUBLE.optionalFieldOf("motionScale", 0.014).forGetter(HoneyFluidAttributesData::motionScale),
            Codec.BOOL.optionalFieldOf("canPushEntities", true).forGetter(HoneyFluidAttributesData::canPushEntities),
            Codec.BOOL.optionalFieldOf("canSwimIn", true).forGetter(HoneyFluidAttributesData::canSwimIn),
            Codec.BOOL.optionalFieldOf("canDrownIn", true).forGetter(HoneyFluidAttributesData::canDrownIn),
            Codec.BOOL.optionalFieldOf("canExtinguish", false).forGetter(HoneyFluidAttributesData::canExtinguish),
            Codec.BOOL.optionalFieldOf("canConvertToSource", false).forGetter(HoneyFluidAttributesData::canConvertToSource),
            Codec.BOOL.optionalFieldOf("supportsBoating", false).forGetter(HoneyFluidAttributesData::supportsBoating),
            Codec.BOOL.optionalFieldOf("canHydrate", false).forGetter(HoneyFluidAttributesData::canHydrate),
            SOUND_CODEC.optionalFieldOf("fillSound", LazyHolder.of(BuiltInRegistries.SOUND_EVENT, SoundEvents.BUCKET_FILL)).forGetter(HoneyFluidAttributesData::bucketFill),
            SOUND_CODEC.optionalFieldOf("emptySound", LazyHolder.of(BuiltInRegistries.SOUND_EVENT, SoundEvents.BUCKET_EMPTY)).forGetter(HoneyFluidAttributesData::bucketEmpty)
    ).apply(instance, CustomHoneyFluidAttributesData::new));

}
