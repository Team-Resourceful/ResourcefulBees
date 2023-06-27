package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipeType;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.world.item.crafting.RecipeType;

public final class ModRecipeTypes {

    private ModRecipeTypes() {
        throw new UtilityClassError();
    }
    
    public static final ResourcefulRegistry<RecipeType<?>> RECIPE_TYPES = ResourcefulRegistries.create(ModRecipes.RECIPE_TYPES);

    public static final RegistryEntry<RecipeType<CentrifugeRecipe>> CENTRIFUGE_RECIPE_TYPE = RECIPE_TYPES.register("centrifuge", () -> CodecRecipeType.of("centrifuge"));
}
