package com.teamresourceful.resourcefulbees.client.fluids;

import com.teamresourceful.resourcefulbees.common.registries.compat.NamespacedRegistry;
import com.teamresourceful.resourcefullib.client.fluid.data.ClientFluidProperties;
import com.teamresourceful.resourcefullib.client.fluid.registry.ResourcefulClientFluidRegistry;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public class NamespacedClientFluidRegistry extends NamespacedRegistry<ClientFluidProperties> {

    public NamespacedClientFluidRegistry(String namespace) {
        super(new ResourcefulClientFluidRegistry(namespace), ResourcefulClientFluidRegistry::new);
    }

    public RegistryEntry<ClientFluidProperties> register(Identifier id, ClientFluidProperties.Builder builder) {
        return register(id, builder::build);
    }

    public RegistryEntry<ClientFluidProperties> register(String id, ClientFluidProperties.Builder builder) {
        return register(Identifier.fromNamespaceAndPath(namespace(), id), builder);
    }

    @Override
    public <I extends ClientFluidProperties> RegistryEntry<I> register(Identifier id, Supplier<I> supplier) {
        return super.register(id, supplier);
    }

    @Override
    public <I extends ClientFluidProperties> RegistryEntry<I> register(String id, Supplier<I> supplier) {
        return super.register(id, supplier);
    }
}
