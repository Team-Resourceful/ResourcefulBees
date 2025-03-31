package com.teamresourceful.resourcefulbees.api.data.honey.fluid;

import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.honey.base.HoneyData;
import com.teamresourceful.resourcefullib.common.item.LazyHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public interface HoneyFluidData extends HoneyData<HoneyFluidData> {

    String id();

    HoneyRenderData renderData();

    HoneyFluidAttributesData fluidAttributesData();

    LazyHolder<Fluid> stillFluid();

    LazyHolder<Fluid> flowingFluid();

    LazyHolder<Item> fluidBucket();

    LazyHolder<Block> fluidBlock();

    BeekeeperTradeData tradeData();
}
