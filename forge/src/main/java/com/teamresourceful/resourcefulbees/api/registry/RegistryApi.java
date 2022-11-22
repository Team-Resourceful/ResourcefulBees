package com.teamresourceful.resourcefulbees.api.registry;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import net.minecraftforge.fml.ModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

public class RegistryApi {

    private BeeRegistry bee;
    private TraitRegistry trait;
    private HoneyRegistry honey;
    private TraitAbilityRegistry ability;
    private HoneycombRegistry combs;

    @ApiStatus.Internal
    public void setBeeRegistry(BeeRegistry beeRegistry) {
        if (bee == null && ModLoadingContext.get().getActiveContainer().getModId().equals(BeeConstants.MOD_ID)) {
            bee = beeRegistry;
            ResourcefulBees.LOGGER.info("Bee Registry Initialized...");
        }
    }

    @ApiStatus.Internal
    public void setTraitRegistry(TraitRegistry traitRegistry) {
        if (trait == null && ModLoadingContext.get().getActiveContainer().getModId().equals(BeeConstants.MOD_ID)) {
            trait = traitRegistry;
            ResourcefulBees.LOGGER.info("Trait Registry Initialized...");
        }
    }

    @ApiStatus.Internal
    public void setHoneyRegistry(HoneyRegistry honeyRegistry) {
        if (honey == null && ModLoadingContext.get().getActiveContainer().getModId().equals(BeeConstants.MOD_ID)) {
            honey = honeyRegistry;
            ResourcefulBees.LOGGER.info("Honey Registry Initialized...");
        }
    }

    @ApiStatus.Internal
    public void setTraitAbilityRegistry(TraitAbilityRegistry traitAbilityRegistry) {
        if (ability == null && ModLoadingContext.get().getActiveContainer().getModId().equals(BeeConstants.MOD_ID)) {
            ability = traitAbilityRegistry;
            ResourcefulBees.LOGGER.info("Trait Ability Registry Initialized...");
        }
    }

    @ApiStatus.Internal
    public void setHoneycombRegistry(HoneycombRegistry honeycombRegistry) {
        if (combs == null && ModLoadingContext.get().getActiveContainer().getModId().equals(BeeConstants.MOD_ID)) {
            combs = honeycombRegistry;
            ResourcefulBees.LOGGER.info("Honeycomb Registry Initialized...");
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
