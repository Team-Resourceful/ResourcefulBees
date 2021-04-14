package com.resourcefulbees.resourcefulbees.api.beedata.mutation;

import com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs.EntityOutput;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs.ItemOutput;
import com.resourcefulbees.resourcefulbees.utils.RandomCollection;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.ITag;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;

public class ItemMutation {
    private EntityType<?> parent;
    private List<Block> inputs;
    private Pair<Double, RandomCollection<ItemOutput>> outputs;
    private int mutationCount;

    public ItemMutation(EntityType<?> parent, ITag<?> inputs, Pair<Double, RandomCollection<ItemOutput>> outputs, int mutationCount) {
        this.parent = parent;
        this.inputs = (List<Block>) inputs.getValues();
        this.outputs = outputs;
        this.mutationCount = mutationCount;
    }

    public ItemMutation(EntityType<?> parent, Block input, Pair<Double, RandomCollection<ItemOutput>> outputs, int mutationCount) {
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

    public Pair<Double, RandomCollection<ItemOutput>> getOutputs() {
        return outputs;
    }

    public int getMutationCount() {
        return mutationCount;
    }
}
