package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.api.beedata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.utils.color.RainbowColor;
import net.minecraft.item.Food;
import net.minecraft.item.HoneyBottleItem;
import net.minecraft.item.ItemStack;

import net.minecraft.item.Item.Properties;

public class CustomHoneyBottleItem extends HoneyBottleItem {

    private final HoneyBottleData honeyBottleData;
    private Food food = null;

    public CustomHoneyBottleItem(Properties properties, HoneyBottleData honeyBottleData) {
        super(properties);
        this.honeyBottleData = honeyBottleData;
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 0) {
            CustomHoneyBottleItem honeyBottleItem = (CustomHoneyBottleItem) stack.getItem();
            return honeyBottleItem.honeyBottleData.isRainbow() ? RainbowColor.getRGB() : honeyBottleItem.getHoneyBottleColor();
        }
        return BeeConstants.DEFAULT_ITEM_COLOR;
    }

    @Override
    public Food getFood() {
        if (food == null) food = honeyBottleData.getFood();
        return food;
    }

    @Override
    public boolean isFood() {
        return true;
    }

    public int getHoneyBottleColor() {
        return honeyBottleData.getHoneyColorInt();
    }

    public HoneyBottleData getHoneyData() {
        return honeyBottleData;
    }
}
