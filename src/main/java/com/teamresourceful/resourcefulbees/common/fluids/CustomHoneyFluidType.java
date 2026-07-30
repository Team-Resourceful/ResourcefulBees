package com.teamresourceful.resourcefulbees.common.fluids;

import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyFluidAttributesData;
import com.teamresourceful.resourcefullib.common.fluid.data.FluidProperties;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.pathfinder.PathType;

public final class CustomHoneyFluidType {

    public static FluidProperties.Builder create(HoneyFluidAttributesData attributes) {
        return FluidProperties.builder()
                .supportsBoating(attributes.supportsBoating())
                .canHydrate(attributes.canHydrate())
                .canDrown(attributes.canDrownIn())
                .canExtinguish(attributes.canExtinguish())
                .canPushEntity(attributes.canPushEntities())
                .canSwim(attributes.canSwimIn())
                .pathType(PathType.WATER)
                .adjacentPathType(PathType.WATER_BORDER)
                .canConvertToSource(attributes.canConvertToSource())
                .fallDistanceModifier(0.15f)
                .motionScale(0.0115)
                .rarity(attributes.rarity())
                .viscosity(attributes.viscosity())
                .density(attributes.density())
                .temperature(attributes.temperature())
                .lightLevel(attributes.lightLevel())
                .tickRate(30)
                .sounds("bucket_fill", attributes.bucketFill().get())
                .sounds("bucket_empty", attributes.bucketEmpty().get())
                .sounds("fluid_vaporize", SoundEvents.FIRE_EXTINGUISH);
    }
}
