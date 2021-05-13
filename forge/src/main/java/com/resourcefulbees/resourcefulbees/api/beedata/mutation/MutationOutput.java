package com.resourcefulbees.resourcefulbees.api.beedata.mutation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class MutationOutput {

    public static final Codec<MutationOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("outputID").forGetter(MutationOutput::getOutputID),
            Codec.INT.fieldOf("count").orElse(1).forGetter(MutationOutput::getCount),
            Codec.DOUBLE.fieldOf("weight").orElse(1d).forGetter(MutationOutput::getWeight),
            CompoundTag.CODEC.fieldOf("nbtData").orElse(new CompoundTag()).forGetter(MutationOutput::getNbt)
            ).apply(instance, MutationOutput::new));

    private final ResourceLocation outputID;
    private final CompoundTag nbt;
    private final int count;
    private final double weight;

    public MutationOutput(ResourceLocation outputID, int count, double weight, CompoundTag tag) {
        this.outputID = outputID;
        this.count = count;
        this.weight = weight;
        this.nbt = tag;
    }

    public CompoundTag getNbt() {
        return nbt;
    }

    public int getCount() {
        return count;
    }

    public double getWeight() {
        return weight;
    }

    public ResourceLocation getOutputID() {
        return outputID;
    }

    @Override
    public String toString() {
        return "[" + getOutputID() + ", " + getWeight() + ", " + getNbt().toString() + "]";
    }
}