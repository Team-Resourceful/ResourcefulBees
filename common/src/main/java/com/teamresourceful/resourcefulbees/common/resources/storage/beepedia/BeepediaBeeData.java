package com.teamresourceful.resourcefulbees.common.resources.storage.beepedia;

import net.minecraft.nbt.CompoundTag;

public class BeepediaBeeData {

    private long timeFound;

    public BeepediaBeeData(long timeFound) {
        this.timeFound = timeFound;
    }

    public static BeepediaBeeData of(CompoundTag nbt) {
        BeepediaBeeData data = new BeepediaBeeData(0);
        data.load(nbt);
        return data;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("TimeFound", this.timeFound);
        return tag;
    }

    public void load(CompoundTag nbt) {
        this.timeFound = nbt.getLong("TimeFound");
    }
}
