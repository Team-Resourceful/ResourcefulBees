package com.teamresourceful.resourcefulbees.common.loot.function;

import com.mojang.serialization.MapCodec;
import com.teamresourceful.resourcefulbees.common.components.PollenRecipe;
import com.teamresourceful.resourcefulbees.common.loot.condition.BeeHasNectarCondition;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class RandomPollenRecipeFunction extends LootItemConditionalFunction {

    public static final MapCodec<RandomPollenRecipeFunction> CODEC = MapCodec.unit(new RandomPollenRecipeFunction());

    public RandomPollenRecipeFunction() {
        super(Optional.of(Holder.direct(new BeeHasNectarCondition())));
    }

    @Override
    protected @NonNull ItemStack run(@NonNull ItemStack stack, LootContext context) {
        var level = context.getLevel();

        var recipes = level.recipeAccess().recipeMap().byType(ModRecipes.MUTATION_RECIPE_TYPE.get());

        if (recipes.isEmpty()) {
            return stack;
        }

        int index = context.getRandom().nextInt(recipes.size());

        var selected = recipes.stream()
                .skip(index)
                .findFirst()
                .orElseThrow();

        stack.set(ModDataComponents.POLLEN_RECIPE.get(), PollenRecipe.of(selected));

        return stack;
    }

    @Override
    public @NonNull MapCodec<? extends LootItemConditionalFunction> codec() {
        return CODEC;
    }
}