package com.teamresourceful.resourcefulbees.common.registry.api.fabric;

import com.teamresourceful.resourcefulbees.common.registry.api.ResourcefulRegistry;
import net.minecraft.core.Registry;

public class ResourcefulRegistriesImpl {
    public static <T> ResourcefulRegistry<T> create(Registry<T> registry, String id) {
        return new FabricResourcefulRegistry<>(registry, id);
    }
}
