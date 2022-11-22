package com.teamresourceful.resourcefulbees.api.registry;

import com.teamresourceful.resourcefulbees.api.data.honey.HoneyData;

import java.util.Map;

public interface HoneyRegistry {

    /**
     * Returns an unmodifiable copy of the Honey Registry.
     * This is useful for iterating over all honey without worry of changing data
     *
     *  @return Returns unmodifiable copy of honey registry.
     */
    Map<String, HoneyData> getHoneyBottles();

    /**
     * Weather the codecs to register items, fluid, or blocks.
     * @return if it should register items, fluids, or blocks.
     */
    boolean canGenerate();

    /**
     * Tells the registry to set generate to false.
     */
    void stopGeneration();
}
