package com.teamresourceful.resourcefulbees.api;

import com.teamresourceful.resourcefulbees.api.honeydata.HoneyData;

import java.util.Map;

public interface IHoneyRegistry {

    /**
     * Returns an unmodifiable copy of the Honey Registry.
     * This is useful for iterating over all honey without worry of changing data
     *
     *  @return Returns unmodifiable copy of honey registry.
     */
    Map<String, HoneyData> getHoneyBottles();
}
