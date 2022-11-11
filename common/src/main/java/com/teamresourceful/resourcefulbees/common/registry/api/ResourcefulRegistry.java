package com.teamresourceful.resourcefulbees.common.registry.api;

import java.util.Collection;
import java.util.function.Supplier;

public interface ResourcefulRegistry<T> {

    <I extends T> RegistryEntry<I> register(String id, Supplier<I> supplier);

    Collection<RegistryEntry<T>> getEntries();

    void init();
}
