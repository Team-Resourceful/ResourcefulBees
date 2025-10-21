package com.teamresourceful.resourcefulbees.common.items;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class SpecialFoodItem extends Item {

    private final Supplier<FoodProperties> food;

    public SpecialFoodItem(Properties properties, UnaryOperator<FoodProperties.Builder> food) {
        super(properties);
        this.food = () -> food.apply(new FoodProperties.Builder()).build();
    }

    @Nullable
    @Override
    public FoodProperties getFoodProperties() {
        return food != null ? food.get() : super.getFoodProperties();
    }
}
