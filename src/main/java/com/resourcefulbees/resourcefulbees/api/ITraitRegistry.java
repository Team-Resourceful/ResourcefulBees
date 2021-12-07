package com.resourcefulbees.resourcefulbees.api;

import com.resourcefulbees.resourcefulbees.api.traitdata.BeeTrait;

/**
 * ITraitRegistry is implemented in TraitRegistry
 */
public interface ITraitRegistry {

    boolean register(String name, BeeTrait data);
    BeeTrait getTrait(String name);
}
