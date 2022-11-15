package com.teamresourceful.resourcefulbees.client.color;

import com.teamresourceful.resourcefulbees.common.block.CustomHoneyBlock;
import com.teamresourceful.resourcefulbees.common.block.HoneycombBlock;
import com.teamresourceful.resourcefulbees.common.item.BeeJar;
import com.teamresourceful.resourcefulbees.common.item.CustomHoneyBottleItem;
import com.teamresourceful.resourcefulbees.common.item.CustomHoneyBucketItem;
import com.teamresourceful.resourcefulbees.common.item.HoneycombItem;
import com.teamresourceful.resourcefulbees.common.registry.api.RegistryEntry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class ColorHandler {

    private ColorHandler() {
        throw new UtilityClassError();
    }

    public static void onItemColors(RegisterColorHandlersEvent.Item event) {
        registerItems(event::register, HoneycombItem::getColor, ModItems.HONEYCOMB_ITEMS.getEntries());
        registerItems(event::register, HoneycombBlock::getItemColor, ModItems.HONEYCOMB_BLOCK_ITEMS.getEntries());
        registerItems(event::register, CustomHoneyBucketItem::getColor, ModItems.HONEY_BUCKET_ITEMS.getEntries());
        registerItems(event::register, CustomHoneyBottleItem::getColor, ModItems.HONEY_BOTTLE_ITEMS.getEntries());
        registerItems(event::register, CustomHoneyBlock::getItemColor, ModItems.HONEY_BLOCK_ITEMS.getEntries());
        event.register(BeeJar::getColor, ModItems.BEE_JAR.get());
    }

    public static void onBlockColors(RegisterColorHandlersEvent.Block event) {
        registerBlocks(event::register, HoneycombBlock::getBlockColor, ModBlocks.HONEYCOMB_BLOCKS.getEntries());
        registerBlocks(event::register, CustomHoneyBlock::getBlockColor, ModBlocks.HONEY_BLOCKS.getEntries());
    }

    private static void registerItems(BiConsumer<ItemColor, Item> handler, ItemColor itemColor, Collection<RegistryEntry<Item>> items) {
        if (items.isEmpty()) return;
        items.stream()
            .map(RegistryEntry::get)
            .forEach(item -> handler.accept(itemColor, item));
    }

    private static void registerBlocks(BiConsumer<BlockColor, Block> handler, Function<BlockState, Integer> blockColor, Collection<RegistryEntry<Block>> blocks) {
        if (blocks.isEmpty()) return;
        blocks.stream()
            .map(RegistryEntry::get)
            .forEach(block -> handler.accept((state, a, b, c) -> blockColor.apply(state), block));
    }
}
