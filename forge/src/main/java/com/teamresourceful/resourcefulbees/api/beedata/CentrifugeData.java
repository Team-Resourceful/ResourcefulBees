package com.teamresourceful.resourcefulbees.api.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.ItemOutput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CentrifugeData {

    public static final Codec<CentrifugeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("hasCentrifugeOutput").orElse(false).forGetter(CentrifugeData::hasCentrifugeOutput),
            Codec.INT.fieldOf("recipeTime").orElse(200).forGetter(CentrifugeData::getRecipeTime),
            Codec.INT.fieldOf("inputCount").orElse(1).forGetter(CentrifugeData::getInputCount),
            ItemOutput.CODEC.listOf().fieldOf("itemOutputs").orElse(new ArrayList<>()).forGetter(CentrifugeData::getItemOutputs),
            FluidOutput.CODEC.listOf().fieldOf("fluidOutputs").orElse(new ArrayList<>()).forGetter(CentrifugeData::getFluidOutputs)
    ).apply(instance, CentrifugeData::new));

    private final boolean hasCentrifugeOutput;
    private final int recipeTime;
    private final int inputCount;
    private final List<ItemOutput> itemOutputs;
    private final List<FluidOutput> fluidOutputs;

    public CentrifugeData(boolean hasCentrifugeOutput, int recipeTime, int inputCount, List<ItemOutput> itemOutputs, List<FluidOutput> fluidOutputs) {
        this.hasCentrifugeOutput = hasCentrifugeOutput;
        this.recipeTime = recipeTime;
        this.inputCount = inputCount;
        this.itemOutputs = new ArrayList<>(itemOutputs);
        this.itemOutputs.add(0, ItemOutput.EMPTY);
        this.fluidOutputs = new ArrayList<>(fluidOutputs);
        this.fluidOutputs.add(0, FluidOutput.EMPTY);
    }

    public boolean hasCentrifugeOutput() {
        return hasCentrifugeOutput;
    }

    public int getRecipeTime() {
        return recipeTime;
    }

    public int getInputCount() {
        return inputCount;
    }

    public List<ItemOutput> getItemOutputs() {
        return itemOutputs;
    }

    public List<FluidOutput> getFluidOutputs() {
        return fluidOutputs;
    }

    public static CentrifugeData createDefault() {
        return new CentrifugeData(false, 0,0, Collections.emptyList(), Collections.emptyList());
    }
}
