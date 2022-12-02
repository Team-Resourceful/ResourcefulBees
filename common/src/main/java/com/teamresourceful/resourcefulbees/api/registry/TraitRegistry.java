package com.teamresourceful.resourcefulbees.api.registry;


import com.teamresourceful.resourcefulbees.api.data.trait.Trait;

import java.util.Map;

/**
 * ITraitRegistry is implemented in TraitRegistry
 */
public interface TraitRegistry {

    boolean register(String name, Trait data);
    Trait getTrait(String name);
    Map<String, Trait> getTraits();
}
