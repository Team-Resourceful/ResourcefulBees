package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.recipes.BreederRecipe;
import com.teamresourceful.resourcefulbees.common.recipes.HiveRecipe;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.RegistryEntry;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.ResourcefulRegistries;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.ResourcefulRegistry;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipeSerializer;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class ModRecipeSerializers {

    private ModRecipeSerializers() {
        throw new UtilityClassError();
    }

    public static final ResourcefulRegistry<RecipeSerializer<?>> RECIPE_SERIALIZERS = ResourcefulRegistries.create(Registry.RECIPE_SERIALIZER, ModConstants.MOD_ID);

    public static final RegistryEntry<CodecRecipeSerializer<HiveRecipe>> HIVE_RECIPE = RECIPE_SERIALIZERS.register("hive", () -> new CodecRecipeSerializer<>(ModRecipes.HIVE_RECIPE_TYPE.get(), HiveRecipe::codec));
    public static final RegistryEntry<CodecRecipeSerializer<BreederRecipe>> BREEDER_RECIPE = RECIPE_SERIALIZERS.register("breeder", () -> new CodecRecipeSerializer<>(ModRecipes.BREEDER_RECIPE_TYPE.get(), BreederRecipe::codec));

}
