package com.teamresourceful.resourcefulbees.common.resources.storage.beepedia;

import com.teamresourceful.resourcefullib.common.utils.SaveHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.*;

public class BeepediaSavedData extends SaveHandler {

    private static final BeepediaSavedData CLIENT_SIDE = new BeepediaSavedData();

    private final Map<UUID, BeepediaData> players = new LinkedHashMap<>();

    public static BeepediaSavedData read(Level level) {
        return read(level, CLIENT_SIDE, BeepediaSavedData::new, "beepedia");
    }

    public static BeepediaData getBeepediaData(Player player) {
        return read(player.level()).players.computeIfAbsent(player.getUUID(), ignored -> new BeepediaData());
    }

    public static void addBee(Player player, String bee) {
        addBees(player, Collections.singletonList(bee));
    }

    public static void removeBee(Player player, String bee) {
        BeepediaSavedData data = read(player.level());
        if (data.players.computeIfAbsent(player.getUUID(), ignored -> new BeepediaData()).removeBee(bee)) {
            data.setDirty();
        }
    }

    public static void clearBees(Player player) {
        BeepediaSavedData data = read(player.level());
        if (data.players.remove(player.getUUID()) != null) {
            data.setDirty();
        }
    }

    public static void addBees(Player player, Collection<String> bees) {
        BeepediaSavedData data = read(player.level());
        BeepediaData beepedia = data.players.computeIfAbsent(player.getUUID(), ignored -> new BeepediaData());
        bees.forEach(beepedia::addBee);
        data.setDirty();
    }

    @Override
    public void loadData(CompoundTag tag) {
        for (String key : tag.getAllKeys()) {
            players.put(UUID.fromString(key), BeepediaData.of(tag.getCompound(key)));
        }
    }

    @Override
    public void saveData(CompoundTag tag) {
        for (Map.Entry<UUID, BeepediaData> entry : players.entrySet()) {
            if (entry.getValue().hasBees()) {
                tag.put(entry.getKey().toString(), entry.getValue().save());
            }
        }
    }
}
