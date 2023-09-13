package com.teamresourceful.resourcefulbees.common.util.bytecodecs;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import net.minecraft.core.registries.BuiltInRegistries;

public final class ByteCodecExtras {

    public static final ByteCodec<FluidHolder> FLUID_HOLDER = ObjectByteCodec.create(
            ByteCodec.VAR_INT.map(BuiltInRegistries.FLUID::byId, BuiltInRegistries.FLUID::getId).fieldOf(FluidHolder::getFluid),
            ByteCodec.VAR_LONG.fieldOf(FluidHolder::getFluidAmount),
            ExtraByteCodecs.NULLABLE_COMPOUND_TAG.fieldOf(FluidHolder::getCompound),
            FluidHolder::of
    );

}
