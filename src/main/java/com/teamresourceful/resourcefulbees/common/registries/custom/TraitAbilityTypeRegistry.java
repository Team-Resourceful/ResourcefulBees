package com.teamresourceful.resourcefulbees.common.registries.custom;

import com.teamresourceful.resourcefulbees.api.data.trait.TraitAbilityType;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class TraitAbilityTypeRegistry implements com.teamresourceful.resourcefulbees.api.registry.TraitAbilityTypeRegistry {

    private static final TraitAbilityTypeRegistry INSTANCE = new TraitAbilityTypeRegistry();

    private volatile Map<Identifier, TraitAbilityType<?>> registry = Map.of();

    private TraitAbilityTypeRegistry() {
    }

    public static TraitAbilityTypeRegistry get() {
        return INSTANCE;
    }

    @Override
    public synchronized boolean register(Identifier id, TraitAbilityType<?> type) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(type);

        if (registry.containsKey(id)) {
            return false;
        }

        Map<Identifier, TraitAbilityType<?>> updated = new HashMap<>(registry);
        updated.put(id, type);
        registry = Map.copyOf(updated);
        return true;
    }

    @Override
    public TraitAbilityType<?> getType(Identifier id) {
        return registry.get(id);
    }

    @Override
    public boolean hasType(Identifier id) {
        return registry.containsKey(id);
    }
}