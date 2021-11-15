package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.recipe.CentrifugeRecipe;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CentrifugeUtils {

    private CentrifugeUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    @NotNull
    public static Optional<CentrifugeRecipe> getRecipe(World level, ItemStack recipeStack) {
        return level != null ? level.getRecipeManager().getRecipeFor(CentrifugeRecipe.CENTRIFUGE_RECIPE_TYPE, new Inventory(recipeStack), level) : Optional.empty();
    }

    public static int getRows(CentrifugeTier tier) {
        return tier.equals(CentrifugeTier.BASIC) ? 1 : tier.getSlots() / 4;
    }

    public static int getColumns(CentrifugeTier tier) {
        return tier.equals(CentrifugeTier.BASIC) ? 1 : 4;
    }
}
