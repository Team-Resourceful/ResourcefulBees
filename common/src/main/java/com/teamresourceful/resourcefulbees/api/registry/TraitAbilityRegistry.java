package com.teamresourceful.resourcefulbees.api.registry;

import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.trait.TraitAbility;

public interface TraitAbilityRegistry {

    static TraitAbilityRegistry get() {
        return ResourcefulBeesAPI.getRegistry().getTraitAbilityRegistry();
    }

    boolean register(String name, TraitAbility consumer);
    boolean hasAbility(String name);
    TraitAbility getAbility(String name);
}
