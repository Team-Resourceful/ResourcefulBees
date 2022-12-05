package com.teamresourceful.resourcefulbees.api.registry;

import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.honey.CustomHoneyData;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public interface HoneyRegistry {

    static HoneyRegistry get() {
        return ResourcefulBeesAPI.getRegistry().getHoneyRegistry();
    }

    /**
     * Returns a HoneyBottleData object for the given honey type.
     *
     * @param honey Honey type for which HoneyData is requested.
     * @return Returns a HoneyBottleData object for the given bee type.
     */
    CustomHoneyData getHoneyData(String honey);

    /**
     * Returns whether the given honey type is registered.
     *
     * @param name Honey type to check.
     * @return Returns true if the given honey type is registered.
     */
    boolean containsHoney(String name);

    /**
     * Returns an unmodifiable copy of the Honey Registry.
     * This is useful for iterating over all honey without worry of changing data
     *
     *  @return Returns unmodifiable copy of honey registry.
     */
    Map<String, CustomHoneyData> getHoneyBottles();

    /**
     * Returns a set containing all registered HoneyBottleData.
     * This is useful for iterating over all honey without worry of changing data
     *
     * @return Returns a set containing all registered HoneyBottleData.
     */
    Set<CustomHoneyData> getSetOfHoney();

    /**
     * A helper method that returns a stream using the {@link HoneyRegistry#getSetOfHoney()} method.
     */
    Stream<CustomHoneyData> getStreamOfHoney();

    /**
     * @return Returns a set containing all registered honeys ids.
     */
    Set<String> getHoneyTypes();
}
