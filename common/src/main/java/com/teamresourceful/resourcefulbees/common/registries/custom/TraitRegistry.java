package com.teamresourceful.resourcefulbees.common.registries.custom;

import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefullib.common.lib.Constants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public final class TraitRegistry implements com.teamresourceful.resourcefulbees.api.registry.TraitRegistry {

    private static final TraitRegistry INSTANCE = new TraitRegistry();

    private final HashMap<String, Trait> registry = new HashMap<>();
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

    @Override
    public boolean register(String name, Trait data) {
        if (closed || registry.containsKey(name)) {
            Constants.LOGGER.error("Trait is already registered or registration is closed: {}", name);
            return false;
        }
        registry.put(name, data);
        return true;
    }

    @Override
    public Trait getTrait(String name) {
        return registry.getOrDefault(name, Trait.DEFAULT);
    }

    @Override
    public Map<String, Trait> getTraits() {
        return Collections.unmodifiableMap(registry);
    }

    @Override
    public Set<Trait> getSetOfTraits() {
        return Set.copyOf(registry.values());
    }

    @Override
    public Stream<Trait> getStreamOfTraits() {
        return getSetOfTraits().stream();
    }

    /**
     * Closes the registry and prevents any further registrations.
     */
    public void close() {
        this.closed = true;
    }
}
