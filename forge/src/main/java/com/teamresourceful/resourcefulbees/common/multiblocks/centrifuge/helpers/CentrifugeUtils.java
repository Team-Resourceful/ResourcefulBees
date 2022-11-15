package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers;

import com.teamresourceful.resourcefulbees.common.inventory.slots.FilterSlot;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeTypes;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public final class CentrifugeUtils {

    private CentrifugeUtils() {
        throw new UtilityClassError();
    }

    @NotNull
    public static Optional<CentrifugeRecipe> getRecipe(Level level, ItemStack recipeStack) {
        return level != null ? level.getRecipeManager().getRecipeFor(ModRecipeTypes.CENTRIFUGE_RECIPE_TYPE.get(), new SimpleContainer(recipeStack), level) : Optional.empty();
    }

    @NotNull
    public static Optional<CentrifugeRecipe> getFilterRecipe(Level level, ItemStack recipeStack) {
        return level != null ? FilterSlot.getRecipeFor(ModRecipeTypes.CENTRIFUGE_RECIPE_TYPE.get(), new SimpleContainer(recipeStack), level) : Optional.empty();
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
