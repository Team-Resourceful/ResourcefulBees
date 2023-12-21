package com.teamresourceful.resourcefulbees.centrifuge.common.helpers;

import com.teamresourceful.resourcefulbees.centrifuge.common.containers.slots.FilterSlot;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public final class CentrifugeUtils {

    private CentrifugeUtils() throws UtilityClassException {
        throw new UtilityClassException();
    }

    @NotNull
    public static Optional<CentrifugeRecipe> getRecipe(Level level, ItemStack recipeStack) {
        return level != null ? level.getRecipeManager().getRecipeFor(ModRecipes.CENTRIFUGE_RECIPE_TYPE.get(), new SimpleContainer(recipeStack), level) : Optional.empty();
    }

    @NotNull
    public static Optional<CentrifugeRecipe> getFilterRecipe(Level level, ItemStack recipeStack) {
        return level != null ? FilterSlot.getRecipeFor(ModRecipes.CENTRIFUGE_RECIPE_TYPE.get(), new SimpleContainer(recipeStack), level) : Optional.empty();
    }

    public static <T> T getFromCollection(Collection<T> collection, int index) {
        if (collection.isEmpty()) return null;
        int i = 0;
        for (T s : collection) {
            if (i == index) return s;
            i++;
        }
        throw new IndexOutOfBoundsException();
    }

    public static String formatBlockPos(BlockPos pos) {
        return String.format("[x:%s, y:%s, z:%s]", pos.getX(), pos.getY(), pos.getZ());
    }
}
