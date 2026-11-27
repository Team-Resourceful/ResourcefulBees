package com.teamresourceful.resourcefulbees.api.data.trait;

import com.mojang.serialization.MapCodec;

public record SimpleTraitAbilityType<T extends TraitAbility>(
        MapCodec<T> codec
) implements TraitAbilityType<T> {
}