package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.FlowHiveRecipe;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.HoneyGenRecipe;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.MutationRecipe;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.SolidificationRecipe;
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
    public static final RegistryEntry<RecipeType<SolidificationRecipe>> SOLIDIFICATION_RECIPE_TYPE = RECIPE_TYPES.register("solidification", () -> CodecRecipeType.of("solidification"));
    public static final RegistryEntry<RecipeType<FlowHiveRecipe>> FLOW_HIVE_RECIPE_TYPE = RECIPE_TYPES.register("flow_hive", () -> CodecRecipeType.of("flow_hive"));
    public static final RegistryEntry<RecipeType<MutationRecipe>> MUTATION_RECIPE_TYPE = RECIPE_TYPES.register("mutation", () -> CodecRecipeType.of("mutation"));
    public static final RegistryEntry<RecipeType<HoneyGenRecipe>> HONEY_GEN_RECIPE_TYPE = RECIPE_TYPES.register("honey_gen", () -> CodecRecipeType.of("honey_gen"));

}
