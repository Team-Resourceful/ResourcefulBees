package com.teamresourceful.resourcefulbees.common.registries.custom;

import com.teamresourceful.resourcefulbees.api.data.trait.TraitAbility;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.resources.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class TraitAbilityRegistry implements com.teamresourceful.resourcefulbees.api.registry.TraitAbilityRegistry {

    private static final TraitAbilityRegistry INSTANCE = new TraitAbilityRegistry();

    // Java-registered abilities, including third-party registrations.
    private final Map<Identifier, TraitAbility> registeredAbilities = new LinkedHashMap<>();

    // Datapack-defined ability instances.
    private volatile Map<Identifier, TraitAbility> datapackAbilities = Map.of();

    // Combined lookup map used during gameplay.
    private volatile Map<Identifier, TraitAbility> activeAbilities = Map.of();

    private TraitAbilityRegistry() {
    }

    public static TraitAbilityRegistry getRegistry() {
        return INSTANCE;
    }

    @Override
    public synchronized boolean register(Identifier id, TraitAbility ability) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(ability);

        if (activeAbilities.containsKey(id)) {
            ModConstants.LOGGER.error("Trait ability is already registered: {}", id);
            return false;
        }

        registeredAbilities.put(id, ability);
        Map<Identifier, TraitAbility> updated = new LinkedHashMap<>(activeAbilities);
        updated.put(id, ability);
        activeAbilities = Map.copyOf(updated);

        return true;
    }

    @Override
    public boolean hasAbility(Identifier id) {
        return activeAbilities.containsKey(id);
    }

    @Override
    public TraitAbility getAbility(Identifier id) {
        return activeAbilities.get(id);
    }

    public synchronized void replaceDatapackAbilities(Map<Identifier, TraitAbility> abilities) {
        Objects.requireNonNull(abilities);

        Map<Identifier, TraitAbility> combined = new LinkedHashMap<>(registeredAbilities);

        for (var entry : abilities.entrySet()) {
            if (combined.putIfAbsent(entry.getKey(), entry.getValue()) != null) {
                throw new IllegalStateException("Datapack ability conflicts with Java-registered ability: " + entry.getKey());
            }
        }

        // Publish immutable snapshots only after validation succeeds.
        Map<Identifier, TraitAbility> updatedDatapack = Map.copyOf(abilities);

        Map<Identifier, TraitAbility> updatedActive = Map.copyOf(combined);

        datapackAbilities = updatedDatapack;
        activeAbilities = updatedActive;
    }

    public Map<Identifier, TraitAbility> getDatapackAbilities() {
        return datapackAbilities;
    }

    public Map<Identifier, TraitAbility> getAbilities() {
        return activeAbilities;
    }
}