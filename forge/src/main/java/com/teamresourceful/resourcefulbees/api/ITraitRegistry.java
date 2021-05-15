package com.teamresourceful.resourcefulbees.api;


import com.teamresourceful.resourcefulbees.api.beedata.BeeTrait;

/**
 * ITraitRegistry is implemented in TraitRegistry
 */
public interface ITraitRegistry {

    boolean register(String name, BeeTrait data);
    BeeTrait getTrait(String name);
}
