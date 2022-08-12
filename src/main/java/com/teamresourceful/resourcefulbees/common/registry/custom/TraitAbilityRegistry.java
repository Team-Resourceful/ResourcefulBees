package com.teamresourceful.resourcefulbees.common.registry.custom;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.ITraitAbilityRegistry;
import com.teamresourceful.resourcefulbees.api.trait.TraitAbility;

import java.util.HashMap;
import java.util.Locale;

public class TraitAbilityRegistry implements ITraitAbilityRegistry {

    private static final HashMap<String, TraitAbility> ABILITY_REGISTRY = new HashMap<>();
    private static boolean closed = false;

    public static void closeAbilityRegistry() {
        closed = true;
    }

    private static final TraitAbilityRegistry INSTANCE = new TraitAbilityRegistry();

    public static TraitAbilityRegistry getRegistry() {
        return INSTANCE;
    }

    @Override
    public boolean register(String name, TraitAbility consumer) {
        if (closed || hasAbility(name)) {
            ResourcefulBees.LOGGER.error("Trait Ability is already registered or registration is closed: {}", name);
            return false;
        }
        ABILITY_REGISTRY.put(name.toLowerCase(Locale.ROOT), consumer);
        return true;
    }

    @Override
    public boolean hasAbility(String name) {
        return ABILITY_REGISTRY.containsKey(name.toLowerCase(Locale.ROOT));
    }

    @Override
    public TraitAbility getAbility(String name) {
        return ABILITY_REGISTRY.get(name.toLowerCase(Locale.ROOT));
    }
}
