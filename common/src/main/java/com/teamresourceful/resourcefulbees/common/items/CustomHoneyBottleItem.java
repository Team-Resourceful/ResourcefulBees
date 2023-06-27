package com.teamresourceful.resourcefulbees.common.items;

import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.honey.bottle.HoneyBottleData;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class CustomHoneyBottleItem extends HoneyBottleItem implements Tradeable {

    public final HoneyBottleData data;
    private FoodProperties food = null;

    public CustomHoneyBottleItem(HoneyBottleData data) {
        super(new Item.Properties().craftRemainder(Items.GLASS_BOTTLE).stacksTo(16).rarity(data.rarity()));
        this.data = data;
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? ((CustomHoneyBottleItem) stack.getItem()).getHoneyBottleColor() : BeeConstants.DEFAULT_ITEM_COLOR;
    }

    @Nullable
    @Override
    public FoodProperties getFoodProperties() {
        if (food == null) food = data.food().getFood();
        return food == null ? super.getFoodProperties() : food;
    }

    @Override
    public boolean isEdible() {
        return true;
    }

    @Override
    public boolean isTradable() {
        return data.tradeData().isTradable();
    }

    @Override
    public BeekeeperTradeData getTradeData() {
        return data.tradeData();
    }

    public int getHoneyBottleColor() {
        return data.color().getValue();
    }

    public HoneyBottleData getHoneyData() {
        return data;
    }
}
