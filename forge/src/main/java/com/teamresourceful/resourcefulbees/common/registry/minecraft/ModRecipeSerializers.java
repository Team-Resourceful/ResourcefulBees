package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.FlowHiveRecipe;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.HoneyGenRecipe;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.SolidificationRecipe;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.CentrifugeRecipe;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipeSerializer;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class ModRecipeSerializers {

    private ModRecipeSerializers() {
        throw new UtilityClassError();
    }

    public static final ResourcefulRegistry<RecipeSerializer<?>> RECIPE_SERIALIZERS = ResourcefulRegistries.create(BuiltInRegistries.RECIPE_SERIALIZER, ModConstants.MOD_ID);

    public static final RegistryEntry<CodecRecipeSerializer<CentrifugeRecipe>> CENTRIFUGE_RECIPE = RECIPE_SERIALIZERS.register("centrifuge", () -> new CodecRecipeSerializer<>(ModRecipeTypes.CENTRIFUGE_RECIPE_TYPE.get(), CentrifugeRecipe::codec));
    public static final RegistryEntry<CodecRecipeSerializer<SolidificationRecipe>> SOLIDIFICATION_RECIPE = RECIPE_SERIALIZERS.register("solidification", () -> new CodecRecipeSerializer<>(ModRecipeTypes.SOLIDIFICATION_RECIPE_TYPE.get(), SolidificationRecipe::codec));
    public static final RegistryEntry<CodecRecipeSerializer<FlowHiveRecipe>> FLOW_HIVE_RECIPE = RECIPE_SERIALIZERS.register("flow_hive", () -> new CodecRecipeSerializer<>(ModRecipeTypes.FLOW_HIVE_RECIPE_TYPE.get(), FlowHiveRecipe::codec));
    public static final RegistryEntry<CodecRecipeSerializer<HoneyGenRecipe>> HONEY_GEN_RECIPE = RECIPE_SERIALIZERS.register("honey_gen", () -> new CodecRecipeSerializer<>(ModRecipeTypes.HONEY_GEN_RECIPE_TYPE.get(), HoneyGenRecipe::codec));

}
