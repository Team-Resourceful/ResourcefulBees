package com.teamresourceful.resourcefulbees.common.setup.data.traitdata;

import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefulbees.api.data.trait.TraitAbility;
import net.minecraft.resources.Identifier;

import java.util.Map;

public record PreparedTraitData(
        Map<Identifier, TraitAbility> abilities,
        Map<Identifier, Trait> traits
) {
}