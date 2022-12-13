package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.*;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.RegistryEntry;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.ResourcefulRegistries;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.ResourcefulRegistry;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipeType;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.RecipeType;

public final class ModRecipeTypes {

    private ModRecipeTypes() {
        throw new UtilityClassError();
    }
    
    public static final ResourcefulRegistry<RecipeType<?>> RECIPE_TYPES = ResourcefulRegistries.create(Registry.RECIPE_TYPE, ResourcefulBees.MOD_ID);

    public static final RegistryEntry<RecipeType<CentrifugeRecipe>> CENTRIFUGE_RECIPE_TYPE = RECIPE_TYPES.register("centrifuge", () -> CodecRecipeType.of("centrifuge"));
    public static final RegistryEntry<RecipeType<SolidificationRecipe>> SOLIDIFICATION_RECIPE_TYPE = RECIPE_TYPES.register("solidification", () -> CodecRecipeType.of("solidification"));
    public static final RegistryEntry<RecipeType<BreederRecipe>> BREEDER_RECIPE_TYPE = RECIPE_TYPES.register("breeder", () -> CodecRecipeType.of("breeder"));
    public static final RegistryEntry<RecipeType<FlowHiveRecipe>> FLOW_HIVE_RECIPE_TYPE = RECIPE_TYPES.register("flowhive", () -> CodecRecipeType.of("flowhive"));
    public static final RegistryEntry<RecipeType<HiveRecipe>> HIVE_RECIPE_TYPE = RECIPE_TYPES.register("hive", () -> CodecRecipeType.of("hive"));
    public static final RegistryEntry<RecipeType<MutationRecipe>> MUTATION_RECIPE_TYPE = RECIPE_TYPES.register("mutation", () -> CodecRecipeType.of("mutation"));

}
