package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.recipe.base.CodecRecipeType;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.BreederRecipe;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.SolidificationRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {

    private ModRecipeTypes() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }
    
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, ResourcefulBees.MOD_ID);

    public static final RegistryObject<RecipeType<CentrifugeRecipe>> CENTRIFUGE_RECIPE_TYPE = RECIPE_TYPES.register("centrifuge", () -> new CodecRecipeType<>("centrifuge"));
    public static final RegistryObject<RecipeType<SolidificationRecipe>> SOLIDIFICATION_RECIPE_TYPE = RECIPE_TYPES.register("solidification", () -> new CodecRecipeType<>("solidification"));
    public static final RegistryObject<RecipeType<BreederRecipe>> BREEDER_RECIPE_TYPE = RECIPE_TYPES.register("breeder", () -> new CodecRecipeType<>("breeder"));

}
