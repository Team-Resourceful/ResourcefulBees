package com.teamresourceful.resourcefulbees.api.data.honey.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;

public record FluidAttributeData(
        int lightLevel, int density, int temperature, int viscosity,
        float fallDistanceModifier, double motionScale,
        boolean canPushEntities, boolean canSwimIn, boolean canDrownIn, boolean canExtinguish,
        boolean canConvertToSource, boolean supportsBoating, boolean canHydrate,
        SoundEvent bucketFill, SoundEvent bucketEmpty
) {

    public static final FluidAttributeData DEFAULT = new FluidAttributeData(1, 1000, 300, 1000, 0.5f, 0.014, true, true, true, false, false, false, false, SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY);

    public static final Codec<FluidAttributeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(0, 15).fieldOf("lightLevel").orElse(1).forGetter(FluidAttributeData::lightLevel),
            Codec.INT.fieldOf("density").orElse(1000).forGetter(FluidAttributeData::density),
            Codec.INT.fieldOf("temperature").orElse(300).forGetter(FluidAttributeData::temperature),
            Codec.INT.fieldOf("viscosity").orElse(1000).forGetter(FluidAttributeData::viscosity),
            Codec.FLOAT.fieldOf("fallDistanceModifier").orElse(0.5f).forGetter(FluidAttributeData::fallDistanceModifier),
            Codec.DOUBLE.fieldOf("motionScale").orElse(0.014).forGetter(FluidAttributeData::motionScale),
            Codec.BOOL.fieldOf("canPushEntities").orElse(true).forGetter(FluidAttributeData::canPushEntities),
            Codec.BOOL.fieldOf("canSwimIn").orElse(true).forGetter(FluidAttributeData::canSwimIn),
            Codec.BOOL.fieldOf("canDrownIn").orElse(true).forGetter(FluidAttributeData::canDrownIn),
            Codec.BOOL.fieldOf("canExtinguish").orElse(false).forGetter(FluidAttributeData::canExtinguish),
            Codec.BOOL.fieldOf("canConvertToSource").orElse(false).forGetter(FluidAttributeData::canConvertToSource),
            Codec.BOOL.fieldOf("supportsBoating").orElse(false).forGetter(FluidAttributeData::supportsBoating),
            Codec.BOOL.fieldOf("canHydrate").orElse(false).forGetter(FluidAttributeData::canHydrate),
            Registry.SOUND_EVENT.byNameCodec().fieldOf("fillSound").orElse(SoundEvents.BUCKET_FILL).forGetter(FluidAttributeData::bucketFill),
            Registry.SOUND_EVENT.byNameCodec().fieldOf("emptySound").orElse(SoundEvents.BUCKET_EMPTY).forGetter(FluidAttributeData::bucketEmpty)
    ).apply(instance, FluidAttributeData::new));

    public FluidType.Properties getProperties() {
        return FluidType.Properties.create()
                .lightLevel(lightLevel)
                .density(density)
                .temperature(temperature)
                .viscosity(viscosity)
                .fallDistanceModifier(fallDistanceModifier)
                .motionScale(motionScale)
                .canPushEntity(canPushEntities)
                .canSwim(canSwimIn)
                .canDrown(canDrownIn)
                .canExtinguish(canExtinguish)
                .canConvertToSource(canConvertToSource)
                .supportsBoating(supportsBoating)
                .canHydrate(canHydrate)
                .sound(SoundActions.BUCKET_FILL, bucketFill)
                .sound(SoundActions.BUCKET_EMPTY, bucketEmpty);
    }


}
