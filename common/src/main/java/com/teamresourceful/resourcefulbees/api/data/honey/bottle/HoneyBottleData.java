package com.teamresourceful.resourcefulbees.api.data.honey.bottle;

import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.honey.base.HoneyData;
import com.teamresourceful.resourcefullib.common.color.Color;
import com.teamresourceful.resourcefullib.common.item.LazyHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public interface HoneyBottleData extends HoneyData<HoneyBottleData> {

    String id();

    Color color();

    HoneyFoodData food();

    Rarity rarity();

    LazyHolder<Item> bottle();

    BeekeeperTradeData tradeData();
}
