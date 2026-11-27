
package com.teamresourceful.resourcefulbees.common.registries.compat;

import com.teamresourceful.resourcefullib.common.registry.HolderRegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.builtin.base.ItemLikeEntry;
import com.teamresourceful.resourcefullib.common.registry.builtin.base.ItemLikeHolderEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;
import java.util.function.Supplier;

public class NamespacedBlockRegistry extends NamespacedRegistry<Block> {

    public NamespacedBlockRegistry(String namespace) {
        super(ResourcefulRegistries.createForBlocks(namespace), ResourcefulRegistries::createForBlocks);
    }

    public <I extends Block> ItemLikeEntry<I> register(Identifier id, Function<BlockBehaviour.Properties, I> factory, Supplier<BlockBehaviour.Properties> properties) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
        return register(id, () -> factory.apply(properties.get().setId(key)));
    }

    public <I extends Block> ItemLikeEntry<I> register(String id, Function<BlockBehaviour.Properties, I> factory, Supplier<BlockBehaviour.Properties> properties) {
        return register(Identifier.fromNamespaceAndPath(namespace(), id), factory, properties);
    }

    @Override
    public <I extends Block> ItemLikeEntry<I> register(Identifier id, Supplier<I> supplier) {
        RegistryEntry<I> entry = super.register(id, supplier);
        return new ItemLikeEntry<>(entry);
    }

    @Override
    public <I extends Block> ItemLikeEntry<I> register(String id, Supplier<I> supplier) {
        return register(Identifier.fromNamespaceAndPath(namespace(), id), supplier);
    }

    @Override
    public ItemLikeHolderEntry<Block> registerHolder(Identifier id, Supplier<Block> supplier) {
        HolderRegistryEntry<Block> entry = super.registerHolder(id, supplier);
        return new ItemLikeHolderEntry<>(entry);
    }

    @Override
    public ItemLikeHolderEntry<Block> registerHolder(String id, Supplier<Block> supplier) {
        return registerHolder(Identifier.fromNamespaceAndPath(namespace(), id), supplier);
    }
}
