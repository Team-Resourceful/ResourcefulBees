package com.teamresourceful.resourcefulbees.common.capabilities.beepedia;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class BeepediaBeeData implements INBTSerializable<CompoundTag> {

    private long timeFound;

    public BeepediaBeeData(long timeFound) {
        this.timeFound = timeFound;
    }

    public static BeepediaBeeData of(CompoundTag nbt) {
        BeepediaBeeData data = new BeepediaBeeData(0);
        data.deserializeNBT(nbt);
        return data;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("TimeFound", this.timeFound);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.timeFound = nbt.getLong("TimeFound");
    }
}
