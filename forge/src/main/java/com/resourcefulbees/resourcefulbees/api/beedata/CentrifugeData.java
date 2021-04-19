package com.resourcefulbees.resourcefulbees.api.beedata;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CentrifugeData extends AbstractBeeData {

    public static final Logger LOGGER = LogManager.getLogger();

    /**
     * The main output that comes from the comb.
     */
    private final String mainOutput;

    /**
     * The second output that comes from the comb.
     */
    private final String secondaryOutput;

    /**
     * What the bottle gets converted into.
     */
    private final String bottleOutput;

    /**
     * The fluid output when no bottles are used.
     */
    private final String fluidOutput;

    /**
     * The chance that the main output comes out of the comb.
     */
    private final float mainOutputWeight;

    /**
     * The chance that the second output comes out of the comb.
     */
    private final float secondaryOutputWeight;

    /**
     * The chance that a bottle is converted to the bottleOutput.
     */
    private final float bottleOutputWeight;

    /**
     * The chance that the fluid output comes out of the comb.
     */
    private final float fluidOutputWeight;

    /**
     * How many items come out of the first output.
     */
    private final int mainOutputCount;

    /**
     * How many items come out of the second output.
     */
    private final int secondaryOutputCount;

    /**
     * How many items come out of the bottle output.
     */
    private final int bottleOutputCount;

    /**
     * How many millibuckets come out of the fluid output
     */
    private final int fluidOutputCount;

    /**
     * How many comb are consumed per iteration.
     */
    private final int mainInputCount;

    private final JsonElement mainNBTData;

    private transient CompoundTag mainNBT;

    private final JsonElement secondaryNBTData;

    private transient CompoundTag secondaryNBT;

    private final JsonElement bottleNBTData;

    private transient CompoundTag bottleNBT;


    /**
     * The time (in ticks) that it takes for a honeycomb to be processed in the centrifuge.
     */
    private final int recipeTime;

    /**
     * If it should have centrifuge output.
     */
    private boolean hasCentrifugeOutput;

    /**
     * Whether the recipe has a fluid or not.
     * if hasFluidOutput true and no fluidOutput is specified, fluidOutput will be initialised from mainOutput to keep backwards compatibility
     */
    private final boolean hasFluidOutput;

    private transient boolean mainIsFluidOutput = false;


    public boolean isMainIsFluidOutput() {
        return mainIsFluidOutput;
    }

    private CentrifugeData(String mainOutput, String secondaryOutput, String bottleOutput, String fluidOutput,
                           float mainOutputWeight, float secondaryOutputWeight, float bottleOutputWeight, float fluidOutputWeight, int mainOutputCount,
                           int secondaryOutputCount, int bottleOutputCount, int fluidOutputCount, int mainInputCount,
                           CompoundTag mainNBT, CompoundTag secondaryNBT, CompoundTag bottleNBT,
                           boolean hasCentrifugeOutput, int recipeTime, boolean hasFluidOutput) {
        super("CentrifugeData");
        this.mainOutput = mainOutput;
        this.secondaryOutput = secondaryOutput;
        this.bottleOutput = bottleOutput;
        this.mainOutputWeight = mainOutputWeight;
        this.secondaryOutputWeight = secondaryOutputWeight;
        this.bottleOutputWeight = bottleOutputWeight;
        this.fluidOutputWeight = fluidOutputWeight;
        this.mainOutputCount = mainOutputCount;
        this.secondaryOutputCount = secondaryOutputCount;
        this.bottleOutputCount = bottleOutputCount;
        this.mainInputCount = mainInputCount;
        this.fluidOutputCount = fluidOutputCount;
        this.mainNBTData = null;
        this.mainNBT = mainNBT;
        this.secondaryNBT = secondaryNBT;
        this.secondaryNBTData = null;
        this.bottleNBT = bottleNBT;
        this.bottleNBTData = null;
        this.hasCentrifugeOutput = hasCentrifugeOutput;
        this.recipeTime = recipeTime;
        this.hasFluidOutput = hasFluidOutput;
        this.fluidOutput = fluidOutput;
    }

    public String getMainOutput() {
        return mainOutput;
    }

    public String getSecondaryOutput() {
        return secondaryOutput == null ? ModItems.WAX.getId().toString() : secondaryOutput;
    }

    public String getBottleOutput() {
        return bottleOutput != null ? bottleOutput : Objects.requireNonNull(Items.HONEY_BOTTLE.getRegistryName()).toString();
    }

    public String getFluidOutput() {
        if (mainIsFluidOutput) return getMainOutput();
        //noinspection ConstantConditions
        return fluidOutput != null ? fluidOutput : Fluids.EMPTY.getRegistryName().toString();
    }

    public float getMainOutputWeight() {
        return mainOutputWeight <= 0 ? BeeConstants.DEFAULT_MAIN_OUTPUT_WEIGHT : mainOutputWeight;
    }

    public float getSecondaryOutputWeight() {
        return secondaryOutputWeight <= 0 ? BeeConstants.DEFAULT_SEC_OUTPUT_WEIGHT : secondaryOutputWeight;
    }

    public float getBottleOutputWeight() {
        return bottleOutputWeight <= 0 ? BeeConstants.DEFAULT_BOT_OUTPUT_WEIGHT : bottleOutputWeight;
    }

    public int getMainOutputCount() {
        return Math.max(1, mainOutputCount);
    }

    public int getSecondaryOutputCount() {
        return Math.max(1, secondaryOutputCount);
    }

    public int getBottleOutputCount() {
        return Math.max(1, bottleOutputCount);
    }

    public int getFluidOutputCount() {
        if (mainIsFluidOutput) return getMainOutputCount() <= 1 ? ModConstants.HONEY_PER_BOTTLE : getMainOutputCount();
        return fluidOutputCount <= 1 ? ModConstants.HONEY_PER_BOTTLE : fluidOutputCount;
    }

    public int getMainInputCount() {
        return Math.max(1, mainInputCount);
    }

    public boolean hasCentrifugeOutput() {
        return hasCentrifugeOutput;
    }

    public void setHasCentrifugeOutput(boolean hasCentrifugeOutput) {
        this.hasCentrifugeOutput = hasCentrifugeOutput;
    }

    public int getRecipeTime() {
        return this.recipeTime > 0 ? this.recipeTime : Config.GLOBAL_CENTRIFUGE_RECIPE_TIME.get();
    }

    public boolean hasFluidOutput() {
        return hasFluidOutput;
    }

    public CompoundTag getMainNBT() {
        mainNBT = getNBT(mainNBTData, mainNBT);
        return mainNBT;
    }

    public CompoundTag getSecondaryNBT() {
        secondaryNBT = getNBT(secondaryNBTData, secondaryNBT);
        return secondaryNBT;
    }

    public CompoundTag getBottleNBT() {
        bottleNBT = getNBT(bottleNBTData, bottleNBT);
        return bottleNBT;
    }

    private CompoundTag getNBT(JsonElement nbtData, CompoundTag nbt) {
        if (nbt == null) {
            nbt = CompoundTag.CODEC.parse(JsonOps.INSTANCE, nbtData)
                    .resultOrPartial(e -> {
                        if (nbtData != null) LOGGER.warn("Could not deserialize NBT: [{}]", nbtData);
                    })
                    .orElse(new CompoundTag());
        }
        if (nbt.isEmpty()) return null;
        return nbt;
    }

    public void init() {
        if (hasFluidOutput && fluidOutput == null) {
            mainIsFluidOutput = true;
        }
    }

    public float getFluidOutputWeight() {
        if (mainIsFluidOutput) return getMainOutputWeight();
        return fluidOutputWeight <= 0 ? BeeConstants.DEFAULT_FLUID_OUTPUT_WEIGHT : fluidOutputWeight;
    }


    @SuppressWarnings("unused")
    public static class Builder {
        private final String mainOutput;
        private String secondaryOutput;
        private String bottleOutput;
        private String fluidOutput;
        private float mainOutputWeight;
        private float secondaryOutputWeight;
        private float bottleOutputWeight;
        private float fluidOutputWeight;
        private int mainOutputCount;
        private int secondaryOutputCount;
        private int bottleOutputCount;
        private int mainInputCount;
        private int fluidOutputCount;
        private CompoundTag mainNBT;
        private CompoundTag secondaryNBT;
        private CompoundTag bottleNBT;
        private final boolean hasCentrifugeOutput;
        private int recipeTime;
        private boolean hasFluidOutput;


        public Builder setMainNBT(CompoundTag mainNBT) {
            this.mainNBT = mainNBT;
            return this;
        }

        public Builder setSecondaryNBT(CompoundTag secondaryNBT) {
            this.secondaryNBT = secondaryNBT;
            return this;
        }

        public Builder setBottleNBT(CompoundTag bottleNBT) {
            this.bottleNBT = bottleNBT;
            return this;
        }

        public Builder(boolean hasCentrifugeOutput, @Nullable String mainOutput) {
            this.hasCentrifugeOutput = hasCentrifugeOutput;
            this.mainOutput = mainOutput;
        }

        public Builder setSecondaryOutput(String secondaryOutput) {
            this.secondaryOutput = secondaryOutput;
            return this;
        }

        public Builder setBottleOutput(String bottleOutput) {
            this.bottleOutput = bottleOutput;
            return this;
        }

        public Builder setFluidOutputWeight(float fluidOutputWeight) {
            this.fluidOutputWeight = fluidOutputWeight;
            return this;
        }

        public Builder setMainOutputWeight(float mainOutputWeight) {
            this.mainOutputWeight = mainOutputWeight;
            return this;
        }

        public Builder setSecondaryOutputWeight(float secondaryOutputWeight) {
            this.secondaryOutputWeight = secondaryOutputWeight;
            return this;
        }

        public Builder setBottleOutputWeight(float bottleOutputWeight) {
            this.bottleOutputWeight = bottleOutputWeight;
            return this;
        }

        public Builder setMainOutputCount(int mainOutputCount) {
            this.mainOutputCount = mainOutputCount;
            return this;
        }

        public Builder setSecondaryOutputCount(int secondaryOutputCount) {
            this.secondaryOutputCount = secondaryOutputCount;
            return this;
        }

        public Builder setBottleOutputCount(int bottleOutputCount) {
            this.bottleOutputCount = bottleOutputCount;
            return this;
        }

        public Builder setMainInputCount(int mainInputCount) {
            this.mainInputCount = mainInputCount;
            return this;
        }

        public Builder setRecipeTime(int recipeTime) {
            this.recipeTime = recipeTime;
            return this;
        }

        public Builder setHasFluidOutput(boolean hasFluidOutput) {
            this.hasFluidOutput = hasFluidOutput;
            return this;
        }

        public Builder setFluidOutput(String fluidOutput) {
            this.fluidOutput = fluidOutput;
            this.hasFluidOutput = true;
            return this;
        }

        public Builder setFluidOutputCount(int fluidOutputCount) {
            this.fluidOutputCount = fluidOutputCount;
            return this;
        }

        public CentrifugeData createCentrifugeData() {
            return new CentrifugeData(mainOutput, secondaryOutput, bottleOutput, fluidOutput,
                    mainOutputWeight, secondaryOutputWeight, bottleOutputWeight, fluidOutputWeight, mainOutputCount,
                    secondaryOutputCount, bottleOutputCount, fluidOutputCount, mainInputCount,
                    mainNBT, secondaryNBT, bottleNBT,
                    hasCentrifugeOutput, recipeTime, hasFluidOutput);
        }
    }

    /**
     * Creates a default CentrifugeData for faster Bee Creation
     *
     * @return CentrifugeData that says Bee doesn't have centrifuge output
     */
    public static CentrifugeData createDefault() {
        return new Builder(false, "minecraft:stone").createCentrifugeData();
    }
}
