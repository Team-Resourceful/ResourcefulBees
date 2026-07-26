package com.teamresourceful.resourcefulbees.common.recipes.centrifuge.outputs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;

public record FluidOutput(FluidStackTemplate fluid, double weight) implements AbstractOutput<FluidStack> {

    public static final Codec<FluidOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FluidStackTemplate.CODEC.fieldOf("fluid").forGetter(FluidOutput::fluid),
            Codec.doubleRange(1.0d, Double.MAX_VALUE).fieldOf("weight").orElse(1.0d).forGetter(FluidOutput::weight)
    ).apply(instance, FluidOutput::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidOutput> STREAM_CODEC = StreamCodec.composite(
            FluidStackTemplate.STREAM_CODEC,
            FluidOutput::fluid,
            ByteBufCodecs.DOUBLE,
            FluidOutput::weight,
            FluidOutput::new
    );

    public FluidStack multiply(int factor) {
        return new FluidStack(this.fluid.fluid(), this.fluid.amount() * factor, this.fluid.components());
    }
}
