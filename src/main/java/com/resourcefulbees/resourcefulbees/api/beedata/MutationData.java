package com.resourcefulbees.resourcefulbees.api.beedata;

import com.resourcefulbees.resourcefulbees.lib.MutationTypes;

public class MutationData extends AbstractBeeData {
    /**
     * The Input that is getting mutated
     */
    private final String mutationInput;

    /**
     * The new muted output
     */
    private final String mutationOutput;

    /**
     * how many inputs in a radius get mutated
     */
    private final int mutationCount;

    /**
     * If the bee has a mutation
     */
    private boolean hasMutation;

    /**
     * What type of mutation it is
     */
    private final MutationTypes mutationType;

    private MutationData(String mutationInput, String mutationOutput, int mutationCount, boolean hasMutation, MutationTypes mutationType) {
        this.mutationInput = mutationInput;
        this.mutationOutput = mutationOutput;
        this.mutationCount = mutationCount;
        this.hasMutation = hasMutation;
        this.mutationType = mutationType;
    }

    public String getMutationInput() {
        return mutationInput;
    }

    public String getMutationOutput() {
        return mutationOutput;
    }

    public int getMutationCount() {
        return mutationCount <=0 ? 10 : mutationCount;
    }

    public boolean hasMutation() {
        return hasMutation;
    }

    public void setHasMutation(boolean hasMutation) { this.hasMutation = hasMutation; }

    public MutationTypes getMutationType() {
        return mutationType;
    }

    public static class Builder {
        private String mutationInput;
        private String mutationOutput;
        private int mutationCount;
        private final boolean hasMutation;
        private final MutationTypes mutationType;

        public Builder(boolean hasMutation, MutationTypes mutationType) {
            this.hasMutation = hasMutation;
            this.mutationType = mutationType;
        }

        public Builder setMutationInput(String mutationInput) {
            this.mutationInput = mutationInput;
            return this;
        }

        public Builder setMutationOutput(String mutationOutput) {
            this.mutationOutput = mutationOutput;
            return this;
        }

        public Builder setMutationCount(int mutationCount) {
            this.mutationCount = mutationCount;
            return this;
        }

        public MutationData createMutationData() {
            return new MutationData(mutationInput, mutationOutput, mutationCount, hasMutation, mutationType);
        }
    }

    public static MutationData createDefault() {
        return new Builder(false, MutationTypes.NONE).createMutationData();
    }
}

