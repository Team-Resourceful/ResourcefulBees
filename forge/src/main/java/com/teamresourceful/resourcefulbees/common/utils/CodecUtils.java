package com.teamresourceful.resourcefulbees.common.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;

import java.util.Optional;

public final class CodecUtils {

    private CodecUtils() {
        throw new UtilityClassError();
    }

    public static final Codec<FluidStack> FLUID_STACK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registry.FLUID.byNameCodec().fieldOf("id").forGetter(FluidStack::getFluid),
            Codec.INT.fieldOf("amount").orElse(1000).forGetter(FluidStack::getAmount),
            CompoundTag.CODEC.optionalFieldOf("nbt").forGetter(o -> Optional.ofNullable(o.getTag()))
    ).apply(instance, (fluid, amount, tag) -> new FluidStack(fluid, amount, tag.orElse(null))));
}
