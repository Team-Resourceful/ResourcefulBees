package com.teamresourceful.resourcefulbees.api.beedata.mutation;

import com.teamresourceful.resourcefulbees.api.beedata.outputs.EntityOutput;
import com.teamresourceful.resourcefulbees.common.utils.RandomCollection;
import net.minecraft.world.entity.EntityType;

public class EntityMutation {
    final EntityType<?> parent;
    final EntityType<?> input;
    final RandomCollection<EntityOutput> outputs;
    private final int mutationCount;

    public EntityMutation(EntityType<?> parent, EntityType<?> input, RandomCollection<EntityOutput> outputs, int mutationCount) {
        this.parent = parent;
        this.input = input;
        this.outputs = outputs;
        this.mutationCount = mutationCount;
    }

    public EntityType<?> getParent() {
        return parent;
    }

    public EntityType<?> getInput() {
        return input;
    }

    public RandomCollection<EntityOutput> getOutputs() {
        return outputs;
    }

    public int getMutationCount() {
        return mutationCount;
    }
}