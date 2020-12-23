package com.resourcefulbees.resourcefulbees.utils.color;

import com.resourcefulbees.resourcefulbees.api.IBeeRegistry;
import com.resourcefulbees.resourcefulbees.block.CustomHoneyBlock;
import com.resourcefulbees.resourcefulbees.block.CustomHoneyFluidBlock;
import com.resourcefulbees.resourcefulbees.block.HoneycombBlock;
import com.resourcefulbees.resourcefulbees.item.*;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidAttributes;

import static com.resourcefulbees.resourcefulbees.ResourcefulBees.LOGGER;

public final class ColorHandler {

    private static final IBeeRegistry BEE_REGISTRY = BeeRegistry.getRegistry();

    private ColorHandler() {
    }

    public static void onItemColors(ColorHandlerEvent.Item event) {
        ItemColors colors = event.getItemColors();
        BEE_REGISTRY.getBees().forEach(((s, customBeeData) -> {
            if (customBeeData.hasHoneycomb() && customBeeData.getColorData().hasHoneycombColor() && !customBeeData.hasCustomDrop()) {
                registerItems(colors, HoneycombItem::getColor, customBeeData.getCombRegistryObject().get());
                registerItems(colors, HoneycombBlock::getItemColor, customBeeData.getCombBlockItemRegistryObject().get());
            }
            registerItems(colors, BeeSpawnEggItem::getColor, customBeeData.getSpawnEggItemRegistryObject().get());
        }));
        BEE_REGISTRY.getHoneyBottles().forEach((h, honeyData) -> {
            registerItems(colors, CustomHoneyBottleItem::getColor, honeyData.getHoneyBottleRegistryObject().get());
            registerItems(colors, CustomHoneyBlock::getItemColor, honeyData.getHoneyBlockItemRegistryObject().get());
            registerItems(colors, CustomHoneyBucketItem::getColor, honeyData.getHoneyBucketItemRegistryObject().get());
        });
        registerItems(colors, BeeJar::getColor, ModItems.BEE_JAR.get());
    }

    public static void onBlockColors(ColorHandlerEvent.Block event) {
        BlockColors colors = event.getBlockColors();
        BEE_REGISTRY.getBees().forEach(((s, customBeeData) -> {
            if (customBeeData.hasHoneycomb() && customBeeData.getColorData().hasHoneycombColor() && !customBeeData.hasCustomDrop()) {
                registerBlocks(colors, HoneycombBlock::getBlockColor, customBeeData.getCombBlockRegistryObject().get());
            }
        }));
        BEE_REGISTRY.getHoneyBottles().forEach((h, honeyData) -> {
            registerBlocks(colors, CustomHoneyBlock::getBlockColor, honeyData.getHoneyBlockRegistryObject().get());
//            registerBlocks(colors, CustomHoneyFluidBlock::getBlockColor, honeyData.getHoneyFluidBlockRegistryObject().get());
        });
    }

    private static void registerItems(ItemColors handler, IItemColor itemColor, IItemProvider... items) {
        try {
            handler.register(itemColor, items);
        } catch (NullPointerException ex) {
            LOGGER.error("ItemColor Registration Failed", ex);
        }
    }

    private static void registerBlocks(BlockColors handler, IBlockColor blockColor, Block... blocks) {
        try {
            handler.register(blockColor, blocks);
        } catch (NullPointerException ex) {
            LOGGER.error("BlockColor Registration Failed", ex);
        }
    }
}
