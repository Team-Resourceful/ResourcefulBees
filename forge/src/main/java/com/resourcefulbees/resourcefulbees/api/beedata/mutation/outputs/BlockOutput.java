package com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;

public class BlockOutput {

    private final Block block;
    private final CompoundTag compoundNBT;
    private final double weight;

    public BlockOutput(Block block, CompoundTag compoundNBT, double weight) {
        this.block = block;
        this.compoundNBT = compoundNBT;
        this.weight = weight;
    }

    public CompoundTag getCompoundNBT() {
        return compoundNBT;
    }

    public Block getBlock() {
        return block;
    }

    public double getWeight() {
        return weight;
    }
}
