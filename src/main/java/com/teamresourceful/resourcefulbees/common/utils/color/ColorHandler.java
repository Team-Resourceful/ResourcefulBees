package com.teamresourceful.resourcefulbees.common.utils.color;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.IBeeRegistry;
import com.teamresourceful.resourcefulbees.api.beedata.HoneycombData;
import com.teamresourceful.resourcefulbees.common.block.CustomHoneyBlock;
import com.teamresourceful.resourcefulbees.common.block.HoneycombBlock;
import com.teamresourceful.resourcefulbees.common.config.Config;
import com.teamresourceful.resourcefulbees.common.item.*;
import com.teamresourceful.resourcefulbees.common.lib.enums.HoneycombType;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.client.event.ColorHandlerEvent;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;

public final class ColorHandler {

    private static final IBeeRegistry BEE_REGISTRY = BeeRegistry.getRegistry();

    private ColorHandler() {
    }

    public static void onItemColors(ColorHandlerEvent.Item event) {
        ItemColors colors = event.getItemColors();
        BEE_REGISTRY.getBees().forEach(((s, beeData) -> {
            HoneycombData honeycombData = beeData.getHoneycombData();
            if (honeycombData.getHoneycombType().equals(HoneycombType.DEFAULT) && !honeycombData.getColor().isDefault()) {
                registerItems(colors, HoneycombItem::getColor, BeeInfoUtils.getItem(ResourcefulBees.MOD_ID + ":" + s + "_honeycomb"));
                registerItems(colors, HoneycombBlock::getItemColor, BeeInfoUtils.getItem(ResourcefulBees.MOD_ID + ":" + s + "_honeycomb_block"));
            }
            if (!beeData.getRenderData().getColorData().getSpawnEggPrimaryColor().isDefault() && !beeData.getRenderData().getColorData().getSpawnEggSecondaryColor().isDefault()) {
                registerItems(colors, BeeSpawnEggItem::getColor, BeeInfoUtils.getItem(ResourcefulBees.MOD_ID + ":" + s + "_bee_spawn_egg"));
            }
        }));
        HoneyRegistry.getRegistry().getHoneyBottles().forEach((h, honeyData) -> {
            registerItems(colors, CustomHoneyBottleItem::getColor, honeyData.getHoneyBottleRegistryObject().get());
            if (Config.HONEY_GENERATE_BLOCKS.get() && honeyData.doGenerateHoneyBlock()) {
                registerItems(colors, CustomHoneyBlock::getItemColor, honeyData.getHoneyBlockItemRegistryObject().get());
            }
            if (Config.HONEY_GENERATE_FLUIDS.get() && honeyData.doGenerateHoneyFluid()) {
                registerItems(colors, CustomHoneyBucketItem::getColor, honeyData.getHoneyBucketItemRegistryObject().get());
            }
        });
        registerItems(colors, BeeJar::getColor, ModItems.BEE_JAR.get());
    }

    public static void onBlockColors(ColorHandlerEvent.Block event) {
        BlockColors colors = event.getBlockColors();
        BEE_REGISTRY.getBees().forEach(((s, beeData) -> {
            HoneycombData honeycombData = beeData.getHoneycombData();
            if (honeycombData.getHoneycombType().equals(HoneycombType.DEFAULT) && (!honeycombData.getColor().isDefault())) {
                registerBlocks(colors, HoneycombBlock::getBlockColor, BeeInfoUtils.getBlock(ResourcefulBees.MOD_ID + ":" + s + "_honeycomb_block"));
            }
        }));
        HoneyRegistry.getRegistry().getHoneyBottles().forEach((h, honeyData) -> {
            if (Config.HONEY_GENERATE_BLOCKS.get() && honeyData.doGenerateHoneyBlock()) {
                registerBlocks(colors, CustomHoneyBlock::getBlockColor, honeyData.getHoneyBlockRegistryObject().get());
            }
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
