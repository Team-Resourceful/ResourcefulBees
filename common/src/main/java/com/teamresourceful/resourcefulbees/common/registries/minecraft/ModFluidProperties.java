package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import earth.terrarium.botarium.common.registry.fluid.FluidData;
import earth.terrarium.botarium.common.registry.fluid.FluidProperties;
import earth.terrarium.botarium.common.registry.fluid.FluidRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class ModFluidProperties {

    public static final FluidRegistry PROPERTIES = new FluidRegistry(ModConstants.MOD_ID);

    public static final FluidData HONEY = PROPERTIES.register("honey", FluidProperties.create()
            .sounds("bucket_fill", SoundEvents.BUCKET_FILL)
            .sounds("bucket_empty", SoundEvents.BUCKET_EMPTY)
            .sounds("fluid_vaporize", SoundEvents.FIRE_EXTINGUISH)
            .density(1450)
            .temperature(300)
            .viscosity(5000)
            .motionScale(0.0115)
            .fallDistanceModifier(0.15f)
            .rarity(Rarity.COMMON)
            .supportsBloating(true)
            .canHydrate(false)
            .canDrown(true)
            .canExtinguish(true)
            .canPushEntity(true)
            .canSwim(true)
            .pathType(BlockPathTypes.WATER)
            .adjacentPathType(BlockPathTypes.WATER_BORDER)
            .canConvertToSource(false)
            .tickRate(20)
            .still(new ResourceLocation(ModConstants.MOD_ID, "block/honey/honey_still"))
            .flowing(new ResourceLocation(ModConstants.MOD_ID, "block/honey/honey_flow"))
            .screenOverlay(new ResourceLocation(ModConstants.MOD_ID, "block/honey/honey_underwater.png"))
            .overlay(new ResourceLocation(ModConstants.MOD_ID, "block/honey/honey_flow"))
    );

}
