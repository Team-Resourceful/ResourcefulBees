package com.teamresourceful.resourcefulbees.api.beedata.centrifuge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.recipe.CentrifugeRecipe;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Unmodifiable
public class CentrifugeData {
    public static final CentrifugeData DEFAULT = new CentrifugeData(0,0, Collections.emptyList(), Collections.emptyList());

    /**
     * A {@link Codec<CentrifugeData>} that can be parsed to create a
     * {@link CentrifugeData} object.
     */
    public static final Codec<CentrifugeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("recipeTime").orElse(200).forGetter(CentrifugeData::getRecipeTime),
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("inputCount").orElse(1).forGetter(CentrifugeData::getInputCount),
            CentrifugeRecipe.Output.ITEM_OUTPUT_CODEC.listOf().fieldOf("itemOutputs").orElse(new ArrayList<>()).forGetter(CentrifugeData::getItemOutputs),
            CentrifugeRecipe.Output.FLUID_OUTPUT_CODEC.listOf().fieldOf("fluidOutputs").orElse(new ArrayList<>()).forGetter(CentrifugeData::getFluidOutputs)
    ).apply(instance, CentrifugeData::new));

    protected final boolean hasCentrifugeOutput;
    protected int recipeTime;
    protected int inputCount;
    protected List<CentrifugeRecipe.Output<ItemOutput>> itemOutputs;
    protected List<CentrifugeRecipe.Output<FluidOutput>> fluidOutputs;

    public CentrifugeData(int recipeTime, int inputCount, List<CentrifugeRecipe.Output<ItemOutput>> itemOutputs, List<CentrifugeRecipe.Output<FluidOutput>> fluidOutputs) {
        this.recipeTime = recipeTime;
        this.inputCount = inputCount;
        this.itemOutputs = itemOutputs.stream().limit(3).collect(Collectors.toList());
        this.fluidOutputs = fluidOutputs.stream().limit(3).collect(Collectors.toList());
        this.hasCentrifugeOutput = hasItemOutputs() || hasFluidOutputs();
    }

    /**
     * @return Returns <tt>true</tt> if either the item outputs or fluid outputs is
     * not empty.
     */
    public boolean hasCentrifugeOutput() {
        return hasCentrifugeOutput;
    }

    /**
     *
     * @return Returns <tt>true</tt> if the centrifuge recipe has item outputs.
     */
    public boolean hasItemOutputs() {
        return !itemOutputs.isEmpty() && itemOutputs.stream().noneMatch(output -> output.getPool().isEmpty());
    }

    /**
     *
     * @return Returns <tt>true</tt> if the centrifuge recipe has fluid outputs.
     */
    public boolean hasFluidOutputs() {
        return !fluidOutputs.isEmpty() && fluidOutputs.stream().noneMatch(output -> output.getPool().isEmpty());
    }

    /**
     *
     * @return Returns the base time in ticks for this recipe before any
     * modifications are made.
     */
    public int getRecipeTime() {
        return recipeTime;
    }

    /**
     *
     * @return Returns the number of honeycombs needed to trigger a match for this recipe.
     */
    public int getInputCount() {
        return inputCount;
    }

    /**
     * The maximum number of item outputs allowed is <t>3</tt>.
     * @return Returns a {@link List} of {@link CentrifugeRecipe.Output}s.
     */
    public List<CentrifugeRecipe.Output<ItemOutput>> getItemOutputs() {
        return itemOutputs;
    }

    /**
     * The maximum number of fluid outputs allowed is <t>3</tt>.
     * @return Returns a {@link List} of {@link FluidOutput}s.
     */
    public List<CentrifugeRecipe.Output<FluidOutput>> getFluidOutputs() {
        return fluidOutputs;
    }

    public CentrifugeData toImmutable() {
        return this;
    }

    public static class Mutable extends CentrifugeData {
        public Mutable(int recipeTime, int inputCount, List<CentrifugeRecipe.Output<ItemOutput>> itemOutputs, List<CentrifugeRecipe.Output<FluidOutput>> fluidOutputs) {
            super(recipeTime, inputCount, itemOutputs, fluidOutputs);
        }

        public Mutable() {
            super(200, 1, new ArrayList<>(), new ArrayList<>());
        }

        public Mutable setRecipeTime(int recipeTime) {
            this.recipeTime = recipeTime;
            return this;
        }

        public Mutable setInputCount(int inputCount) {
            this.inputCount = inputCount;
            return this;
        }

        public Mutable setItemOutputs(List<CentrifugeRecipe.Output<ItemOutput>> itemOutputs) {
            this.itemOutputs = itemOutputs;
            return this;
        }

        public Mutable setFluidOutputs(List<CentrifugeRecipe.Output<FluidOutput>> fluidOutputs) {
            this.fluidOutputs = fluidOutputs;
            return this;
        }

        @Override
        public CentrifugeData toImmutable() {
            return new CentrifugeData(this.recipeTime, this.inputCount, this.itemOutputs, this.fluidOutputs);
        }
    }
}
