package com.teamresourceful.resourcefulbees.api.beedata.outputs;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;

@Unmodifiable
public class EntityOutput extends AbstractOutput{
    private final EntityType<?> entityType;
    private final Optional<CompoundNBT> compoundNBT;
    private Entity guiEntity = null;

    public EntityOutput(EntityType<?> entityType, Optional<CompoundNBT> compoundNBT, double weight, double chance) {
        super(weight, chance);
        this.entityType = entityType;
        this.compoundNBT = compoundNBT;
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    public Optional<CompoundNBT> getCompoundNBT() {
        return compoundNBT;
    }

    @OnlyIn(Dist.CLIENT)
    public Entity getGuiEntity(ClientWorld world) {
        if (guiEntity == null) guiEntity = entityType.create(world);
        return guiEntity;
    }
}
