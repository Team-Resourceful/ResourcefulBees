package com.teamresourceful.resourcefulbees.api.data.trait;

import com.mojang.serialization.MapCodec;

public interface TraitAbilityType<T extends TraitAbility> {

    MapCodec<T> codec();
}