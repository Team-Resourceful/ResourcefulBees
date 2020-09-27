package com.resourcefulbees.resourcefulbees.api;

import net.minecraftforge.fml.ModLoadingContext;

public class ResourcefulBeesAPI {

    public static final String MOD_ID = "resourcefulbees";
    private static IBeeRegistry beeRegistry;

    public static void setBeeRegistry(IBeeRegistry beeRegistry) {
        if (ResourcefulBeesAPI.beeRegistry == null && ModLoadingContext.get().getActiveContainer().getModId().equals(MOD_ID)) {
            ResourcefulBeesAPI.beeRegistry = beeRegistry;
        }
    }

    public static IBeeRegistry getBeeRegistry(){
        return beeRegistry;
    }
}
