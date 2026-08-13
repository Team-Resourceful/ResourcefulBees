package com.teamresourceful.resourcefulbees.common.registries.custom;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.api.data.honey.CustomHoneyData;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public final class HoneyRegistry implements com.teamresourceful.resourcefulbees.api.registry.HoneyRegistry {

    private static final HoneyRegistry INSTANCE = new HoneyRegistry();

    private final Map<String, JsonObject> rawHoneyData = new LinkedHashMap<>();
    private final Map<String, CustomHoneyData> honeyInfo = new LinkedHashMap<>();

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
    public boolean containsHoney(String honey) {
        return honeyInfo.containsKey(honey);
    }

    @Override
    public CustomHoneyData getHoneyData(String honey) {
        return honeyInfo.get(honey);
    }

    public void cacheRawHoneyData(String name, JsonObject jsonObject) {
        rawHoneyData.putIfAbsent(name, jsonObject);
    }

    @Override
    public Map<String, CustomHoneyData> getHoneyBottles() {
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
    public Set<String> getHoneyTypes() {
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
    public boolean register(String honeyType, CustomHoneyData honeyData) {
        return honeyInfo.putIfAbsent(honeyType, honeyData) == null;
    }

    /**
     * Returns a Map containing the raw json data.
     *
     * @return Returns a Map containing the raw json data.
     */
    public Map<String, JsonObject> getRawHoney() {
        return Collections.unmodifiableMap(rawHoneyData);
    }
}
