package com.teamresourceful.resourcefulbees.api;


import com.teamresourceful.resourcefulbees.api.beedata.traits.BeeTrait;

import java.util.Map;

/**
 * ITraitRegistry is implemented in TraitRegistry
 */
public interface ITraitRegistry {

    boolean register(String name, BeeTrait data);
    BeeTrait getTrait(String name);
    Map<String, BeeTrait> getTraits();
}
