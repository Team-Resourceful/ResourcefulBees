package com.teamresourceful.resourcefulbees.common.capabilities.beepedia;

import com.teamresourceful.resourcefulbees.common.capabilities.base.SyncableCapability;
import com.teamresourceful.resourcefulbees.common.utils.BeepediaUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class BeepediaData implements INBTSerializable<CompoundTag>, SyncableCapability {

    private final Map<String, BeepediaBeeData> bees = new HashMap<>();

    public boolean hasBee(String id) {
        return this.bees.containsKey(id);
    }

    public void addBee(String bee) {
        bees.computeIfAbsent(bee, ignored -> new BeepediaBeeData(Instant.now().getEpochSecond()));
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag map = new CompoundTag();
        bees.forEach((key, value) -> map.put(key, value.serializeNBT()));
        return map;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.bees.clear();
        for (String key : nbt.getAllKeys()) {
            bees.put(key, BeepediaBeeData.of(nbt.getCompound(key)));
        }
    }

    @Override
    public void onSynced(Player player) {
        if (player.level.isClientSide()) {
            BeepediaUtils.capabilityUpdated(this);
        }
    }


    @Override
    public String toString() {
        return bees.toString();
    }
}
