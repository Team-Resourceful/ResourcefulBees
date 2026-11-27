package com.teamresourceful.resourcefulbees.common.lib.defaults;

import com.teamresourceful.resourcefulbees.api.data.trait.SimpleTraitAbilityType;
import com.teamresourceful.resourcefulbees.api.data.trait.abilities.*;
import com.teamresourceful.resourcefulbees.common.lib.constants.TraitConstants;
import com.teamresourceful.resourcefulbees.common.registries.custom.TraitAbilityTypeRegistry;

public final class DefaultTraitAbilityTypes {

    private DefaultTraitAbilityTypes() {
    }

    public static void registerDefaults() {
        TraitAbilityTypeRegistry registry = TraitAbilityTypeRegistry.get();

        registry.register(TraitConstants.TELEPORT, new SimpleTraitAbilityType<>(TeleportAbility.CODEC));
        registry.register(TraitConstants.SLIMY, new SimpleTraitAbilityType<>(SlimyAbility.CODEC));
        registry.register(TraitConstants.FLAMMABLE, new SimpleTraitAbilityType<>(FlammableAbility.CODEC));
        registry.register(TraitConstants.ANGRY, new SimpleTraitAbilityType<>(AngryAbility.CODEC));
        registry.register(TraitConstants.SCULK, new SimpleTraitAbilityType<>(SculkAbility.CODEC));
        registry.register(TraitConstants.SPIDER, new SimpleTraitAbilityType<>(SpiderAbility.CODEC));
        registry.register(TraitConstants.CLINGY, new SimpleTraitAbilityType<>(ClingyAbility.CODEC));
        registry.register(TraitConstants.COURIER, new SimpleTraitAbilityType<>(CourierAbility.CODEC));
    }
}