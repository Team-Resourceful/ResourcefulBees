
package com.teamresourceful.resourcefulbees.common.registries.compat;

import com.teamresourceful.resourcefullib.common.registry.HolderRegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntries;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.function.Supplier;

public class NamespacedRegistryChild<T> implements NamespacedRegistration<T> {

    private final NamespacedRegistration<T> parent;
    private final RegistryEntries<T> entries = new RegistryEntries<>();

    public NamespacedRegistryChild(NamespacedRegistration<T> parent) {
        this.parent = parent;
    }

    @Override
    public String namespace() {
        return parent.namespace();
    }

    @Override
    public <I extends T> RegistryEntry<I> register(String id, Supplier<I> supplier) {
        return register(Identifier.fromNamespaceAndPath(namespace(), id), supplier);
    }

    public <I extends T> RegistryEntry<I> register(Identifier id, Supplier<I> supplier) {
        return entries.add(parent.register(id, supplier));
    }

    @Override
    public HolderRegistryEntry<T> registerHolder(String id, Supplier<T> supplier) {
        return registerHolder(Identifier.fromNamespaceAndPath(namespace(), id), supplier);
    }

    public HolderRegistryEntry<T> registerHolder(Identifier id, Supplier<T> supplier) {
        return entries.add(parent.registerHolder(id, supplier));
    }

    @Override
    public Collection<RegistryEntry<T>> getEntries() {
        return entries.getEntries();
    }

    @Override
    public void init() {
        // Parent owns the underlying registries and their initialization.
    }
}
