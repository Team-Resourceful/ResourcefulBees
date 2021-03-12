package com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityOutput {
    private final EntityType<?> entityType;
    private final CompoundNBT compoundNBT;
    private final double weight;
    private Entity guiEntity = null;

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

    @OnlyIn(Dist.CLIENT)
    public Entity getGuiEntity(ClientWorld world) {
        if (guiEntity == null) guiEntity = entityType.create(world);
        return guiEntity;
    }
}
