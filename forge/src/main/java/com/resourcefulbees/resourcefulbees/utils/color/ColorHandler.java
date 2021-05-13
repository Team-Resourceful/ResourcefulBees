package com.resourcefulbees.resourcefulbees.utils.color;

import com.mojang.serialization.JsonOps;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.IBeeRegistry;
import com.resourcefulbees.resourcefulbees.block.ColoredHoneyBlock;
import com.resourcefulbees.resourcefulbees.block.HoneycombBlock;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.item.*;
import com.resourcefulbees.resourcefulbees.lib.HoneycombTypes;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.ColorHandlerEvent;

import static com.resourcefulbees.resourcefulbees.ResourcefulBees.LOGGER;

public final class ColorHandler {

    private static final IBeeRegistry BEE_REGISTRY = BeeRegistry.getRegistry();

    private ColorHandler() {
    }

    public static void onItemColors(ColorHandlerEvent.Item event) {
        ItemColors colors = event.getItemColors();
        BEE_REGISTRY.getRawBees().forEach(((s, beeData) -> {
            HoneycombTypes honeycombType = HoneycombTypes.CODEC.fieldOf("honeycombType").orElse(HoneycombTypes.DEFAULT).codec().fieldOf("HoneycombData").codec().parse(JsonOps.INSTANCE, beeData).get().orThrow();
            if (honeycombType.equals(HoneycombTypes.DEFAULT)) {
                registerItems(colors, HoneycombItem::getColor, BeeInfoUtils.getItem(ResourcefulBees.MOD_ID + ":" + s + "_honeycomb"));
                registerItems(colors, HoneycombBlock::getItemColor, BeeInfoUtils.getItem(ResourcefulBees.MOD_ID + ":" + s + "_honeycomb_block"));
            }
            registerItems(colors, BeeSpawnEggItem::getColor, BeeInfoUtils.getItem(ResourcefulBees.MOD_ID + ":" + s + "_bee_spawn_egg"));
        }));
        BEE_REGISTRY.getHoneyBottles().forEach((h, honeyData) -> {
            registerItems(colors, CustomHoneyBottleItem::getColor, honeyData.getHoneyBottleRegistryObject().get());
            if (Config.HONEY_GENERATE_BLOCKS.get() && honeyData.doGenerateHoneyBlock()) {
                registerItems(colors, ColoredHoneyBlock::getItemColor, honeyData.getHoneyBlockItemRegistryObject().get());
            }
            if (Config.HONEY_GENERATE_FLUIDS.get() && honeyData.doGenerateHoneyFluid()) {
                registerItems(colors, CustomHoneyBucketItem::getColor, honeyData.getHoneyBucketItemRegistryObject().get());
            }
        });
        registerItems(colors, BeeJar::getColor, ModItems.BEE_JAR.get());
    }

    public static void onBlockColors(ColorHandlerEvent.Block event) {
        BlockColors colors = event.getBlockColors();
        BEE_REGISTRY.getRawBees().forEach(((s, beeData) -> {
            HoneycombTypes honeycombType = HoneycombTypes.CODEC.fieldOf("honeycombType").orElse(HoneycombTypes.DEFAULT).codec().fieldOf("HoneycombData").codec().parse(JsonOps.INSTANCE, beeData).get().orThrow();
            if (honeycombType.equals(HoneycombTypes.DEFAULT)) {
                registerBlocks(colors, HoneycombBlock::getBlockColor, BeeInfoUtils.getBlock(ResourcefulBees.MOD_ID + ":" + s + "_honeycomb_block"));
            }
        }));
        BEE_REGISTRY.getHoneyBottles().forEach((h, honeyData) -> {
            if (Config.HONEY_GENERATE_BLOCKS.get() && honeyData.doGenerateHoneyBlock()) {
                registerBlocks(colors, ColoredHoneyBlock::getBlockColor, honeyData.getHoneyBlockRegistryObject().get());
            }
        });
    }

    private static void registerItems(ItemColors handler, ItemColor itemColor, ItemLike... items) {
        try {
            handler.register(itemColor, items);
        } catch (NullPointerException ex) {
            LOGGER.error("ItemColor Registration Failed", ex);
        }
    }

    private static void registerBlocks(BlockColors handler, BlockColor blockColor, Block... blocks) {
        try {
            handler.register(blockColor, blocks);
        } catch (NullPointerException ex) {
            LOGGER.error("BlockColor Registration Failed", ex);
        }
    }
}
