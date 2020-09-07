package com.resourcefulbees.resourcefulbees.utils;

import com.resourcefulbees.resourcefulbees.block.HoneycombBlock;
import com.resourcefulbees.resourcefulbees.item.BeeJar;
import com.resourcefulbees.resourcefulbees.item.HoneycombItem;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.RegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.client.event.ColorHandlerEvent;

import static com.resourcefulbees.resourcefulbees.ResourcefulBees.LOGGER;

public final class ColorHandler {
    private ColorHandler() {}

    public static void onItemColors(ColorHandlerEvent.Item event) {
        ItemColors colors = event.getItemColors();
        BeeRegistry.getBees().forEach(((s, customBeeData) -> {
            if (customBeeData.hasHoneycomb() && customBeeData.ColorData.hasHoneycombColor()) {
                registerItems(colors, HoneycombItem::getColor, customBeeData.getCombRegistryObject().get());
                registerItems(colors, HoneycombBlock::getItemColor, customBeeData.getCombBlockItemRegistryObject().get());
            }
        }));
        registerItems(colors, BeeJar::getColor, RegistryHandler.BEE_JAR.get());
    }

    public static void onBlockColors(ColorHandlerEvent.Block event){
        BlockColors colors = event.getBlockColors();
        BeeRegistry.getBees().forEach(((s, customBeeData) -> {
            if (customBeeData.hasHoneycomb() && customBeeData.ColorData.hasHoneycombColor()) {
                registerBlocks(colors, HoneycombBlock::getBlockColor, customBeeData.getCombBlockRegistryObject().get());
            }
        }));
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
