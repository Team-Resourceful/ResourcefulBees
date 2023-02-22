package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.recipes.BreederRecipe;
import com.teamresourceful.resourcefulbees.common.recipes.HiveRecipe;
import com.teamresourceful.resourcefulbees.common.recipes.MutationRecipe;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipeType;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.RecipeType;

public final class ModRecipes {

    private ModRecipes() {
        throw new UtilityClassError();
    }

    public static final ResourcefulRegistry<RecipeType<?>> RECIPE_TYPES = ResourcefulRegistries.create(Registry.RECIPE_TYPE, ModConstants.MOD_ID);

    public static final RegistryEntry<RecipeType<HiveRecipe>> HIVE_RECIPE_TYPE = RECIPE_TYPES.register("hive", () -> CodecRecipeType.of("hive"));
    public static final RegistryEntry<RecipeType<BreederRecipe>> BREEDER_RECIPE_TYPE = RECIPE_TYPES.register("breeder", () -> CodecRecipeType.of("breeder"));
    public static final RegistryEntry<RecipeType<MutationRecipe>> MUTATION_RECIPE_TYPE = RECIPE_TYPES.register("mutation", () -> CodecRecipeType.of("mutation"));


}
