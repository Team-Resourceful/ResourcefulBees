package com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs;

import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;

public class ItemOutput {
    private final Item item;
    private final CompoundNBT compoundNBT;
    private final double weight;

    public ItemOutput(Item item, CompoundNBT compoundNBT, double weight) {
        this.item = item;
        this.compoundNBT = compoundNBT;
        this.weight = weight;
    }

    public CompoundNBT getCompoundNBT() {
        return compoundNBT;
    }

    public Item getItem() {
        return item;
    }

    public double getWeight() {
        return weight;
    }
}
