package com.resourcefulbees.resourcefulbees.api.beedata.mutation;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.resourcefulbees.resourcefulbees.api.beedata.MutationData;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class MutationOutput {
    private final String outputID;
    private JsonElement nbtData;
    private transient CompoundTag nbt;
    private final double weight;

    public MutationOutput(String outputID, double weight, CompoundTag nbt) {
        this.outputID = outputID;
        this.weight = weight;
        this.nbt = nbt;
    }

    public CompoundTag getNbt() {
        return nbt == null ? initNbt() : nbt;
    }

    private CompoundTag initNbt() {
        nbt = nbtData == null ? new CompoundTag() : CompoundTag.CODEC.parse(JsonOps.INSTANCE, nbtData).resultOrPartial(e -> MutationData.LOGGER.warn("Could not deserialize NBT: [{}]", nbtData)).orElse(new CompoundTag());
        return nbt;
    }

    public double getWeight() {
        return weight <= 0 ? 1 : weight;
    }

    public @Nullable String getOutputID() {
        return outputID.toLowerCase(Locale.ENGLISH);
    }

    @Override
    public String toString() {
        return "[" + getOutputID() + ", " + getWeight() + ", " + getNbt().toString() + "]";
    }
}