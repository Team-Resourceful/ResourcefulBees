package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.BreederRecipe;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.FlowHiveRecipe;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.HiveRecipe;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.SolidificationRecipe;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipeType;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModRecipeTypes {

    private ModRecipeTypes() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }
    
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, ResourcefulBees.MOD_ID);

    public static final RegistryObject<RecipeType<CentrifugeRecipe>> CENTRIFUGE_RECIPE_TYPE = RECIPE_TYPES.register("centrifuge", () -> CodecRecipeType.of("centrifuge"));
    public static final RegistryObject<RecipeType<SolidificationRecipe>> SOLIDIFICATION_RECIPE_TYPE = RECIPE_TYPES.register("solidification", () -> CodecRecipeType.of("solidification"));
    public static final RegistryObject<RecipeType<BreederRecipe>> BREEDER_RECIPE_TYPE = RECIPE_TYPES.register("breeder", () -> CodecRecipeType.of("breeder"));
    public static final RegistryObject<RecipeType<FlowHiveRecipe>> FLOW_HIVE_RECIPE_TYPE = RECIPE_TYPES.register("flowhive", () -> CodecRecipeType.of("flowhive"));
    public static final RegistryObject<RecipeType<HiveRecipe>> HIVE_RECIPE_TYPE = RECIPE_TYPES.register("hive", () -> CodecRecipeType.of("hive"));

}
