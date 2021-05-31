package com.teamresourceful.resourcefulbees.api;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.lib.constants.ModConstants;
import net.minecraftforge.fml.ModLoadingContext;

public class ResourcefulBeesAPI {

    private ResourcefulBeesAPI() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final String MOD_ID = "resourcefulbees";
    /**
     * Instance of IBeeRegistry
     */
    private static IBeeRegistry beeRegistry;

    /**
     * Instance of ITraitRegistry
     */
    private static ITraitRegistry traitRegistry;

    public static void setBeeRegistry(IBeeRegistry beeRegistry) {
        if (ResourcefulBeesAPI.beeRegistry == null && ModLoadingContext.get().getActiveContainer().getModId().equals(MOD_ID)) {
            ResourcefulBeesAPI.beeRegistry = beeRegistry;
            ResourcefulBees.LOGGER.info("Bee Registry Initialized...");
        }
    }

    public static void setTraitRegistry(ITraitRegistry traitRegistry) {
        if (ResourcefulBeesAPI.traitRegistry == null && ModLoadingContext.get().getActiveContainer().getModId().equals(MOD_ID)) {
            ResourcefulBeesAPI.traitRegistry = traitRegistry;
            ResourcefulBees.LOGGER.info("Trait Registry Initialized...");
        }
    }

    public static IBeeRegistry getBeeRegistry() { return beeRegistry; }

    //TODO provide proper access - epic
    public static ITraitRegistry getTraitRegistry() { return traitRegistry; }

}
