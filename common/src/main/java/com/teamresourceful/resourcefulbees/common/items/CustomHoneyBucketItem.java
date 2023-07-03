package com.teamresourceful.resourcefulbees.common.items;

import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyFluidData;
import com.teamresourceful.resourcefulbees.common.items.honey.ColoredObject;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import earth.terrarium.botarium.common.registry.fluid.FluidBucketItem;
import earth.terrarium.botarium.common.registry.fluid.FluidData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class CustomHoneyBucketItem extends FluidBucketItem implements Tradeable, ColoredObject {

    private final HoneyFluidData data;

    public CustomHoneyBucketItem(FluidData data, HoneyFluidData honeyData) {
        super(data, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
        this.data = honeyData;
    }

    @Override
    public boolean isTradable() {
        return this.data.tradeData().isTradable();
    }

    @Override
    public BeekeeperTradeData getTradeData() {
        return this.data.tradeData();
    }

    @Override
    public int getObjectColor(int index) {
        return index == 1 ? data.renderData().color().getValue() : BeeConstants.DEFAULT_ITEM_COLOR;
    }
}
