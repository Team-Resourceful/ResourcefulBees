package com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs;

import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;

public class EntityOutput {
    private final EntityType<?> entityType;
    private final CompoundNBT compoundNBT;
    private final double weight;

    public EntityOutput(EntityType<?> entityType, CompoundNBT compoundNBT, double weight) {
        this.entityType = entityType;
        this.compoundNBT = compoundNBT;
        this.weight = weight;
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    public CompoundNBT getCompoundNBT() {
        return compoundNBT;
    }

    public double getWeight() {
        return weight;
    }
}
