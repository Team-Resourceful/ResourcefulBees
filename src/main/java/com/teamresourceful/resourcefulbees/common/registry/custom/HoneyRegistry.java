package com.teamresourceful.resourcefulbees.common.registry.custom;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.IHoneyRegistry;
import com.teamresourceful.resourcefulbees.api.honeydata.HoneyData;

import java.util.*;

public class HoneyRegistry implements IHoneyRegistry {

    private boolean generate = true;
    private final Map<String, JsonObject> rawHoneyData = new LinkedHashMap<>();
    private final Map<String, HoneyData> honeyInfo = new LinkedHashMap<>();

    private static final HoneyRegistry INSTANCE = new HoneyRegistry();

    /**
     * Return the instance of this class. This is useful for calling methods to the mod from a static or threaded context.
     *
     * @return Instance of this class
     */
    public static HoneyRegistry getRegistry() {
        return INSTANCE;
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
        return Collections.unmodifiableSet(new HashSet<>(honeyInfo.values()));
    }

    /**
     * Registers the supplied Honey Type and associated data to the mod.
     * If the bee already exists in the registry the method will return false.
     *
     * @param honeyType Honey Type of the honey being registered.
     * @param honeyData HoneyData of the honey being registered
     * @return Returns false if bee already exists in the registry.
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean registerHoney(String honeyType, HoneyData honeyData) {
        honeyInfo.putIfAbsent(honeyType, honeyData);
        return true;
    }

    public Map<String, JsonObject> getRawHoney() {
        return rawHoneyData;
    }

    public void regenerateHoneyData() {
        HoneyRegistry.getRegistry().getRawHoney().forEach((s, json) ->
                honeyInfo.compute(s, (name, data) -> HoneyData.codec(name).parse(JsonOps.INSTANCE, json)
                .getOrThrow(false, e -> ResourcefulBees.LOGGER.error("Could not create Custom Honey Data for {} honey", name))));
    }
}
