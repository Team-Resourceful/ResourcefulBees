package com.teamresourceful.resourcefulbees.common.registry.custom;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.api.data.honey.HoneyData;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public final class HoneyRegistry implements com.teamresourceful.resourcefulbees.api.registry.HoneyRegistry {

    private static final HoneyRegistry INSTANCE = new HoneyRegistry();

    private boolean generate = true;
    private final Map<String, JsonObject> rawHoneyData = new LinkedHashMap<>();
    private final Map<String, HoneyData> honeyInfo = new LinkedHashMap<>();

    private HoneyRegistry() {
        // Single instanced classes do not need to be able to be extended
    }

    /**
     * @return Returns the singleton instance of the registry
     */
    public static HoneyRegistry getRegistry() {
        return INSTANCE;
    }

    public static boolean containsHoney(String honey) {
        return INSTANCE.honeyInfo.containsKey(honey);
    }

    /**
     * Returns a HoneyBottleData object for the given honey type.
     *
     * @param honey Honey type for which HoneyData is requested.
     * @return Returns a HoneyBottleData object for the given bee type.
     */
    public HoneyData getHoneyData(String honey) {
        return honeyInfo.get(honey);
    }

    public void cacheRawHoneyData(String name, JsonObject jsonObject) {
        rawHoneyData.computeIfAbsent(name, s -> jsonObject);
    }

    /**
     * Returns an unmodifiable copy of the Honey Registry.
     * This is useful for iterating over all honey without worry of changing data
     *
     * @return Returns unmodifiable copy of honey registry.
     */
    public Map<String, HoneyData> getHoneyBottles() {
        return Collections.unmodifiableMap(honeyInfo);
    }

    @Override
    public boolean canGenerate() {
        return generate;
    }

    @Override
    public void stopGeneration() {
        generate = false;
    }

    /**
     * Returns a set containing all registered HoneyBottleData.
     * This is useful for iterating over all honey without worry of changing data
     *
     * @return Returns a set containing all registered HoneyBottleData.
     */
    public Set<HoneyData> getSetOfHoney() {
        return Set.copyOf(honeyInfo.values());
    }

    /**
     * A helper method that returns a stream using the {@link HoneyRegistry#getSetOfHoney()} method.
     */
    public Stream<HoneyData> getStreamOfHoney() {
        return getSetOfHoney().stream();
    }

    /**
     * Registers the supplied Honey Type and associated data to the mod.
     * If the bee already exists in the registry the method will return false.
     *
     * @param honeyType Honey Type of the honey being registered.
     * @param honeyData HoneyData of the honey being registered
     * @return Returns false if bee already exists in the registry.
     */
    public boolean register(String honeyType, HoneyData honeyData) {
        if (honeyInfo.containsKey(honeyType)) return false;
        honeyInfo.putIfAbsent(honeyType, honeyData);
        return true;
    }

    public Map<String, JsonObject> getRawHoney() {
        return rawHoneyData;
    }
}
