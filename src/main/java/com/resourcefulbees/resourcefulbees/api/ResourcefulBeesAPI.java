package com.resourcefulbees.resourcefulbees.api;

import net.minecraftforge.fml.ModLoadingContext;

public class ResourcefulBeesAPI {

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
        }
    }

    public static void setTraitRegistry(ITraitRegistry traitRegistry) {
        if (ResourcefulBeesAPI.traitRegistry == null && ModLoadingContext.get().getActiveContainer().getModId().equals(MOD_ID)) {
            ResourcefulBeesAPI.traitRegistry = traitRegistry;
        }
    }

    public static IBeeRegistry getBeeRegistry() { return beeRegistry; }

    public static ITraitRegistry getTraitRegistry() { return traitRegistry; }

}
