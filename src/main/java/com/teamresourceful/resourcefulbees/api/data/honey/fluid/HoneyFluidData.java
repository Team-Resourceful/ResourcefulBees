package com.teamresourceful.resourcefulbees.api.data.honey.fluid;

import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.honey.base.HoneyData;
import com.teamresourceful.resourcefullib.common.item.LazyHolder;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.Set;

public interface HoneyFluidData extends HoneyData<HoneyFluidData> {

    String id();

    HoneyRenderData renderData();

    HoneyFluidAttributesData fluidAttributesData();

    LazyHolder<Fluid> stillFluid();

    LazyHolder<Fluid> flowingFluid();

    LazyHolder<Item> fluidBucket();

    LazyHolder<Block> fluidBlock();

    BeekeeperTradeData tradeData();

    Set<Pair<Holder<MobEffect>, Float>> beeconEffects();
}
