package com.teamresourceful.resourcefulbees.common.blockentity.base;

import com.teamresourceful.resourcefulbees.common.mixin.accessors.BeehiveEntityAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class BlockBee {
    public final Component displayName;
    public final int minOccupationTicks;
    public final CompoundTag entityData;

    private boolean locked;
    private int ticksInHive;

    public BlockBee(CompoundTag entityData, int ticksInHive, int minOccupationTicks, Component displayName, boolean locked) {
        this(entityData, ticksInHive, minOccupationTicks, displayName);
        this.locked = locked;
    }

    public BlockBee(CompoundTag entityData, int ticksInHive, int minOccupationTicks, Component displayName) {
        BeehiveEntityAccessor.callRemoveIgnoredBeeTags(entityData);
        this.entityData = entityData;
        this.ticksInHive = ticksInHive;
        this.minOccupationTicks = minOccupationTicks;
        this.displayName = displayName;
    }

    public boolean isLocked() {
        return locked;
    }

    public void toggleLocked() {
        setLocked(!locked);
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getTicksInHive() {
        return ticksInHive;
    }

    public void setTicksInHive(int ticksInHive) {
        this.ticksInHive = ticksInHive;
    }
}
