package com.teamresourceful.resourcefulbees.api.registry;

import com.teamresourceful.resourcefulbees.api.data.trait.TraitAbilityType;
import net.minecraft.resources.Identifier;

public interface TraitAbilityTypeRegistry {

    boolean register(Identifier id, TraitAbilityType<?> type);

    TraitAbilityType<?> getType(Identifier id);

    boolean hasType(Identifier id);
}