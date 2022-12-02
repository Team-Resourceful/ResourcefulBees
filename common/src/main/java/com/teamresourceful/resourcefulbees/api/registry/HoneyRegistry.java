package com.teamresourceful.resourcefulbees.api.registry;

import com.teamresourceful.resourcefulbees.api.data.honey.CustomHoneyData;

import java.util.Map;

public interface HoneyRegistry {

    /**
     * Returns an unmodifiable copy of the Honey Registry.
     * This is useful for iterating over all honey without worry of changing data
     *
     *  @return Returns unmodifiable copy of honey registry.
     */
    Map<String, CustomHoneyData> getHoneyBottles();
}
