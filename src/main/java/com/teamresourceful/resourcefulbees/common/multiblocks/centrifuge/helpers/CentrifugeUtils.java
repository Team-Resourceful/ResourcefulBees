package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

public final class CentrifugeUtils {

    private CentrifugeUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    @NotNull
    public static Optional<CentrifugeRecipe> getRecipe(Level level, ItemStack recipeStack) {
        return level != null ? level.getRecipeManager().getRecipeFor(ModRecipeTypes.CENTRIFUGE_RECIPE_TYPE.get(), new SimpleContainer(recipeStack), level) : Optional.empty();
    }

    public static int getRows(CentrifugeTier tier) {
        return tier.equals(CentrifugeTier.BASIC) ? 1 : tier.getSlots() / 4;
    }

    public static int getColumns(CentrifugeTier tier) {
        return tier.equals(CentrifugeTier.BASIC) ? 1 : 4;
    }

    public static <T> T getFromSet(Set<T> set, int index) {
        int i = 0;
        for (T s : set) {
            if (i == index) return s;
            i++;
        }
        throw new IndexOutOfBoundsException();
    }

    public static String formatBlockPos(BlockPos pos) {
        return String.format("[x:%s, y:%s, z:%s]", pos.getX(), pos.getY(), pos.getZ());
    }
}
