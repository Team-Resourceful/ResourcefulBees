package com.teamresourceful.resourcefulbees.common.resources.storage.beepedia;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.*;

public final class BeeDiscoveryData {

    public static final MapCodec<BeeDiscoveryData> CODEC = Codec.unboundedMap(Identifier.CODEC, DiscoveredBee.CODEC).fieldOf("bees")
            .xmap(BeeDiscoveryData::new, BeeDiscoveryData::discoveries);

    public static final StreamCodec<ByteBuf, BeeDiscoveryData> STREAM_CODEC =
            ByteBufCodecs.map(
                    HashMap::new,
                    Identifier.STREAM_CODEC,
                    DiscoveredBee.STREAM_CODEC
            ).map(
                    BeeDiscoveryData::new,
                    data -> new HashMap<>(data.discoveries())
            );

    private final Map<Identifier, DiscoveredBee> discovered;

    public BeeDiscoveryData() {
        this(new HashMap<>());
    }

    public BeeDiscoveryData(Map<Identifier, DiscoveredBee> discoveredBees) {
        this.discovered = new HashMap<>(discoveredBees);
    }

    public boolean isDiscovered(Identifier bee) {
        return discovered.containsKey(bee);
    }

    public boolean discover(Identifier bee) {
        return discovered.putIfAbsent(bee, DiscoveredBee.now()) == null;
    }

    public boolean forget(Identifier bee) {
        return discovered.remove(bee) != null;
    }

    public void clear() {
        discovered.clear();
    }

    public int size() {
        return discovered.size();
    }

    public Map<Identifier, DiscoveredBee> discoveries() {
        return Collections.unmodifiableMap(discovered);
    }

    public Set<Identifier> discoveredBees() {
        return Collections.unmodifiableSet(discovered.keySet());
    }

    public Optional<DiscoveredBee> get(Identifier bee) {
        return Optional.ofNullable(
                discovered.get(bee)
        );
    }
}