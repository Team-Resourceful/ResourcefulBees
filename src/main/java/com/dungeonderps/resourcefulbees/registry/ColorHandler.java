package com.dungeonderps.resourcefulbees.registry;

import com.dungeonderps.resourcefulbees.block.HoneycombBlock;
import com.dungeonderps.resourcefulbees.item.BeeJar;
import com.dungeonderps.resourcefulbees.item.ResourcefulHoneycomb;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.client.event.ColorHandlerEvent;

import static com.dungeonderps.resourcefulbees.ResourcefulBees.LOGGER;

public final class ColorHandler {
    private ColorHandler() {}

    public static void onItemColors(ColorHandlerEvent.Item event) {
        ItemColors colors = event.getItemColors();
        registerItems(colors, ResourcefulHoneycomb::getColor, RegistryHandler.RESOURCEFUL_HONEYCOMB.get());
        registerItems(colors, HoneycombBlock::getItemColor, RegistryHandler.HONEYCOMB_BLOCK_ITEM.get());
        registerItems(colors, BeeJar::getColor, RegistryHandler.BEE_JAR.get());
    }

    public static void onBlockColors(ColorHandlerEvent.Block event){
        BlockColors colors = event.getBlockColors();
        registerBlocks(colors, HoneycombBlock::getBlockColor, RegistryHandler.HONEYCOMB_BLOCK.get());
    }

    private static void registerItems(ItemColors handler, IItemColor itemColor, IItemProvider... items) {
        try {
            handler.register(itemColor, items);
        } catch (NullPointerException ex) {
            LOGGER.error("ItemColor Registration Failed", ex);
        }
    }

    private static void registerBlocks(BlockColors handler, IBlockColor blockColor, Block... blocks) {
        try{
            handler.register(blockColor, blocks);
        } catch (NullPointerException ex) {
            LOGGER.error("BlockColor Registration Failed", ex);
        }
    }
}
