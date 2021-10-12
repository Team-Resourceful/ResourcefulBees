package com.teamresourceful.resourcefulbees.api.beedata.mutation;


import com.teamresourceful.resourcefulbees.api.beedata.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.utils.RandomCollection;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;

import java.util.Collections;
import java.util.List;

public class ItemMutation {
    private final EntityType<?> parent;
    private final List<Block> inputs;
    private final RandomCollection<ItemOutput> outputs;
    private final int mutationCount;

    public ItemMutation(EntityType<?> parent, Block input, RandomCollection<ItemOutput> outputs, int mutationCount) {
        this.parent = parent;
        this.inputs = Collections.singletonList(input);
        this.outputs = outputs;
        this.mutationCount = mutationCount;
    }

    public EntityType<?> getParent() {
        return parent;
    }

    public List<Block> getInputs() {
        return inputs;
    }

    public RandomCollection<ItemOutput> getOutputs() {
        return outputs;
    }

    public int getMutationCount() {
        return mutationCount;
    }
}
