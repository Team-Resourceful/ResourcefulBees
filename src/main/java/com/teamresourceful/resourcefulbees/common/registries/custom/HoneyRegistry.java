package com.teamresourceful.resourcefulbees.common.registries.custom;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.api.data.honey.CustomHoneyData;
import net.minecraft.resources.Identifier;

import java.util.*;
import java.util.stream.Stream;

public final class HoneyRegistry implements com.teamresourceful.resourcefulbees.api.registry.HoneyRegistry {

    private static final HoneyRegistry INSTANCE = new HoneyRegistry();

    private final Map<Identifier, JsonObject> rawHoneyData = new LinkedHashMap<>();
    private final Map<Identifier, CustomHoneyData> honeyInfo = new LinkedHashMap<>();

    private HoneyRegistry() {
        // Single instanced classes do not need to be able to be extended
    }

    /**
     * @return Returns the singleton instance of the registry
     */
    public static HoneyRegistry getRegistry() {
        return INSTANCE;
    }

    @Override
    public boolean containsHoney(Identifier honey) {
        return honeyInfo.containsKey(honey);
    }

    @Override
    public boolean containsHoney(String name) {
        return honeyInfo.containsKey(Identifier.parse(name));
    }

    @Override
    public CustomHoneyData getHoneyData(Identifier honey) {
        return honeyInfo.get(honey);
    }

    @Override
    public Optional<CustomHoneyData> getOptionalHoneyData(Identifier honey) {
        return Optional.ofNullable(honeyInfo.get(honey));
    }

    public void cacheRawHoneyData(Identifier name, JsonObject jsonObject) {
        rawHoneyData.putIfAbsent(name, jsonObject);
    }

    @Override
    public Map<Identifier, CustomHoneyData> getHoneyBottles() {
        return Collections.unmodifiableMap(honeyInfo);
    }

    @Override
    public Set<CustomHoneyData> getSetOfHoney() {
        return Set.copyOf(honeyInfo.values());
    }

    @Override
    public Stream<CustomHoneyData> getStreamOfHoney() {
        return honeyInfo.values().stream();
    }

    @Override
    public Set<Identifier> getHoneyTypes() {
        return Collections.unmodifiableSet(honeyInfo.keySet());
    }

    /**
     * Registers the supplied Honey Type and associated data to the mod.
     * If the honey already exists in the registry the method will return false.
     *
     * @param honeyType Honey Type of the honey being registered.
     * @param honeyData HoneyData of the honey being registered
     * @return Returns false if honey already exists in the registry.
     */
    public boolean register(Identifier honeyType, CustomHoneyData honeyData) {
        return honeyInfo.putIfAbsent(honeyType, honeyData) == null;
    }

    /**
     * Returns a Map containing the raw json data.
     *
     * @return Returns a Map containing the raw json data.
     */
    public Map<Identifier, JsonObject> getRawHoney() {
        return Collections.unmodifiableMap(rawHoneyData);
    }
}
