package com.teamresourceful.resourcefulbees.api.beedata.outputs;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

public class EntityOutput extends AbstractOutput{
    private final EntityType<?> entityType;
    private final Optional<CompoundTag> compoundNBT;
    private Entity guiEntity = null;

    public EntityOutput(EntityType<?> entityType, Optional<CompoundTag> compoundNBT, double weight, double chance) {
        super(weight, chance);
        this.entityType = entityType;
        this.compoundNBT = compoundNBT;
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    public Optional<CompoundTag> getCompoundNBT() {
        return compoundNBT;
    }

    @OnlyIn(Dist.CLIENT)
    public Entity getGuiEntity(ClientLevel world) {
        if (guiEntity == null) guiEntity = entityType.create(world);
        return guiEntity;
    }
}
