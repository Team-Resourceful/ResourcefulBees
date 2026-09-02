package com.teamresourceful.resourcefulbees.common.modcompat.jei.ingredients;

import com.teamresourceful.resourcefulbees.common.modcompat.jei.JEICompat;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class EntityIngredientHelper implements IIngredientHelper<EntityIngredient> {

    @Override
    public @NotNull IIngredientType<EntityIngredient> getIngredientType() {
        return JEICompat.ENTITY_INGREDIENT;
    }

    @Override
    public @NotNull String getDisplayName(EntityIngredient ingredient) {
        return ingredient.getDisplayName().getString();
    }

    @Override
    public @NotNull Object getUid(EntityIngredient ingredient, @NonNull UidContext context) {
        return ingredient.getEntityId();
    }

    @Override
    public @NotNull ItemStack getCheatItemStack(EntityIngredient ingredient) {
        Optional<Holder<Item>> spawnEgg = SpawnEggItem.byId(ingredient.entityType());
        return spawnEgg.map(ItemStack::new).orElse(ItemStack.EMPTY);
    }

    @Override
    public @NotNull Identifier getIdentifier(EntityIngredient ingredient) {
        return ingredient.getEntityId();
    }

    @Override
    public @NotNull EntityIngredient copyIngredient(EntityIngredient ingredient) {
        return ingredient;
    }

    @Override
    public @NotNull String getErrorInfo(@Nullable EntityIngredient ingredient) {
        return ingredient == null
                ? "null"
                : ingredient.toString();
    }
}