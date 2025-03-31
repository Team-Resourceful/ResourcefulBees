package com.teamresourceful.resourcefulbees.api.data.honey;

import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.honey.base.HoneyData;
import com.teamresourceful.resourcefullib.common.color.Color;
import com.teamresourceful.resourcefullib.common.item.LazyHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public interface HoneyBlockData extends HoneyData<HoneyBlockData> {

    Color color();

    float jumpFactor();

    float speedFactor();

    LazyHolder<Item> blockItem();

    LazyHolder<Block> block();

    BeekeeperTradeData tradeData();
}
