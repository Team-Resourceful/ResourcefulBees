package com.teamresourceful.resourcefulbees.api.registry;


import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.trait.Trait;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * TraitRegistry is implemented in TraitRegistry
 */
public interface TraitRegistry {

    static TraitRegistry get() {
        return ResourcefulBeesAPI.getRegistry().getTraitRegistry();
    }

    /**
     * Registers the supplied Trait Name and associated data to the mod.
     * If the trait already exists in the registry the method will return false.
     *
     * @param name Trait Name of the trait being registered.
     * @param data BeeTrait of the trait being registered
     * @return Returns false if trait already exists in the registry.
     */
    boolean register(String name, Trait data);

    /**
     * Returns a BeeTrait object for the given trait name.
     *
     * @param name Trait name for which BeeTrait is requested.
     * @return Returns a BeeTrait object for the given bee type.
     */
    Trait getTrait(String name);

    /**
     * Returns an unmodifiable copy of the Trait Registry.
     * This is useful for iterating over all traits without worry of changing data
     *
     * @return Returns unmodifiable copy of trait registry.
     */
    Map<String, Trait> getTraits();

    /**
     * A helper method that returns an unmodifiable set of the values contained in the internal
     * {@link Trait} map. This is useful for iterating over all traits without
     * worry of changing registry data as the objects contained in the map are immutable.
     *
     * @return Returns an unmodifiable set of the values contained in the internal
     * {@link Trait} map
     */
    Set<Trait> getSetOfTraits();

    /**
     * A helper method that returns a stream using the {@link TraitRegistry#getSetOfTraits()} method.
     */
    Stream<Trait> getStreamOfTraits();
}
