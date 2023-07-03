package com.teamresourceful.resourcefulbees.common.fluids;

import earth.terrarium.botarium.common.registry.fluid.FluidInformation;
import earth.terrarium.botarium.common.registry.fluid.FluidProperties;
import earth.terrarium.botarium.common.registry.fluid.FluidSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public abstract class WrappedFluidInformation implements FluidInformation {

    protected final FluidProperties properties;

    public WrappedFluidInformation(FluidProperties.Builder builder) {
        this.properties = builder.build(null);
    }

    @Override
    public double motionScale() {
        return properties.motionScale();
    }

    @Override
    public boolean canPushEntity() {
        return properties.canPushEntity();
    }

    @Override
    public boolean canSwim() {
        return properties.canSwim();
    }

    @Override
    public boolean canDrown() {
        return properties.canDrown();
    }

    @Override
    public float fallDistanceModifier() {
        return properties.fallDistanceModifier();
    }

    @Override
    public boolean canExtinguish() {
        return properties.canExtinguish();
    }

    @Override
    public boolean canConvertToSource() {
        return properties.canConvertToSource();
    }

    @Override
    public boolean supportsBloating() {
        return properties.supportsBloating();
    }

    @Override
    public BlockPathTypes pathType() {
        return properties.pathType();
    }

    @Override
    public BlockPathTypes adjacentPathType() {
        return properties.adjacentPathType();
    }

    @Override
    public boolean canHydrate() {
        return properties.canHydrate();
    }

    @Override
    public int lightLevel() {
        return properties.lightLevel();
    }

    @Override
    public int density() {
        return properties.density();
    }

    @Override
    public int temperature() {
        return properties.temperature();
    }

    @Override
    public int viscosity() {
        return properties.viscosity();
    }

    @Override
    public Rarity rarity() {
        return properties.rarity();
    }

    @Override
    public FluidSounds sounds() {
        return properties.sounds();
    }

    @Override
    public ResourceLocation still() {
        return properties.still();
    }

    @Override
    public ResourceLocation flowing() {
        return properties.flowing();
    }

    @Override
    public ResourceLocation overlay() {
        return properties.overlay();
    }

    @Override
    public ResourceLocation screenOverlay() {
        return properties.screenOverlay();
    }

    @Override
    public int tintColor() {
        return properties.tintColor();
    }

    @Override
    public int tickDelay() {
        return properties.tickDelay();
    }

    @Override
    public int slopeFindDistance() {
        return properties.slopeFindDistance();
    }

    @Override
    public int dropOff() {
        return properties.dropOff();
    }

    @Override
    public float explosionResistance() {
        return properties.explosionResistance();
    }

    @Override
    public boolean canPlace() {
        return properties.canPlace();
    }

    @Override
    public FluidProperties toProperties() {
        return new FluidProperties(
                id(),
                motionScale(),
                canPushEntity(),
                canSwim(),
                canDrown(),
                fallDistanceModifier(),
                canExtinguish(),
                canConvertToSource(),
                supportsBloating(),
                pathType(),
                adjacentPathType(),
                canHydrate(),
                lightLevel(),
                density(),
                temperature(),
                viscosity(),
                rarity(),
                sounds(),
                still(),
                flowing(),
                overlay(),
                screenOverlay(),
                tintColor(),
                tickDelay(),
                slopeFindDistance(),
                dropOff(),
                explosionResistance(),
                canPlace()
        );
    }
}
