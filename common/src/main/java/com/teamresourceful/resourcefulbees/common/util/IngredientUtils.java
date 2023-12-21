package com.teamresourceful.resourcefulbees.common.util;

import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.item.LazyHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public final class IngredientUtils {

    private IngredientUtils() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static Ingredient of(HolderSet<? extends ItemLike> holders) {
        return Ingredient.of(holders.stream()
                .filter(Holder::isBound)
                .map(Holder::value)
                .map(ItemLike::asItem)
                .map(Item::getDefaultInstance)
        );
    }

    public static Ingredient of(LazyHolder<? extends ItemLike> holder) {
        return Ingredient.of(holder.get().asItem().getDefaultInstance());
    }
}
