package com.teamresourceful.resourcefulbees.api.registry;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings("unused")
@ApiStatus.NonExtendable
public class RegistryApi {

    private BeeRegistry bee;
    private TraitRegistry trait;
    private HoneyRegistry honey;
    private TraitAbilityRegistry ability;
    private HoneycombRegistry combs;

    @ApiStatus.Internal
    public void setBeeRegistry(BeeRegistry beeRegistry) {
        if (bee == null) {
            bee = beeRegistry;
            ModConstants.LOGGER.info("Bee Registry Initialized...");
        }
    }

    @ApiStatus.Internal
    public void setTraitRegistry(TraitRegistry traitRegistry) {
        if (trait == null) {
            trait = traitRegistry;
            ModConstants.LOGGER.info("Trait Registry Initialized...");
        }
    }

    @ApiStatus.Internal
    public void setHoneyRegistry(HoneyRegistry honeyRegistry) {
        if (honey == null) {
            honey = honeyRegistry;
            ModConstants.LOGGER.info("Honey Registry Initialized...");
        }
    }

    @ApiStatus.Internal
    public void setTraitAbilityRegistry(TraitAbilityRegistry traitAbilityRegistry) {
        if (ability == null) {
            ability = traitAbilityRegistry;
            ModConstants.LOGGER.info("Trait Ability Registry Initialized...");
        }
    }

    @ApiStatus.Internal
    public void setHoneycombRegistry(HoneycombRegistry honeycombRegistry) {
        if (combs == null) {
            combs = honeycombRegistry;
            ModConstants.LOGGER.info("Honeycomb Registry Initialized...");
        }
    }

    public BeeRegistry getBeeRegistry() {
        return bee;
    }

    public TraitRegistry getTraitRegistry() {
        return trait;
    }

    public HoneyRegistry getHoneyRegistry() {
        return honey;
    }

    public TraitAbilityRegistry getTraitAbilityRegistry() {
        return ability;
    }

    public HoneycombRegistry getHoneycombRegistry() {
        return combs;
    }
}
