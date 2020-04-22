package com.dungeonderps.resourcefulbees.utils;

import com.dungeonderps.resourcefulbees.item.ResourcefulHoneycomb;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.client.event.ColorHandlerEvent;
import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.RegistryHandler;

import static com.dungeonderps.resourcefulbees.ResourcefulBees.LOGGER;

public final class ColorHandler {
    private ColorHandler() {}

    public static void onItemColors(ColorHandlerEvent.Item event) {
        ItemColors colors = event.getItemColors();
        LOGGER.info("on Item Colors");
        registerItems(colors, ResourcefulHoneycomb::getColor, RegistryHandler.RESOURCEFUL_HONEYCOMB.get());
    }

    private static void registerItems(ItemColors handler, IItemColor itemColor, IItemProvider... items) {
        try {
            LOGGER.info("item/comb color registration");
            handler.register(itemColor, items);
        } catch (NullPointerException ex) {
            ResourcefulBees.LOGGER.error("ItemColor Registration Failed", ex);
        }
    }
}
