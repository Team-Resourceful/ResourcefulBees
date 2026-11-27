
package com.teamresourceful.resourcefulbees.common.registries.compat;

import com.teamresourceful.resourcefullib.common.registry.HolderRegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntries;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class NamespacedRegistry<T> implements NamespacedRegistration<T> {

    private final String namespace;
    private final Function<String, ResourcefulRegistry<T>> factory;
    private final Map<String, ResourcefulRegistry<T>> registries = new LinkedHashMap<>();
    private final RegistryEntries<T> entries = new RegistryEntries<>();

    private boolean initialized;

    public NamespacedRegistry(ResourcefulRegistry<T> defaultRegistry, Function<String, ResourcefulRegistry<T>> factory) {
        this.namespace = Objects.requireNonNull(defaultRegistry.namespace());
        this.factory = Objects.requireNonNull(factory);
        this.registries.put(this.namespace, defaultRegistry);
    }

    @Override
    public String namespace() {
        return this.namespace;
    }

    @Override
    public <I extends T> RegistryEntry<I> register(String id, Supplier<I> supplier) {
        return register(Identifier.fromNamespaceAndPath(this.namespace, id), supplier);
    }

    public <I extends T> RegistryEntry<I> register(Identifier id, Supplier<I> supplier) {
        return entries.add(registry(id.getNamespace()).register(id.getPath(), supplier));
    }

    @Override
    public HolderRegistryEntry<T> registerHolder(String id, Supplier<T> supplier) {
        return registerHolder(Identifier.fromNamespaceAndPath(this.namespace, id), supplier);
    }

    public HolderRegistryEntry<T> registerHolder(Identifier id, Supplier<T> supplier) {
        return entries.add(registry(id.getNamespace()).registerHolder(id.getPath(), supplier));
    }

    @Override
    public Collection<RegistryEntry<T>> getEntries() {
        return entries.getEntries();
    }

    @Override
    public void init() {
        if (initialized) return;

        initialized = true;
        registries.values().forEach(ResourcefulRegistry::init);
    }

    private ResourcefulRegistry<T> registry(String namespace) {
        return registries.computeIfAbsent(namespace, name -> {
            ResourcefulRegistry<T> registry = factory.apply(name);

            if (initialized) {
                registry.init();
            }

            return registry;
        });
    }
}
