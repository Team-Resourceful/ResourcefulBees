package com.teamresourceful.resourcefulbees.common.util;

import com.teamresourceful.resourcefulbees.api.data.shared.RegistryPredicate;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.item.LazyHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import java.util.Set;

public final class IngredientUtils {

    private IngredientUtils() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static Ingredient of(HolderSet<? extends ItemLike> holders) {
        return Ingredient.of(holders.stream()
                .filter(Holder::isBound)
                .map(Holder::value)
                .map(ItemLike::asItem)
        );
    }

    public static Ingredient of(LazyHolder<? extends ItemLike> holder) {
        return Ingredient.of(holder.get().asItem());
    }

    public static Ingredient of(Set<ItemStackTemplate> templates) {
        return CompoundIngredient.of(
                templates.stream()
                        .map(template -> DataComponentIngredient.of(false, template))
                        .toArray(Ingredient[]::new)
        );
    }

    public static Ingredient of(RegistryPredicate<Item> predicate, Registry<Item> registry) {
        return predicate.unwrap().map(
                tag -> Ingredient.of(registry.getOrThrow(tag)),
                holders -> Ingredient.of(
                        holders.stream()
                                .filter(Holder::isBound)
                                .map(Holder::value)
                )
        );
    }

    public static Ingredient of(RegistryPredicate<Item> predicate) {
        return of(predicate, BuiltInRegistries.ITEM);
    }
}
