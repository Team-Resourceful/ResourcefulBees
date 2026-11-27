
package com.teamresourceful.resourcefulbees.common.registries.compat;

import com.teamresourceful.resourcefullib.common.registry.builtin.base.ItemLikeEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;
import java.util.function.Supplier;

public class NamespacedBlockRegistryChild extends NamespacedRegistryChild<Block> {

    public NamespacedBlockRegistryChild(NamespacedBlockRegistry parent) {
        super(parent);
    }

    public NamespacedBlockRegistryChild(NamespacedBlockRegistryChild parent) {
        super(parent);
    }

    public <I extends Block> ItemLikeEntry<I> register(Identifier id, Function<BlockBehaviour.Properties, I> factory, Supplier<BlockBehaviour.Properties> properties) {
        var key = ResourceKey.create(Registries.BLOCK, id);
        return new ItemLikeEntry<>(
                super.register(id, () -> factory.apply(properties.get().setId(key)))
        );
    }

    public <I extends Block> ItemLikeEntry<I> register(String id, Function<BlockBehaviour.Properties, I> factory, Supplier<BlockBehaviour.Properties> properties) {
        return register(Identifier.fromNamespaceAndPath(namespace(), id), factory, properties);
    }
}
