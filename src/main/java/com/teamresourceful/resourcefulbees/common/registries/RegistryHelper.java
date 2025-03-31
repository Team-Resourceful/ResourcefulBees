package com.teamresourceful.resourcefulbees.common.registries;

import com.google.common.base.Suppliers;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import dev.architectury.injectables.targets.ArchitecturyTarget;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public final class RegistryHelper {

    public static <T> ResourcefulRegistry<T> create(Registry<T> registry, String id) {
        if ("forge".equals(ArchitecturyTarget.getCurrentTarget())) {
            return ResourcefulRegistries.create(registry, id);
        }
        return new DeferredRegistry<>(ResourcefulRegistries.create(registry, id));
    }

    private static class DeferredRegistry<T> implements ResourcefulRegistry<T> {

        private boolean initalized = false;
        private final List<DeferredRegistry.Entry<?>> entries = new ArrayList<>();
        private final ResourcefulRegistry<T> registry;

        public DeferredRegistry(ResourcefulRegistry<T> registry) {
            this.registry = registry;
        }

        @Override
        public <I extends T> RegistryEntry<I> register(String id, Supplier<I> supplier) {
            if (initalized) {
                return registry.register(id, supplier);
            }
            var entry = new DeferredRegistry.Entry<>(new AtomicBoolean(false), Suppliers.memoize(() -> registry.register(id, supplier)));
            entries.add(entry);
            return entry;
        }

        @Override
        public Collection<RegistryEntry<T>> getEntries() {
            return registry.getEntries();
        }

        @Override
        public void init() {
            initalized = true;
            entries.forEach(Entry::init);
        }

        private record Entry<T>(AtomicBoolean inited, Supplier<RegistryEntry<T>> entrySupplier) implements RegistryEntry<T> {

            public void init() {
                entrySupplier.get();
                inited.set(true);
            }

            @Override
            public T get() {
                if (!inited.get()) {
                    throw new IllegalStateException("Entry not initialized");
                }
                return entrySupplier.get().get();
            }

            @Override
            public ResourceLocation getId() {
                if (!inited.get()) {
                    throw new IllegalStateException("Entry not initialized");
                }
                return entrySupplier.get().getId();
            }
        }
    }
}
