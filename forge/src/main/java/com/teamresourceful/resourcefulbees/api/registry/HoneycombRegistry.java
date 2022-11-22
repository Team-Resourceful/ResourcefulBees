package com.teamresourceful.resourcefulbees.api.registry;

import com.teamresourceful.resourcefulbees.api.data.honeycomb.OutputVariation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface HoneycombRegistry {

    /**
     * Returns an unmodifiable copy of the Honey Registry.
     * This is useful for iterating over all honey without worry of changing data
     *
     *  @return Returns unmodifiable copy of honey registry.
     */
    Map<String, OutputVariation> getData();

    /**
     * Returns the OutputVariation for the given name.
     *
     * @param name The name of the OutputVariation to get.
     * @return Returns the OutputVariation for the given name.
     */
    @Nullable
    OutputVariation getOutputVariation(String name);
}
