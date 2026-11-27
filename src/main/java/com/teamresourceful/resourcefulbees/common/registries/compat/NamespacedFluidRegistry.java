
package com.teamresourceful.resourcefulbees.common.registries.compat;

import com.teamresourceful.resourcefullib.common.fluid.data.FluidData;
import com.teamresourceful.resourcefullib.common.fluid.data.FluidProperties;
import com.teamresourceful.resourcefullib.common.fluid.registry.ResourcefulFluidRegistry;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntries;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistryType;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class NamespacedFluidRegistry implements ResourcefulFluidRegistry {

    private final String namespace;
    private final Map<String, ResourcefulFluidRegistry> registries = new LinkedHashMap<>();
    private final RegistryEntries<FluidData> entries = new RegistryEntries<>();

    private boolean initialized;

    public NamespacedFluidRegistry(String namespace) {
        this.namespace = Objects.requireNonNull(namespace);
        registries.put(namespace, createRegistry(namespace));
    }

    private static ResourcefulFluidRegistry createRegistry(String namespace) {
        return ResourcefulRegistries.create(ResourcefulRegistryType.FLUID, namespace);
    }

    @Override
    public String namespace() {
        return namespace;
    }

    @Override
    public RegistryEntry<FluidData> register(String id, FluidProperties properties) {
        return register(Identifier.fromNamespaceAndPath(namespace, id), properties);
    }

    public RegistryEntry<FluidData> register(Identifier id, FluidProperties properties) {
        return entries.add(registry(id.getNamespace()).register(id.getPath(), properties));
    }

    public RegistryEntry<FluidData> register(Identifier id, FluidProperties.Builder builder) {
        return register(id, builder.build());
    }

    @Override
    public Collection<RegistryEntry<FluidData>> getEntries() {
        return entries.getEntries();
    }

    @Override
    public void init() {
        if (initialized) {
            return;
        }

        initialized = true;
        registries.values().forEach(ResourcefulFluidRegistry::init);
    }

    private ResourcefulFluidRegistry registry(String namespace) {
        return registries.computeIfAbsent(namespace, name -> {
            ResourcefulFluidRegistry registry = createRegistry(name);

            if (initialized) {
                registry.init();
            }

            return registry;
        });
    }
}
