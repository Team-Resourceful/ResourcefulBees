package com.teamresourceful.resourcefulbees.common.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraftforge.fluids.FluidStack;

import java.util.Optional;
import java.util.function.Function;

public final class CodecUtils {

    private CodecUtils() {
        throw new UtilityClassError();
    }

    //region FluidStack Codec
    public static final Codec<FluidStack> FLUID_STACK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registry.FLUID.byNameCodec().fieldOf("id").forGetter(FluidStack::getFluid),
            Codec.INT.fieldOf("amount").orElse(1000).forGetter(FluidStack::getAmount),
            CompoundTag.CODEC.optionalFieldOf("nbt").forGetter(o -> Optional.ofNullable(o.getTag()))
    ).apply(instance, (fluid, amount, tag) -> new FluidStack(fluid, amount, tag.orElse(null))));
    //endregion

    public static Codec<UniformInt> rangedUniformIntCodec(int min, int max) {
        Codec<UniformInt> codec = RecordCodecBuilder.create(instance -> instance.group(
                Codec.intRange(min, max).fieldOf("min").forGetter(UniformInt::getMinValue),
                Codec.intRange(min, max).fieldOf("max").forGetter(UniformInt::getMaxValue)
        ).apply(instance, UniformInt::of));
        return codec.comapFlatMap(uniformInt -> uniformInt.getMaxValue() < uniformInt.getMinValue()
                ? DataResult.error("Max must be at least min, min: " + uniformInt.getMinValue()+ ", max: " + uniformInt.getMaxValue())
                : DataResult.success(uniformInt), Function.identity());
    }
}
