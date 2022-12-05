package com.teamresourceful.resourcefulbees.api.data.honey.bottle;

import net.minecraft.world.food.FoodProperties;

import java.util.List;

public interface HoneyFoodData {

    int hunger();

    float saturation();

    boolean canAlwaysEat();

    boolean fastFood();

    List<HoneyBottleEffectData> effects();

    default FoodProperties getFood() {
        var builder = new FoodProperties.Builder();
        builder.nutrition(hunger());
        builder.saturationMod(saturation());
        if (canAlwaysEat()) builder.alwaysEat();
        if (fastFood()) builder.fast();
        effects().forEach(effect -> builder.effect(effect.getInstance(), effect.chance()));
        return builder.build();
    }
}
