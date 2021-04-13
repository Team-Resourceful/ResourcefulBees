package com.resourcefulbees.resourcefulbees.api.beedata.mutation;

import com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs.EntityOutput;
import com.resourcefulbees.resourcefulbees.utils.RandomCollection;
import net.minecraft.entity.EntityType;
import org.apache.commons.lang3.tuple.Pair;

public class EntityMutation {
    EntityType<?> parent;
    EntityType<?> input;
    Pair<Double, RandomCollection<EntityOutput>> outputs;

    public EntityMutation(EntityType<?> parent, EntityType<?> input, Pair<Double, RandomCollection<EntityOutput>> outputs) {
        this.parent = parent;
        this.input = input;
        this.outputs = outputs;
    }

    public EntityType<?> getParent() {
        return parent;
    }

    public EntityType<?> getInput() {
        return input;
    }

    public Pair<Double, RandomCollection<EntityOutput>> getOutputs() {
        return outputs;
    }
}