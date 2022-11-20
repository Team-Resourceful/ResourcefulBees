package com.teamresourceful.resourcefulbees.common.registry.custom;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.traits.BeeTrait;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public final class TraitRegistry implements com.teamresourceful.resourcefulbees.api.TraitRegistry {

    private static final TraitRegistry INSTANCE = new TraitRegistry();

    private final HashMap<String, BeeTrait> registry = new HashMap<>();
    private boolean closed = false;

    private TraitRegistry() {
        // Single instanced classes do not need to be able to be extended
    }

    /**
     * @return The singleton instance of the registry
     */
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
        if (closed || registry.containsKey(name)) {
            ResourcefulBees.LOGGER.error("Trait is already registered or registration is closed: {}", name);
            return false;
        }
        registry.put(name, data);
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
        return registry.getOrDefault(name, BeeTrait.DEFAULT);
    }

    /**
     * Returns an unmodifiable copy of the Trait Registry.
     * This is useful for iterating over all traits without worry of changing data
     *
     * @return Returns unmodifiable copy of trait registry.
     */
    @Override
    public Map<String, BeeTrait> getTraits() {
        return Collections.unmodifiableMap(registry);
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
        return Set.copyOf(registry.values());
    }

    /**
     * A helper method that returns a stream using the {@link TraitRegistry#getSetOfTraits()} method.
     */
    public Stream<BeeTrait> getStreamOfTraits() {
        return getSetOfTraits().stream();
    }

    /**
     * Closes the registry and prevents any further registrations.
     */
    public void close() {
        this.closed = true;
    }
}
