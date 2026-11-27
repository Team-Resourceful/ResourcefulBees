
package com.teamresourceful.resourcefulbees.common.registries.compat;

import com.teamresourceful.resourcefullib.common.registry.builtin.base.ItemLikeEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;
import java.util.function.Supplier;

public class NamespacedItemRegistryChild extends NamespacedRegistryChild<Item> {

    public NamespacedItemRegistryChild(NamespacedItemRegistry parent) {
        super(parent);
    }

    public NamespacedItemRegistryChild(NamespacedItemRegistryChild parent) {
        super(parent);
    }

    public <I extends Item> ItemLikeEntry<I> register(Identifier id, Function<Item.Properties, I> factory, Supplier<Item.Properties> properties) {
        var key = ResourceKey.create(Registries.ITEM, id);
        return new ItemLikeEntry<>(
                super.register(id, () -> factory.apply(properties.get().setId(key)))
        );
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
}
