package com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;

public class ItemOutput {
    private final Item item;
    private final CompoundTag compoundNBT;
    private final double weight;

    public ItemOutput(Item item, CompoundTag compoundNBT, double weight) {
        this.item = item;
        this.compoundNBT = compoundNBT;
        this.weight = weight;
    }

    public CompoundTag getCompoundNBT() {
        return compoundNBT;
    }

    public Item getItem() {
        return item;
    }

    public double getWeight() {
        return weight;
    }
}
