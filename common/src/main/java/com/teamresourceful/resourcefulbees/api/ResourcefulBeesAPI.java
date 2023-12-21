package com.teamresourceful.resourcefulbees.api;

import com.teamresourceful.resourcefulbees.api.intializers.HoneyInitializerApi;
import com.teamresourceful.resourcefulbees.api.intializers.InitializerApi;
import com.teamresourceful.resourcefulbees.api.registry.RegistryApi;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;

public final class ResourcefulBeesAPI {

    private ResourcefulBeesAPI() throws UtilityClassException {
        throw new UtilityClassException();
    }

    private static final RegistryApi registry = new RegistryApi();
    private static final InitializerApi initializers = new InitializerApi();
    private static final HoneyInitializerApi honeyInitializers = new HoneyInitializerApi();
    private static final ResourcefulBeesEvents events = new ResourcefulBeesEvents();

    public static RegistryApi getRegistry() {
        return registry;
    }

    public static InitializerApi getInitializers() {
        return initializers;
    }

    public static HoneyInitializerApi getHoneyInitalizers() {
        return honeyInitializers;
    }

    public static ResourcefulBeesEvents getEvents() {
        return events;
    }

}
