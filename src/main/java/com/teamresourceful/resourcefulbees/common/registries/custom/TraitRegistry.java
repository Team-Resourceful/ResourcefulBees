package com.teamresourceful.resourcefulbees.common.registries.custom;

import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public final class TraitRegistry implements com.teamresourceful.resourcefulbees.api.registry.TraitRegistry {

    private static final TraitRegistry INSTANCE = new TraitRegistry();

    private volatile Map<Identifier, Trait> registry = Map.of();

    private TraitRegistry() {
    }

    /**
     * @return The singleton instance of the registry
     */
    public static TraitRegistry getRegistry() {
        return INSTANCE;
    }

    @Override
    public synchronized boolean register(Identifier name, Trait data) {
        if (registry.containsKey(name)) {
            ModConstants.LOGGER.error("Trait is already registered: {}", name);
            return false;
        }

        Map<Identifier, Trait> updated = new HashMap<>(registry);
        updated.put(name, data);
        registry = Map.copyOf(updated);
        return true;
    }

    @Override
    public Trait getTrait(Identifier name) {
        return registry.getOrDefault(name, Trait.DEFAULT);
    }

    @Override
    public Map<Identifier, Trait> getTraits() {
        return registry;
    }

    @Override
    public Set<Trait> getSetOfTraits() {
        return Set.copyOf(registry.values());
    }

    @Override
    public Stream<Trait> getStreamOfTraits() {
        return getSetOfTraits().stream();
    }

    public synchronized void replace(Map<Identifier, Trait> traits) {
        registry = Map.copyOf(traits);
        ModConstants.LOGGER.info("Loaded {} traits.", registry.size());
    }
}
