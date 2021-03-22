package com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityOutput {
    private final EntityType<?> entityType;
    private final CompoundTag compoundNBT;
    private final double weight;
    private Entity guiEntity = null;

    public EntityOutput(EntityType<?> entityType, CompoundTag compoundNBT, double weight) {
        this.entityType = entityType;
        this.compoundNBT = compoundNBT;
        this.weight = weight;
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    public CompoundTag getCompoundNBT() {
        return compoundNBT;
    }

    public double getWeight() {
        return weight;
    }

    @OnlyIn(Dist.CLIENT)
    public Entity getGuiEntity(ClientLevel world) {
        if (guiEntity == null) guiEntity = entityType.create(world);
        return guiEntity;
    }
}
