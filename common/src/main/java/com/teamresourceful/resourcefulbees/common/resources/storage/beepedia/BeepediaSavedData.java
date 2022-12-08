package com.teamresourceful.resourcefulbees.common.resources.storage.beepedia;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class BeepediaSavedData extends SavedData {

    private static final BeepediaSavedData CLIENT_SIDE = new BeepediaSavedData();

    private final Map<UUID, BeepediaData> players = new LinkedHashMap<>();

    public BeepediaSavedData() {}

    public BeepediaSavedData(CompoundTag tag) {
        for (String key : tag.getAllKeys()) {
            players.put(UUID.fromString(key), BeepediaData.of(tag.getCompound(key)));
        }
    }

    public static BeepediaSavedData read(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return CLIENT_SIDE;
        }
        return read(serverLevel.getServer().overworld().getDataStorage());
    }

    public static BeepediaSavedData read(DimensionDataStorage storage) {
        return storage.computeIfAbsent(BeepediaSavedData::new, BeepediaSavedData::new, "beepedia");
    }

    public static BeepediaData getBeepediaData(Player player) {
        return read(player.level).players.computeIfAbsent(player.getUUID(), ignored -> new BeepediaData());
    }

    public static void addBee(Player player, String bee) {
        BeepediaSavedData data = read(player.level);
        data.players.computeIfAbsent(player.getUUID(), ignored -> new BeepediaData()).addBee(bee);
        data.setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        for (Map.Entry<UUID, BeepediaData> entry : players.entrySet()) {
            if (entry.getValue().hasBees()) {
                tag.put(entry.getKey().toString(), entry.getValue().save());
            }
        }
        return tag;
    }
}
