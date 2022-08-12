package com.teamresourceful.resourcefulbees.api;

import com.teamresourceful.resourcefulbees.api.trait.TraitAbility;

public interface ITraitAbilityRegistry {

    boolean register(String name, TraitAbility consumer);
    boolean hasAbility(String name);
    TraitAbility getAbility(String name);
}
