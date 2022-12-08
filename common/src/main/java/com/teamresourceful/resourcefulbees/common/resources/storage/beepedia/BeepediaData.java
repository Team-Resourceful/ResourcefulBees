package com.teamresourceful.resourcefulbees.common.resources.storage.beepedia;

import net.minecraft.nbt.CompoundTag;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class BeepediaData {

    private final Map<String, BeepediaBeeData> bees = new HashMap<>();

    public static BeepediaData of(CompoundTag tag) {
        BeepediaData data = new BeepediaData();
        data.load(tag);
        return data;
    }

    public boolean hasBees() {
        return !bees.isEmpty();
    }

    public boolean hasBee(String id) {
        return this.bees.containsKey(id);
    }

    public void addBee(String bee) {
        bees.computeIfAbsent(bee, ignored -> new BeepediaBeeData(Instant.now().getEpochSecond()));
    }

    public CompoundTag save() {
        CompoundTag map = new CompoundTag();
        bees.forEach((key, value) -> map.put(key, value.save()));
        return map;
    }

    public void load(CompoundTag nbt) {
        this.bees.clear();
        for (String key : nbt.getAllKeys()) {
            bees.put(key, BeepediaBeeData.of(nbt.getCompound(key)));
        }
    }

    @Override
    public String toString() {
        return bees.toString();
    }
}
