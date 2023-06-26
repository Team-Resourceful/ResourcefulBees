package com.teamresourceful.resourcefulbees.common.fluids;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;

import java.util.function.Consumer;

public class NormalHoneyFluidType extends FluidType {

    private NormalHoneyFluidType() {
        super(Properties.create()
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
                .density(1450)
                .temperature(300)
                .viscosity(5000)
                .motionScale(0.0115)
                .fallDistanceModifier(0.15f)
                .rarity(Rarity.COMMON)
                .supportsBoating(true)
                .canHydrate(false)
                .canDrown(true)
                .canExtinguish(true)
                .canPushEntity(true)
                .canSwim(true)
                .pathType(BlockPathTypes.WATER)
                .adjacentPathType(BlockPathTypes.WATER_BORDER)
                .canConvertToSource(false)

        );
    }

    public static NormalHoneyFluidType of() {
        return new NormalHoneyFluidType();
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(NormalHoneyRenderProperties.INSTANCE);
    }

    @Override
    public boolean move(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity) {
        double d9 = entity.getY();
        float f4 = getEntitySlowdownModifier(state, entity);
        float f5 = 0.02F;

        f5 *= (float)entity.getAttribute(ForgeMod.SWIM_SPEED.get()).getValue();
        entity.moveRelative(f5, movementVector);
        entity.move(MoverType.SELF, entity.getDeltaMovement());
        Vec3 vec36 = entity.getDeltaMovement();
        if (entity.horizontalCollision && entity.onClimbable()) {
            vec36 = new Vec3(vec36.x, 0.2D, vec36.z);
        }

        entity.setDeltaMovement(vec36.multiply(f4, 0.8F, f4));
        Vec3 vec32 = entity.getFluidFallingAdjustedMovement(gravity, entity.getDeltaMovement().y <= 0, entity.getDeltaMovement());
        entity.setDeltaMovement(vec32);
        if (entity.horizontalCollision && entity.isFree(vec32.x, vec32.y + 0.6F - entity.getY() + d9, vec32.z)) {
            entity.setDeltaMovement(vec32.x, 0.3F, vec32.z);
        }
        
        return true;
    }

    private float getEntitySlowdownModifier(FluidState state, LivingEntity entity) {
        final float divisor = entity.isSprinting() ? 7_000F : 10_000F;
        var density = getDensity(state, entity.level(), entity.blockPosition());

        if (entity.level().dimensionType().ultraWarm()) {
            density *= 0.5;
        }

        return density / divisor;
    }
}
