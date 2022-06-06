package com.teamresourceful.resourcefulbees.common.item;

import com.teamresourceful.resourcefulbees.api.honeydata.HoneyBottleData;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CustomHoneyBottleItem extends HoneyBottleItem {

    public final HoneyBottleData data;
    private FoodProperties food = null;

    public CustomHoneyBottleItem(HoneyBottleData data) {
        super(data.getProperties());
        this.data = data;
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? ((CustomHoneyBottleItem) stack.getItem()).getHoneyBottleColor() : BeeConstants.DEFAULT_ITEM_COLOR;
    }

    @Nullable
    @Override
    public FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
        if (food == null) food = data.getFood();
        return food;
    }

    @Override
    public boolean isEdible() {
        return true;
    }

    public int getHoneyBottleColor() {
        return data.color().getValue();
    }

    public HoneyBottleData getHoneyData() {
        return data;
    }
}
