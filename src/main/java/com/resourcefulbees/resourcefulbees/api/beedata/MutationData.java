package com.resourcefulbees.resourcefulbees.api.beedata;

import com.resourcefulbees.resourcefulbees.lib.MutationTypes;

public class MutationData {
    private final String mutationInput;
    private final String mutationOutput;
    private final int mutationCount;
    private final boolean hasMutation;
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
        return mutationCount;
    }

    public boolean hasMutation() {
        return hasMutation;
    }

    public MutationTypes getMutationType() {
        return mutationType;
    }

    public static class Builder {
        private String mutationInput = "";
        private String mutationOutput = "";
        private int mutationCount = 10;
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
}

