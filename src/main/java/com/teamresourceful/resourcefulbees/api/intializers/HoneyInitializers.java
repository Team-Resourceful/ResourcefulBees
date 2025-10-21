package com.teamresourceful.resourcefulbees.api.intializers;

import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.honey.CustomHoneyData;
import com.teamresourceful.resourcefulbees.api.data.honey.HoneyBlockData;
import com.teamresourceful.resourcefulbees.api.data.honey.base.HoneyData;
import com.teamresourceful.resourcefulbees.api.data.honey.bottle.HoneyBottleData;
import com.teamresourceful.resourcefulbees.api.data.honey.bottle.HoneyBottleEffectData;
import com.teamresourceful.resourcefulbees.api.data.honey.bottle.HoneyFoodData;
import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyFluidAttributesData;
import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyFluidData;
import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyRenderData;
import com.teamresourceful.resourcefullib.common.color.Color;
import com.teamresourceful.resourcefullib.common.item.LazyHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.List;
import java.util.Map;

public final class HoneyInitializers {

    @FunctionalInterface
    public interface HoneyDataInitializer {
        CustomHoneyData create(String name, Map<ResourceLocation, HoneyData<?>> data);
    }

    @FunctionalInterface
    public interface HoneyFluidDataInitializer {
        HoneyFluidData create(String id, HoneyRenderData renderData, HoneyFluidAttributesData attributes, LazyHolder<Fluid> still, LazyHolder<Fluid> flowing, LazyHolder<Item> bucket, LazyHolder<Block> block, BeekeeperTradeData tradeData);
    }

    @FunctionalInterface
    public interface HoneyRenderDataInitializer {
        HoneyRenderData create(Color color, ResourceLocation still, ResourceLocation flowing, ResourceLocation face, ResourceLocation overlay);
    }

    @FunctionalInterface
    public interface HoneyAttributesInitializer {
        HoneyFluidAttributesData create(int lightLevel, int density, int temperature, int viscosity, float fallDistanceModifier, double motionScale, boolean canPushEntities, boolean canSwimIn, boolean canDrownIn, boolean canExtinguish, boolean canConvertToSource, boolean supportsBoating, boolean canHydrate, LazyHolder<SoundEvent> bucketFill, LazyHolder<SoundEvent> bucketEmpty);
    }

    @FunctionalInterface
    public interface HoneyBlockDataInitializer {
        HoneyBlockData create(Color color, float jumpFactor, float speedFactor, LazyHolder<Item> blockItem, LazyHolder<Block> block, BeekeeperTradeData tradeData);
    }

    @FunctionalInterface
    public interface HoneyBottleDataInitializer {
        HoneyBottleData create(String id, Color color, HoneyFoodData food, Rarity rarity, LazyHolder<Item> bottle, BeekeeperTradeData tradeData);
    }

    @FunctionalInterface
    public interface HoneyFoodDataInitializer {
        HoneyFoodData create(int hunger, float saturation, boolean canAlwaysEat, boolean fastFood, List<HoneyBottleEffectData> effects);
    }

    @FunctionalInterface
    public interface HoneyBottleEffectDataInitializer {
        HoneyBottleEffectData create(LazyHolder<MobEffect> effect, int duration, int strength, float chance);
    }
}
