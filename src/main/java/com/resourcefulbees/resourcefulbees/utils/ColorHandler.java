package com.resourcefulbees.resourcefulbees.utils;

import com.resourcefulbees.resourcefulbees.api.CustomBee;
import com.resourcefulbees.resourcefulbees.block.HoneycombBlock;
import com.resourcefulbees.resourcefulbees.item.BeeJar;
import com.resourcefulbees.resourcefulbees.item.ResourcefulHoneycomb;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.RegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.client.event.ColorHandlerEvent;

import static com.resourcefulbees.resourcefulbees.ResourcefulBees.LOGGER;

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

    public static int getItemColor(ItemStack stack, int tintIndex) {
        CompoundNBT honeycombNBT = stack.getChildTag(NBTConstants.NBT_ROOT);
        if (honeycombNBT != null && honeycombNBT.contains(NBTConstants.NBT_BEE_TYPE)) {
            CustomBee customBee = BeeRegistry.getInfo(honeycombNBT.getString(NBTConstants.NBT_BEE_TYPE));
            if (customBee != null && customBee.ColorData.isRainbowBee()) {
                return RainbowColor.getRGB();
            }
        }
        if (honeycombNBT != null && honeycombNBT.contains(NBTConstants.NBT_COLOR)
                && !honeycombNBT.getString(NBTConstants.NBT_COLOR).isEmpty()
                && !honeycombNBT.getString(NBTConstants.NBT_COLOR).equals(BeeConstants.STRING_DEFAULT_ITEM_COLOR)) {
            return Color.parseInt(honeycombNBT.getString(NBTConstants.NBT_COLOR));
        }
        else {
            return BeeConstants.DEFAULT_ITEM_COLOR;
        }
    }
}
