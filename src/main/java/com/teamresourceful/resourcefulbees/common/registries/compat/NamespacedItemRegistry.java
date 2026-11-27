
package com.teamresourceful.resourcefulbees.common.registries.compat;

import com.teamresourceful.resourcefullib.common.registry.HolderRegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.builtin.base.ItemLikeEntry;
import com.teamresourceful.resourcefullib.common.registry.builtin.base.ItemLikeHolderEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;
import java.util.function.Supplier;

public class NamespacedItemRegistry extends NamespacedRegistry<Item> {

    public NamespacedItemRegistry(String namespace) {
        super(ResourcefulRegistries.createForItems(namespace), ResourcefulRegistries::createForItems);
    }

    public <I extends Item> ItemLikeEntry<I> register(Identifier id, Function<Item.Properties, I> factory, Supplier<Item.Properties> properties) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return register(id, () -> factory.apply(properties.get().setId(key)));
    }

    public <I extends Item> ItemLikeEntry<I> register(String id, Function<Item.Properties, I> factory, Supplier<Item.Properties> properties) {
        return register(Identifier.fromNamespaceAndPath(namespace(), id), factory, properties);
    }

    public ItemLikeEntry<BlockItem> register(Identifier id, Supplier<? extends Block> block, Supplier<Item.Properties> properties) {
        return register(id, props -> new BlockItem(block.get(), props.useBlockDescriptionPrefix()), properties);
    }

    public ItemLikeEntry<BlockItem> register(String id, Supplier<? extends Block> block, Supplier<Item.Properties> properties) {
        return register(Identifier.fromNamespaceAndPath(namespace(), id), block, properties);
    }

    @Override
    public <I extends Item> ItemLikeEntry<I> register(Identifier id, Supplier<I> supplier) {
        RegistryEntry<I> entry = super.register(id, supplier);
        return new ItemLikeEntry<>(entry);
    }

    @Override
    public <I extends Item> ItemLikeEntry<I> register(String id, Supplier<I> supplier) {
        return register(Identifier.fromNamespaceAndPath(namespace(), id), supplier);
    }

    @Override
    public ItemLikeHolderEntry<Item> registerHolder(Identifier id, Supplier<Item> supplier) {
        HolderRegistryEntry<Item> entry = super.registerHolder(id, supplier);
        return new ItemLikeHolderEntry<>(entry);
    }

    @Override
    public ItemLikeHolderEntry<Item> registerHolder(String id, Supplier<Item> supplier) {
        return registerHolder(Identifier.fromNamespaceAndPath(namespace(), id), supplier);
    }
}
