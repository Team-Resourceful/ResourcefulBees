package com.resourcefulbees.resourcefulbees.api.beedata;

import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.registry.ModItems;

import javax.annotation.Nullable;
import java.util.Objects;

public class CentrifugeData extends AbstractBeeData {
    /**
     * The main output that comes from the comb.
     */
    private final String mainOutput;

    /**
     * The second output that comes from the comb.
     */
    private String secondaryOutput;

    /**
     * What the bottle gets converted into.
     */
    private String bottleOutput;

    /**
     * The chance that the main output comes out of the comb.
     */
    private float mainOutputWeight;

    /**
     * The chance that the second output comes out of the comb.
     */
    private float secondaryOutputWeight;

    /**
     * The chance that a bottle is converted to the bottleOutput.
     */
    private float bottleOutputWeight;

    /**
     * How many items come out of the first output.
     */
    private int mainOutputCount;

    /**
     * How many items come out of the second output.
     */
    private int secondaryOutputCount;

    /**
     * How many items come out of the bottle output.
     */
    private int bottleOutputCount;

    /**
     * How many comb are consumed per iteration.
     */
    private int mainInputCount;

    /**
     * If it should have centrifuge output.
     */
    private boolean hasCentrifugeOutput;

    private CentrifugeData(String mainOutput, String secondaryOutput, String bottleOutput, float mainOutputWeight, float secondaryOutputWeight, float bottleOutputWeight, int mainOutputCount, int secondaryOutputCount, int bottleOutputCount, int mainInputCount, boolean hasCentrifugeOutput) {
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
        this.hasCentrifugeOutput = hasCentrifugeOutput;
    }

    public String getMainOutput() { return mainOutput; }

    public String getSecondaryOutput() { return secondaryOutput == null ? ModItems.WAX.getId().toString() : secondaryOutput; }

    public String getBottleOutput() { return bottleOutput != null ? bottleOutput : Objects.requireNonNull(net.minecraft.item.Items.HONEY_BOTTLE.getRegistryName()).toString(); }

    public float getMainOutputWeight() { return mainOutputWeight <= 0 ? BeeConstants.DEFAULT_MAIN_OUTPUT_WEIGHT : mainOutputWeight; }

    public float getSecondaryOutputWeight() { return secondaryOutputWeight <= 0 ? BeeConstants.DEFAULT_SEC_OUTPUT_WEIGHT : secondaryOutputWeight; }

    public float getBottleOutputWeight() { return bottleOutputWeight <= 0 ? BeeConstants.DEFAULT_BOT_OUTPUT_WEIGHT : bottleOutputWeight; }

    public int getMainOutputCount() { return Math.max(1, mainOutputCount); }

    public int getSecondaryOutputCount() { return Math.max(1, secondaryOutputCount); }

    public int getBottleOutputCount() { return Math.max(1, bottleOutputCount); }

    public int getMainInputCount() { return Math.max(1, mainInputCount); }

    public boolean hasCentrifugeOutput() { return hasCentrifugeOutput; }

    public void setHasCentrifugeOutput(boolean hasCentrifugeOutput) { this.hasCentrifugeOutput = hasCentrifugeOutput; }

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
        private final boolean hasCentrifugeOutput;

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

        public CentrifugeData createCentrifugeData() {
            return new CentrifugeData(mainOutput, secondaryOutput, bottleOutput, mainOutputWeight, secondaryOutputWeight, bottleOutputWeight, mainOutputCount, secondaryOutputCount, bottleOutputCount, mainInputCount, hasCentrifugeOutput);
        }
    }

    /**
     * Creates a default CentrifugeData for faster Bee Creation
     * @return CentrifugeData that says Bee doesn't have centrifuge output
     */
    public static CentrifugeData createDefault() {
        return new Builder(false, "minecraft:stone").createCentrifugeData();
    }
}
