package com.resourcefulbees.resourcefulbees.api.beedata.mutation;

import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.MutationTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Mutation {
    private final MutationTypes type;
    private final String inputID;
    private final double chance;
    private final List<MutationOutput> outputs;

    public Mutation(MutationTypes type, String inputID, double chance, MutationOutput... outputs) {
        this.type = type;
        this.inputID = inputID;
        this.chance = chance;
        this.outputs = new ArrayList<>();
        this.outputs.addAll(Arrays.asList(outputs));
    }

    public MutationTypes getType() {
        return this.type;
    }

    public String getInputID() {
        return this.inputID.toLowerCase(Locale.ENGLISH);
    }

    public double getChance() {
        return chance == 0 ? 1 : Math.min(this.chance, 1);
    }

    public List<MutationOutput> getOutputs() {
        return this.outputs;
    }

    public boolean isTag() {
        return getInputID().startsWith(BeeConstants.TAG_PREFIX);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[")
                .append(getType())
                .append(", ")
                .append(getInputID())
                .append(", ")
                .append(getChance())
                .append(", [");
        outputs.forEach(builder::append);
        builder.append("]]");
        return builder.toString();
    }
}
