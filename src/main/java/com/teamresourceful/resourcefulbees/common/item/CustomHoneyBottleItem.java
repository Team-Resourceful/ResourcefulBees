package com.teamresourceful.resourcefulbees.common.item;

import com.teamresourceful.resourcefulbees.api.honeydata.HoneyBottleData;
import com.teamresourceful.resourcefulbees.api.honeydata.HoneyData;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import net.minecraft.item.Food;
import net.minecraft.item.HoneyBottleItem;
import net.minecraft.item.ItemStack;

public class CustomHoneyBottleItem extends HoneyBottleItem {

    public final HoneyBottleData data;
    private Food food = null;

    public CustomHoneyBottleItem(HoneyBottleData data) {
        super(data.getProperties());
        this.data = data;
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? ((CustomHoneyBottleItem) stack.getItem()).getHoneyBottleColor() : BeeConstants.DEFAULT_ITEM_COLOR;
    }

    @Override
    public Food getFoodProperties() {
        if (food == null) food = data.getFood();
        return food;
    }

    @Override
    public boolean isEdible() {
        return true;
    }

    public int getHoneyBottleColor() {
        return data.getColor().getValue();
    }

    public HoneyBottleData getHoneyData() {
        return data;
    }
}
