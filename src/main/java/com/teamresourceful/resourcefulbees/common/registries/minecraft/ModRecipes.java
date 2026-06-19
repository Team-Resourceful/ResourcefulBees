package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.recipes.*;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.registries.RegistryHelper;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeType;

public final class ModRecipes {

    private ModRecipes() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final ResourcefulRegistry<RecipeType<?>> RECIPE_TYPES = RegistryHelper.create(BuiltInRegistries.RECIPE_TYPE, ModConstants.MOD_ID);

    public static final RegistryEntry<RecipeType<HiveRecipe>> HIVE_RECIPE_TYPE = RECIPE_TYPES.register("hive", () -> RecipeType.register("hive"));
    public static final RegistryEntry<RecipeType<BreederRecipe>> BREEDER_RECIPE_TYPE = RECIPE_TYPES.register("breeder", () -> RecipeType.register("breeder"));
    public static final RegistryEntry<RecipeType<MutationRecipe>> MUTATION_RECIPE_TYPE = RECIPE_TYPES.register("mutation", () -> RecipeType.register("mutation"));
    public static final RegistryEntry<RecipeType<SolidificationRecipe>> SOLIDIFICATION_RECIPE_TYPE = RECIPE_TYPES.register("solidification", () -> RecipeType.register("solidification"));
    public static final RegistryEntry<RecipeType<FlowHiveRecipe>> FLOW_HIVE_RECIPE_TYPE = RECIPE_TYPES.register("flow_hive", () -> RecipeType.register("flow_hive"));
    public static final RegistryEntry<RecipeType<HoneyGenRecipe>> HONEY_GEN_RECIPE_TYPE = RECIPE_TYPES.register("honey_gen", () -> RecipeType.register("honey_gen"));
    public static final RegistryEntry<RecipeType<CentrifugeRecipe>> CENTRIFUGE_RECIPE_TYPE = RECIPE_TYPES.register("centrifuge", () -> RecipeType.register("centrifuge"));


}
