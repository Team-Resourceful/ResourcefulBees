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
import com.teamresourceful.resourcefulbees.common.lib.tools.ValidationException;
import com.teamresourceful.resourcefullib.common.color.Color;
import com.teamresourceful.resourcefullib.common.item.LazyHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@ApiStatus.NonExtendable
@SuppressWarnings("unused")
public class HoneyInitializerApi {

    private HoneyInitializers.HoneyDataInitializer data;
    //Fluid
    private HoneyInitializers.HoneyFluidDataInitializer fluid;
    private HoneyInitializers.HoneyRenderDataInitializer fluidRender;
    private HoneyInitializers.HoneyAttributesInitializer fluidAttributes;
    //Block
    private HoneyInitializers.HoneyBlockDataInitializer block;
    //Bottle
    private HoneyInitializers.HoneyBottleDataInitializer bottle;
    private HoneyInitializers.HoneyFoodDataInitializer food;
    private HoneyInitializers.HoneyBottleEffectDataInitializer effect;

    public CustomHoneyData data(String name, Map<ResourceLocation, HoneyData<?>> data) {
        return this.data.create(name, data);
    }

    public HoneyFluidData fluid(String id, HoneyRenderData renderData, HoneyFluidAttributesData attributes, LazyHolder<Fluid> still, LazyHolder<Fluid> flowing, LazyHolder<Item> bucket, LazyHolder<Block> block, BeekeeperTradeData tradeData) {
        return this.fluid.create(id, renderData, attributes, still, flowing, bucket, block, tradeData);
    }

    public HoneyRenderData fluidRender(Color color, ResourceLocation still, ResourceLocation flowing, ResourceLocation face, ResourceLocation overlay) {
        return this.fluidRender.create(color, still, flowing, face, overlay);
    }

    public HoneyFluidAttributesData fluidAttributes(int lightLevel, int density, int temperature, int viscosity, float fallDistanceModifier, double motionScale, boolean canPushEntities, boolean canSwimIn, boolean canDrownIn, boolean canExtinguish, boolean canConvertToSource, boolean supportsBoating, boolean canHydrate, LazyHolder<SoundEvent> bucketFill, LazyHolder<SoundEvent> bucketEmpty) {
        return this.fluidAttributes.create(lightLevel, density, temperature, viscosity, fallDistanceModifier, motionScale, canPushEntities, canSwimIn, canDrownIn, canExtinguish, canConvertToSource, supportsBoating, canHydrate, bucketFill, bucketEmpty);
    }

    public HoneyBlockData block(Color color, float jumpFactor, float speedFactor, LazyHolder<Item> blockItem, LazyHolder<Block> block, BeekeeperTradeData tradeData) {
        return this.block.create(color, jumpFactor, speedFactor, blockItem, block, tradeData);
    }

    public HoneyBottleData bottle(String id, Color color, HoneyFoodData food, Rarity rarity, LazyHolder<Item> bottle, BeekeeperTradeData tradeData) {
        return this.bottle.create(id, color, food, rarity, bottle, tradeData);
    }

    public HoneyFoodData food(int hunger, float saturation, boolean canAlwaysEat, boolean fastFood, List<HoneyBottleEffectData> effects) {
        return this.food.create(hunger, saturation, canAlwaysEat, fastFood, effects);
    }

    public HoneyBottleEffectData effect(LazyHolder<MobEffect> effect, int duration, int amplifier, float chance) {
        return this.effect.create(effect, duration, amplifier, chance);
    }

    @ApiStatus.Internal
    public void setData(HoneyInitializers.HoneyDataInitializer data) {
        this.data = data;
    }

    @ApiStatus.Internal
    public void setFluid(HoneyInitializers.HoneyFluidDataInitializer fluid) {
        this.fluid = fluid;
    }

    @ApiStatus.Internal
    public void setFluidRender(HoneyInitializers.HoneyRenderDataInitializer fluidRender) {
        this.fluidRender = fluidRender;
    }

    @ApiStatus.Internal
    public void setFluidAttributes(HoneyInitializers.HoneyAttributesInitializer fluidAttributes) {
        this.fluidAttributes = fluidAttributes;
    }

    @ApiStatus.Internal
    public void setBlock(HoneyInitializers.HoneyBlockDataInitializer block) {
        this.block = block;
    }

    @ApiStatus.Internal
    public void setBottle(HoneyInitializers.HoneyBottleDataInitializer bottle) {
        this.bottle = bottle;
    }

    @ApiStatus.Internal
    public void setFood(HoneyInitializers.HoneyFoodDataInitializer food) {
        this.food = food;
    }

    @ApiStatus.Internal
    public void setEffect(HoneyInitializers.HoneyBottleEffectDataInitializer effect) {
        this.effect = effect;
    }

    @ApiStatus.Internal
    public void validate() {
        List<String> badFields = Arrays.stream(this.getClass().getDeclaredFields())
                .filter(field -> {
                    try {
                        return field.get(this) == null;
                    } catch (IllegalAccessException e) {
                        throw new ValidationException(e);
                    }
                })
                .map(Field::getName)
                .toList();
        if (!badFields.isEmpty()) {
            throw new ValidationException("HoneyInitializerApi is missing the following initializers: " + badFields);
        }
    }
}
