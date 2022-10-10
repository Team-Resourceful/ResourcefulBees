package com.teamresourceful.resourcefulbees.client.color;

import com.teamresourceful.resourcefulbees.common.block.CustomHoneyBlock;
import com.teamresourceful.resourcefulbees.common.block.HoneycombBlock;
import com.teamresourceful.resourcefulbees.common.item.BeeJar;
import com.teamresourceful.resourcefulbees.common.item.CustomHoneyBottleItem;
import com.teamresourceful.resourcefulbees.common.item.CustomHoneyBucketItem;
import com.teamresourceful.resourcefulbees.common.item.HoneycombItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;
import java.util.function.Function;

public final class ColorHandler {

    private ColorHandler() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void onItemColors(RegisterColorHandlersEvent.Item event) {
        registerItems(event, HoneycombItem::getColor, ModItems.HONEYCOMB_ITEMS.getEntries());
        registerItems(event, HoneycombBlock::getItemColor, ModItems.HONEYCOMB_BLOCK_ITEMS.getEntries());
        registerItems(event, CustomHoneyBucketItem::getColor, ModItems.HONEY_BUCKET_ITEMS.getEntries());
        registerItems(event, CustomHoneyBottleItem::getColor, ModItems.HONEY_BOTTLE_ITEMS.getEntries());
        registerItems(event, CustomHoneyBlock::getItemColor, ModItems.HONEY_BLOCK_ITEMS.getEntries());
        event.register(BeeJar::getColor, ModItems.BEE_JAR.get());
    }

    public static void onBlockColors(RegisterColorHandlersEvent.Block event) {
        registerBlocks(event, HoneycombBlock::getBlockColor, ModBlocks.HONEYCOMB_BLOCKS.getEntries());
        registerBlocks(event, CustomHoneyBlock::getBlockColor, ModBlocks.HONEY_BLOCKS.getEntries());
    }

    private static void registerItems(RegisterColorHandlersEvent.Item handler, ItemColor itemColor, Collection<RegistryObject<Item>> items) {
        if (items.isEmpty()) return;
        items.stream()
            .filter(RegistryObject::isPresent)
            .map(RegistryObject::get)
            .forEach(item -> handler.register(itemColor, item));
    }

    private static void registerBlocks(RegisterColorHandlersEvent.Block handler, Function<BlockState, Integer> blockColor, Collection<RegistryObject<Block>> blocks) {
        if (blocks.isEmpty()) return;
        blocks.stream()
            .filter(RegistryObject::isPresent)
            .map(RegistryObject::get)
            .forEach(block -> handler.register((state, a, b, c) -> blockColor.apply(state), block));
    }
}
