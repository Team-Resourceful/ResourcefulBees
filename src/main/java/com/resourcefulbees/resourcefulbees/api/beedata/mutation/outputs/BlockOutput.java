package com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs;

import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;

public class BlockOutput {

    private final Block block;
    private final CompoundNBT compoundNBT;
    private final double weight;

    public BlockOutput(Block block, CompoundNBT compoundNBT, double weight) {
        this.block = block;
        this.compoundNBT = compoundNBT;
        this.weight = weight;
    }

    public CompoundNBT getCompoundNBT() {
        return compoundNBT;
    }

    public Block getBlock() {
        return block;
    }

    public double getWeight() {
        return weight;
    }
}
