package com.teamresourceful.resourcefulbees.client.color;

import com.teamresourceful.resourcefulbees.common.block.CustomHoneyBlock;
import com.teamresourceful.resourcefulbees.common.block.HoneycombBlock;
import com.teamresourceful.resourcefulbees.common.item.*;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;

public final class ColorHandler {

    private ColorHandler() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void onItemColors(ColorHandlerEvent.Item event) {
        ItemColors colors = event.getItemColors();
        registerItems(colors, HoneycombItem::getColor, ModItems.HONEYCOMB_ITEMS.getEntries());
        registerItems(colors, HoneycombBlock::getItemColor, ModItems.HONEYCOMB_BLOCK_ITEMS.getEntries());
        registerItems(colors, BeeSpawnEggItem::getColor, ModItems.SPAWN_EGG_ITEMS.getEntries());
        registerItems(colors, CustomHoneyBucketItem::getColor, ModItems.HONEY_BUCKET_ITEMS.getEntries());
        registerItems(colors, CustomHoneyBottleItem::getColor, ModItems.HONEY_BOTTLE_ITEMS.getEntries());
        registerItems(colors, CustomHoneyBlock::getItemColor, ModItems.HONEY_BLOCK_ITEMS.getEntries());
        colors.register(BeeJar::getColor, ModItems.BEE_JAR.get());
    }

    public static void onBlockColors(ColorHandlerEvent.Block event) {
        BlockColors colors = event.getBlockColors();
        registerBlocks(colors, HoneycombBlock::getBlockColor, ModBlocks.HONEYCOMB_BLOCKS.getEntries());
        registerBlocks(colors, CustomHoneyBlock::getBlockColor, ModBlocks.HONEY_BLOCKS.getEntries());
    }

    private static void registerItems(ItemColors handler, ItemColor itemColor, Collection<RegistryObject<Item>> items) {
        if (items.isEmpty()) return;
        List<Item> itemList = items.stream().filter(RegistryObject::isPresent).map(RegistryObject::get).collect(Collectors.toList());
        try {
            handler.register(itemColor, itemList.toArray(new Item[]{}));
        } catch (NullPointerException ex) {
            LOGGER.error("ItemColor Registration Failed", ex);
        }
    }

    private static void registerBlocks(BlockColors handler, BlockColor blockColor, Collection<RegistryObject<Block>> blocks) {
        if (blocks.isEmpty()) return;
        List<Block> blockList = blocks.stream().filter(RegistryObject::isPresent).map(RegistryObject::get).collect(Collectors.toList());
        try {
            handler.register(blockColor, blockList.toArray(new Block[]{}));
        } catch (NullPointerException ex) {
            LOGGER.error("BlockColor Registration Failed", ex);
        }
    }
}
