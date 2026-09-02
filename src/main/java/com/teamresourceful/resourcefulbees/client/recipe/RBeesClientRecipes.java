package com.teamresourceful.resourcefulbees.client.recipe;

import com.teamresourceful.resourcefulbees.common.recipes.*;
import com.teamresourceful.resourcefulbees.common.recipes.breeder.BreederRecipe;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;

import java.util.ArrayList;
import java.util.List;

public final class RBeesClientRecipes {

    private RBeesClientRecipes() {
    }

    private static final List<RecipeHolder<HiveRecipe>> HIVE = new ArrayList<>();
    private static final List<RecipeHolder<BreederRecipe>> BREEDER = new ArrayList<>();
    private static final List<RecipeHolder<CentrifugeRecipe>> CENTRIFUGE = new ArrayList<>();
    private static final List<RecipeHolder<SolidificationRecipe>> SOLIDIFICATION = new ArrayList<>();
    private static final List<RecipeHolder<HoneyGenRecipe>> HONEY_GENERATOR = new ArrayList<>();
    private static final List<RecipeHolder<FlowHiveRecipe>> FLOW_HIVE = new ArrayList<>();
    private static final List<RecipeHolder<MutationRecipe>> MUTATION = new ArrayList<>();

    public static List<RecipeHolder<MutationRecipe>> getMutationRecipes() {
        return List.copyOf(MUTATION);
    }

    public static List<RecipeHolder<HiveRecipe>> getHiveRecipes() {
        return List.copyOf(HIVE);
    }

    public static List<RecipeHolder<BreederRecipe>> getBreederRecipes() {
        return List.copyOf(BREEDER);
    }

    public static List<RecipeHolder<CentrifugeRecipe>> getCentrifugeRecipes() {
        return List.copyOf(CENTRIFUGE);
    }

    public static List<RecipeHolder<SolidificationRecipe>> getSolidificationRecipes() {
        return List.copyOf(SOLIDIFICATION);
    }

    public static List<RecipeHolder<HoneyGenRecipe>> getHoneyGeneratorRecipes() {
        return List.copyOf(HONEY_GENERATOR);
    }

    public static List<RecipeHolder<FlowHiveRecipe>> getFlowHiveRecipes() {
        return List.copyOf(FLOW_HIVE);
    }

    public static void update(RecipeMap recipes) {
        clear();
        HIVE.addAll(recipes.byType(ModRecipes.HIVE_RECIPE_TYPE.get()));
        BREEDER.addAll(recipes.byType(ModRecipes.BREEDER_RECIPE_TYPE.get()));
        CENTRIFUGE.addAll(recipes.byType(ModRecipes.CENTRIFUGE_RECIPE_TYPE.get()));
        SOLIDIFICATION.addAll(recipes.byType(ModRecipes.SOLIDIFICATION_RECIPE_TYPE.get()));
        HONEY_GENERATOR.addAll(recipes.byType(ModRecipes.HONEY_GEN_RECIPE_TYPE.get()));
        FLOW_HIVE.addAll(recipes.byType(ModRecipes.FLOW_HIVE_RECIPE_TYPE.get()));
        MUTATION.addAll(recipes.byType(ModRecipes.MUTATION_RECIPE_TYPE.get()));
    }

    public static void clear() {
        HIVE.clear();
        BREEDER.clear();
        CENTRIFUGE.clear();
        SOLIDIFICATION.clear();
        HONEY_GENERATOR.clear();
        FLOW_HIVE.clear();
        MUTATION.clear();
    }
}