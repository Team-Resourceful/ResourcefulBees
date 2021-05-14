package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.utils.color.RainbowColor;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.item.ItemStack;

public class CustomHoneyBottleItem extends HoneyBottleItem {

    public final HoneyBottleData honeyBottleData;
    private FoodProperties food = null;

    public CustomHoneyBottleItem(Properties properties, HoneyBottleData honeyBottleData) {
        super(properties);
        this.honeyBottleData = honeyBottleData;
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 0) {
            CustomHoneyBottleItem honeyBottleItem = (CustomHoneyBottleItem) stack.getItem();
            return honeyBottleItem.honeyBottleData.getColorData().isRainbow() ? RainbowColor.getRGB() : honeyBottleItem.getHoneyBottleColor();
        }
        return BeeConstants.DEFAULT_ITEM_COLOR;
    }

    @Override
    public FoodProperties getFoodProperties() {
        if (food == null) food = honeyBottleData.getFood();
        return food;
    }

    @Override
    public boolean isEdible() {
        return true;
    }

    public int getHoneyBottleColor() {
        return honeyBottleData.getColorData().getColor().getC();
    }

    public HoneyBottleData getHoneyData() {
        return honeyBottleData;
    }
}
