package com.teamresourceful.resourcefulbees.common.data.honeydata.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyFluidAttributesData;
import com.teamresourceful.resourcefullib.common.item.LazyHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;

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
            Codec.INT.fieldOf("density").orElse(1000).forGetter(HoneyFluidAttributesData::density),
            Codec.INT.fieldOf("temperature").orElse(300).forGetter(HoneyFluidAttributesData::temperature),
            Codec.INT.fieldOf("viscosity").orElse(1000).forGetter(HoneyFluidAttributesData::viscosity),
            Codec.FLOAT.fieldOf("fallDistanceModifier").orElse(0.5f).forGetter(HoneyFluidAttributesData::fallDistanceModifier),
            Codec.DOUBLE.fieldOf("motionScale").orElse(0.014).forGetter(HoneyFluidAttributesData::motionScale),
            Codec.BOOL.fieldOf("canPushEntities").orElse(true).forGetter(HoneyFluidAttributesData::canPushEntities),
            Codec.BOOL.fieldOf("canSwimIn").orElse(true).forGetter(HoneyFluidAttributesData::canSwimIn),
            Codec.BOOL.fieldOf("canDrownIn").orElse(true).forGetter(HoneyFluidAttributesData::canDrownIn),
            Codec.BOOL.fieldOf("canExtinguish").orElse(false).forGetter(HoneyFluidAttributesData::canExtinguish),
            Codec.BOOL.fieldOf("canConvertToSource").orElse(false).forGetter(HoneyFluidAttributesData::canConvertToSource),
            Codec.BOOL.fieldOf("supportsBoating").orElse(false).forGetter(HoneyFluidAttributesData::supportsBoating),
            Codec.BOOL.fieldOf("canHydrate").orElse(false).forGetter(HoneyFluidAttributesData::canHydrate),
            SOUND_CODEC.fieldOf("fillSound").orElse(LazyHolder.of(BuiltInRegistries.SOUND_EVENT, SoundEvents.BUCKET_FILL)).forGetter(HoneyFluidAttributesData::bucketFill),
            SOUND_CODEC.fieldOf("emptySound").orElse(LazyHolder.of(BuiltInRegistries.SOUND_EVENT, SoundEvents.BUCKET_EMPTY)).forGetter(HoneyFluidAttributesData::bucketEmpty)
    ).apply(instance, CustomHoneyFluidAttributesData::new));

    public static FluidType.Properties getProperties(HoneyFluidAttributesData data) {
        return FluidType.Properties.create()
                .lightLevel(data.lightLevel())
                .density(data.density())
                .temperature(data.temperature())
                .viscosity(data.viscosity())
                .fallDistanceModifier(data.fallDistanceModifier())
                .motionScale(data.motionScale())
                .canPushEntity(data.canPushEntities())
                .canSwim(data.canSwimIn())
                .canDrown(data.canDrownIn())
                .canExtinguish(data.canExtinguish())
                .canConvertToSource(data.canConvertToSource())
                .supportsBoating(data.supportsBoating())
                .canHydrate(data.canHydrate())
                .sound(SoundActions.BUCKET_FILL, data.bucketFill().get())
                .sound(SoundActions.BUCKET_EMPTY, data.bucketEmpty().get());
    }

}
