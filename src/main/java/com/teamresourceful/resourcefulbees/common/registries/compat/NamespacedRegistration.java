package com.teamresourceful.resourcefulbees.common.registries.compat;

import com.teamresourceful.resourcefullib.common.registry.HolderRegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public interface NamespacedRegistration<T> extends ResourcefulRegistry<T> {

    <I extends T> RegistryEntry<I> register(Identifier id, Supplier<I> supplier);

    HolderRegistryEntry<T> registerHolder(Identifier id, Supplier<T> supplier);
}