package com.teamresourceful.resourcefulbees.api;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraftforge.fml.ModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings("unused")
public final class ResourcefulBeesAPI {

    private ResourcefulBeesAPI() {
        throw new UtilityClassError();
    }

    public static final String MOD_ID = "resourcefulbees";
    /**
     * Instance of IBeeRegistry
     */
    private static BeeRegistry beeRegistry;

    /**
     * Instance of ITraitRegistry
     */
    private static TraitRegistry traitRegistry;

    @ApiStatus.Internal
    public static void setBeeRegistry(BeeRegistry beeRegistry) {
        if (ResourcefulBeesAPI.beeRegistry == null && ModLoadingContext.get().getActiveContainer().getModId().equals(MOD_ID)) {
            ResourcefulBeesAPI.beeRegistry = beeRegistry;
            ResourcefulBees.LOGGER.info("Bee Registry Initialized...");
        }
    }

    @ApiStatus.Internal
    public static void setTraitRegistry(TraitRegistry traitRegistry) {
        if (ResourcefulBeesAPI.traitRegistry == null && ModLoadingContext.get().getActiveContainer().getModId().equals(MOD_ID)) {
            ResourcefulBeesAPI.traitRegistry = traitRegistry;
            ResourcefulBees.LOGGER.info("Trait Registry Initialized...");
        }
    }

    public static BeeRegistry getBeeRegistry() { return beeRegistry; }

    public static TraitRegistry getTraitRegistry() { return traitRegistry; }

}
