package com.teamresourceful.resourcefulbees.common.registry.custom;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.data.trait.TraitAbility;

import java.util.HashMap;
import java.util.Locale;

public final class TraitAbilityRegistry implements com.teamresourceful.resourcefulbees.api.registry.TraitAbilityRegistry {

    private static final TraitAbilityRegistry INSTANCE = new TraitAbilityRegistry();

    private final HashMap<String, TraitAbility> registry = new HashMap<>();
    private boolean closed = false;

    private TraitAbilityRegistry() {
        // Single instanced classes do not need to be able to be extended
    }

    public static TraitAbilityRegistry getRegistry() {
        return INSTANCE;
    }

    @Override
    public boolean register(String name, TraitAbility consumer) {
        if (closed || hasAbility(name)) {
            ResourcefulBees.LOGGER.error("Trait Ability is already registered or registration is closed: {}", name);
            return false;
        }
        registry.put(name.toLowerCase(Locale.ROOT), consumer);
        return true;
    }

    @Override
    public boolean hasAbility(String name) {
        return registry.containsKey(name.toLowerCase(Locale.ROOT));
    }

    @Override
    public TraitAbility getAbility(String name) {
        return registry.get(name.toLowerCase(Locale.ROOT));
    }

    /**
     * Closes the registry and prevents any further registrations.
     */
    public void close() {
        this.closed = true;
    }
}
