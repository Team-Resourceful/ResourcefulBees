package com.teamresourceful.resourcefulbees.common.item;

import com.teamresourceful.resourcefulbees.api.honeydata.HoneyBottleData;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import net.minecraft.item.Food;
import net.minecraft.item.HoneyBottleItem;
import net.minecraft.item.ItemStack;

public class CustomHoneyBottleItem extends HoneyBottleItem {

    public final HoneyBottleData honeyBottleData;
    private Food food = null;

    public CustomHoneyBottleItem(Properties properties, HoneyBottleData honeyBottleData) {
        super(properties);
        this.honeyBottleData = honeyBottleData;
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 0) {
            CustomHoneyBottleItem honeyBottleItem = (CustomHoneyBottleItem) stack.getItem();
            return honeyBottleItem.getHoneyBottleColor();
        }
        return BeeConstants.DEFAULT_ITEM_COLOR;
    }

    @Override
    public Food getFoodProperties() {
        if (food == null) food = honeyBottleData.getFood();
        return food;
    }

    @Override
    public boolean isEdible() {
        return true;
    }

    public int getHoneyBottleColor() {
        return honeyBottleData.getColor().getValue();
    }

    public HoneyBottleData getHoneyData() {
        return honeyBottleData;
    }
}