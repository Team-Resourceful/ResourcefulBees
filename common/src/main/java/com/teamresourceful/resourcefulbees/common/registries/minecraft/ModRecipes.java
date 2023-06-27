package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.recipes.*;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipeType;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeType;

public final class ModRecipes {

    private ModRecipes() {
        throw new UtilityClassError();
    }

    public static final ResourcefulRegistry<RecipeType<?>> RECIPE_TYPES = ResourcefulRegistries.create(BuiltInRegistries.RECIPE_TYPE, ModConstants.MOD_ID);

    public static final RegistryEntry<RecipeType<HiveRecipe>> HIVE_RECIPE_TYPE = RECIPE_TYPES.register("hive", () -> CodecRecipeType.of("hive"));
    public static final RegistryEntry<RecipeType<BreederRecipe>> BREEDER_RECIPE_TYPE = RECIPE_TYPES.register("breeder", () -> CodecRecipeType.of("breeder"));
    public static final RegistryEntry<RecipeType<MutationRecipe>> MUTATION_RECIPE_TYPE = RECIPE_TYPES.register("mutation", () -> CodecRecipeType.of("mutation"));
    public static final RegistryEntry<RecipeType<SolidificationRecipe>> SOLIDIFICATION_RECIPE_TYPE = RECIPE_TYPES.register("solidification", () -> CodecRecipeType.of("solidification"));
    public static final RegistryEntry<RecipeType<FlowHiveRecipe>> FLOW_HIVE_RECIPE_TYPE = RECIPE_TYPES.register("flow_hive", () -> CodecRecipeType.of("flow_hive"));
    public static final RegistryEntry<RecipeType<HoneyGenRecipe>> HONEY_GEN_RECIPE_TYPE = RECIPE_TYPES.register("honey_gen", () -> CodecRecipeType.of("honey_gen"));


}
