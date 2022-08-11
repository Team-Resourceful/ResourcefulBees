package com.teamresourceful.resourcefulbees.common.registry.custom;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.ITraitRegistry;
import com.teamresourceful.resourcefulbees.api.beedata.traits.BeeTrait;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class TraitRegistry implements ITraitRegistry {

    private static final HashMap<String, BeeTrait> TRAIT_REGISTRY = new HashMap<>();
    private static boolean closed = false;

    private static final TraitRegistry INSTANCE = new TraitRegistry();

    public static TraitRegistry getRegistry() {
        return INSTANCE;
    }

    /**
     * Registers the supplied Trait Name and associated data to the mod.
     * If the trait already exists in the registry the method will return false.
     *
     * @param name Trait Name of the trait being registered.
     * @param data BeeTrait of the trait being registered
     * @return Returns false if trait already exists in the registry.
     */
    @Override
    public boolean register(String name, BeeTrait data) {
        if (closed || TRAIT_REGISTRY.containsKey(name)) {
            ResourcefulBees.LOGGER.error("Trait is already registered or registration is closed: {}", name);
            return false;
        }
        TRAIT_REGISTRY.put(name, data);
        return true;
    }

    /**
     * Returns a BeeTrait object for the given trait name.
     *
     * @param name Trait name for which BeeTrait is requested.
     * @return Returns a BeeTrait object for the given bee type.
     */
    @Override
    public BeeTrait getTrait(String name) {
        return TRAIT_REGISTRY.getOrDefault(name, BeeTrait.DEFAULT);
    }

    /**
     * Returns an unmodifiable copy of the Trait Registry.
     * This is useful for iterating over all traits without worry of changing data
     *
     * @return Returns unmodifiable copy of trait registry.
     */
    @Override
    public Map<String, BeeTrait> getTraits() {
        return Collections.unmodifiableMap(TRAIT_REGISTRY);
    }

    /**
     * A helper method that returns an unmodifiable set of the values contained in the internal
     * {@link BeeTrait} map. This is useful for iterating over all traits without
     * worry of changing registry data as the objects contained in the map are immutable.
     *
     * @return Returns an unmodifiable set of the values contained in the internal
     * {@link BeeTrait} map
     */
    public Set<BeeTrait> getSetOfTraits() {
        return Set.copyOf(TRAIT_REGISTRY.values());
    }

    /**
     * A helper method that returns a stream using the {@link TraitRegistry#getSetOfTraits()} method.
     */
    public Stream<BeeTrait> getStreamOfTraits() {
        return getSetOfTraits().stream();
    }

    public static void setTraitRegistryClosed() {
        closed = true;
    }
}
