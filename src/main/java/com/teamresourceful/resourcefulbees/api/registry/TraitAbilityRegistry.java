package com.teamresourceful.resourcefulbees.api.registry;

import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.trait.TraitAbility;
import net.minecraft.resources.Identifier;

public interface TraitAbilityRegistry {

    static TraitAbilityRegistry get() {
        return ResourcefulBeesAPI.getRegistry().getTraitAbilityRegistry();
    }

    boolean register(Identifier name, TraitAbility consumer);
    boolean hasAbility(Identifier name);
    TraitAbility getAbility(Identifier name);
}
