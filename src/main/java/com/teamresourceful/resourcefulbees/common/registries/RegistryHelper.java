package com.teamresourceful.resourcefulbees.common.registries;

import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.Registry;

public final class RegistryHelper {

    public static <T> ResourcefulRegistry<T> create(Registry<T> registry, String id) {
        return ResourcefulRegistries.create(registry, id);
    }
}
