package com.teamresourceful.resourcefulbees.api.data.honey.fluid;

import com.teamresourceful.resourcefullib.common.item.LazyHolder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Rarity;

public interface HoneyFluidAttributesData {

    int lightLevel();

    int density();

    int temperature();

    int viscosity();

    float fallDistanceModifier();

    double motionScale();

    boolean canPushEntities();

    boolean canSwimIn();

    boolean canDrownIn();

    boolean canExtinguish();

    boolean canConvertToSource();

    boolean supportsBoating();

    boolean canHydrate();

    Rarity rarity();

    LazyHolder<SoundEvent> bucketFill();

    LazyHolder<SoundEvent> bucketEmpty();

    //LazyHolder<SoundEvent> extinguishSound();
}
