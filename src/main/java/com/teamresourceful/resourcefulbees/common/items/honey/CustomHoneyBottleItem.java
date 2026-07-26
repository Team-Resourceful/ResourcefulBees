package com.teamresourceful.resourcefulbees.common.items.honey;

import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.honey.bottle.HoneyBottleData;
import com.teamresourceful.resourcefulbees.common.items.base.Tradeable;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

//HoneyBottleItem.class no longer exists now using Item.class
public class CustomHoneyBottleItem extends Item implements Tradeable, ColoredObject {

    public final HoneyBottleData data;

    public CustomHoneyBottleItem(Item.Properties properties, HoneyBottleData data) {
        super(properties
                .craftRemainder(Items.GLASS_BOTTLE)
                .food(data.food().getFood(), data.food().getConsumable())
                .stacksTo(16)
                .rarity(data.rarity()));
        this.data = data;
    }



    @Override
    public boolean isTradable() {
        return data.tradeData().isTradable();
    }

    @Override
    public BeekeeperTradeData getTradeData() {
        return data.tradeData();
    }

    public HoneyBottleData getHoneyData() {
        return data;
    }

    @Override
    public int color() {
        return data.color().getOpaqueValue();
    }
}
