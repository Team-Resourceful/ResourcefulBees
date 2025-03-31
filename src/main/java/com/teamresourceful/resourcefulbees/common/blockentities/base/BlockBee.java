package com.teamresourceful.resourcefulbees.common.blockentities.base;

import com.teamresourceful.resourcefulbees.mixin.common.BeehiveEntityAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class BlockBee {
    //TODO streamline bee storage and bee jar tags even further to eliminate redundancies
    public final Component displayName;
    public final int minOccupationTicks;
    public final CompoundTag entityData;
    public final String color;

    private boolean locked;
    private int ticksInHive;

    public BlockBee(CompoundTag entityData, int ticksInHive, int minOccupationTicks, Component displayName, String color, boolean locked) {
        this(entityData, ticksInHive, minOccupationTicks, displayName, color);
        this.locked = locked;
    }

    public BlockBee(CompoundTag entityData, int ticksInHive, int minOccupationTicks, Component displayName, String color) {
        BeehiveEntityAccessor.callRemoveIgnoredBeeTags(entityData);
        this.entityData = entityData;
        this.ticksInHive = ticksInHive;
        this.minOccupationTicks = minOccupationTicks;
        this.displayName = displayName;
        this.color = color;
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
        this.ticksInHive = Mth.clamp(ticksInHive, 0, Integer.MAX_VALUE);
    }

    //can use this and the method above for a cool enchantment or something that can accelerate or slow bees in the hive/apiary
    public void incrementTicksInHive(int amount) {
        ticksInHive = Math.min(ticksInHive + amount, Integer.MAX_VALUE - amount);
    }
}
