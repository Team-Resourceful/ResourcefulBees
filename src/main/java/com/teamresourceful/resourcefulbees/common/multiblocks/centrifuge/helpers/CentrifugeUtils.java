package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public final class CentrifugeUtils {

    private CentrifugeUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    @NotNull
    public static Optional<CentrifugeRecipe> getRecipe(Level level, ItemStack recipeStack) {
        return level != null ? level.getRecipeManager().getRecipeFor(ModRecipeTypes.CENTRIFUGE_RECIPE_TYPE.get(), new SimpleContainer(recipeStack), level) : Optional.empty();
    }

    public static <T> T getFromCollection(Collection<T> collection, int index) {
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
