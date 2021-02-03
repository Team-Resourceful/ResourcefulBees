package com.resourcefulbees.resourcefulbees.api.beedata;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
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
     * How many comb are consumed per iteration.
     */
    private final int mainInputCount;


    private final JsonElement mainNBTData;

    private transient CompoundNBT mainNBT = null;

    private final JsonElement secondaryNBTData;

    private transient CompoundNBT secondaryNBT = null;

    private final JsonElement bottleNBTData;

    private transient CompoundNBT bottleNBT = null;


    /**
     * The time (in ticks) that it takes for a honeycomb to be processed in the centrifuge.
     */
    private final int recipeTime;

    /**
     * If it should have centrifuge output.
     */
    private boolean hasCentrifugeOutput;

    /**
     * Use this when the main output is a fluid.
     */
    private final boolean hasFluidOutput;

    private CentrifugeData(String mainOutput, String secondaryOutput, String bottleOutput, float mainOutputWeight, float secondaryOutputWeight, float bottleOutputWeight, int mainOutputCount, int secondaryOutputCount, int bottleOutputCount, int mainInputCount, CompoundNBT mainNBT, CompoundNBT secondaryNBT, CompoundNBT bottleNBT, boolean hasCentrifugeOutput, int recipeTime, boolean hasFluidOutput) {
        this.mainOutput = mainOutput;
        this.secondaryOutput = secondaryOutput;
        this.bottleOutput = bottleOutput;
        this.mainOutputWeight = mainOutputWeight;
        this.secondaryOutputWeight = secondaryOutputWeight;
        this.bottleOutputWeight = bottleOutputWeight;
        this.mainOutputCount = mainOutputCount;
        this.secondaryOutputCount = secondaryOutputCount;
        this.bottleOutputCount = bottleOutputCount;
        this.mainInputCount = mainInputCount;
        this.mainNBTData = null;
        this.mainNBT = mainNBT;
        this.secondaryNBT = secondaryNBT;
        this.secondaryNBTData = null;
        this.bottleNBT = bottleNBT;
        this.bottleNBTData = null;
        this.hasCentrifugeOutput = hasCentrifugeOutput;
        this.recipeTime = recipeTime;
        this.hasFluidOutput = hasFluidOutput;
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

    public boolean hasFluidoutput() {
        return hasFluidOutput;
    }

    public CompoundNBT getMainNBT() {
        if (mainNBTData != null && mainNBT == null) {
            mainNBT = CompoundNBT.CODEC.parse(JsonOps.INSTANCE, mainNBTData).resultOrPartial(e -> LOGGER.warn(String.format("Could not deserialize NBT: [%s]", mainNBTData.toString()))).get();
        }else if (mainNBTData == null && mainNBT == null){
            mainNBT = new CompoundNBT();
        }
        if (bottleNBT == null || mainNBT.isEmpty()) return null;
        return mainNBT;
    }

    public CompoundNBT getSecondaryNBT() {
        if (secondaryNBTData != null && secondaryNBT == null) {
            secondaryNBT = CompoundNBT.CODEC.parse(JsonOps.INSTANCE, secondaryNBTData).resultOrPartial(e -> LOGGER.warn(String.format("Could not deserialize NBT: [%s]", secondaryNBTData.toString()))).get();
        }else if (mainNBTData == null && secondaryNBT == null){
            secondaryNBT = new CompoundNBT();
        }
        if (secondaryNBT == null || secondaryNBT.isEmpty()) return null;
        return secondaryNBT;
    }

    public CompoundNBT getBottleNBT() {
        if (bottleNBTData != null && bottleNBT == null) {
            bottleNBT = CompoundNBT.CODEC.parse(JsonOps.INSTANCE, bottleNBTData).resultOrPartial(e -> LOGGER.warn(String.format("Could not deserialize NBT: [%s]", bottleNBTData.toString()))).get();
        }else if (bottleNBTData == null && bottleNBT == null){
            bottleNBT = new CompoundNBT();
        }
        if (bottleNBT == null || bottleNBT.isEmpty()) return null;
        return bottleNBT;
    }

    public static class Builder {
        private final String mainOutput;
        private String secondaryOutput;
        private String bottleOutput;
        private float mainOutputWeight;
        private float secondaryOutputWeight;
        private float bottleOutputWeight;
        private int mainOutputCount;
        private int secondaryOutputCount;
        private int bottleOutputCount;
        private int mainInputCount;
        private CompoundNBT mainNBT;
        private CompoundNBT secondaryNBT;
        private CompoundNBT bottleNBT;
        private final boolean hasCentrifugeOutput;
        private int recipeTime;
        private boolean hasFluidOutput;

        public Builder setMainNBT(CompoundNBT mainNBT) {
            this.mainNBT = mainNBT;
            return this;
        }

        public Builder setSecondaryNBT(CompoundNBT secondaryNBT) {
            this.secondaryNBT = secondaryNBT;
            return this;
        }

        public Builder setBottleNBT(CompoundNBT bottleNBT) {
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

        public Builder setHasFluidoutput(boolean hasFluidOutput) {
            this.hasFluidOutput = hasFluidOutput;
            return this;
        }

        public CentrifugeData createCentrifugeData() {
            return new CentrifugeData(mainOutput, secondaryOutput, bottleOutput, mainOutputWeight, secondaryOutputWeight, bottleOutputWeight, mainOutputCount, secondaryOutputCount, bottleOutputCount, mainInputCount, mainNBT, secondaryNBT, bottleNBT, hasCentrifugeOutput, recipeTime, hasFluidOutput);
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
