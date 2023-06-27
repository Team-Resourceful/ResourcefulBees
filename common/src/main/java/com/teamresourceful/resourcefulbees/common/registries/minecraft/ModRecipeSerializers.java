package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.recipes.*;
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

    public static final RegistryEntry<CodecRecipeSerializer<HiveRecipe>> HIVE_RECIPE = RECIPE_SERIALIZERS.register("hive", () -> new CodecRecipeSerializer<>(ModRecipes.HIVE_RECIPE_TYPE.get(), HiveRecipe::codec));
    public static final RegistryEntry<CodecRecipeSerializer<BreederRecipe>> BREEDER_RECIPE = RECIPE_SERIALIZERS.register("breeder", () -> new CodecRecipeSerializer<>(ModRecipes.BREEDER_RECIPE_TYPE.get(), BreederRecipe::codec, BreederRecipe::packetCodec));
    public static final RegistryEntry<CodecRecipeSerializer<MutationRecipe>> MUTATION_RECIPE = RECIPE_SERIALIZERS.register("mutation", () -> new CodecRecipeSerializer<>(ModRecipes.MUTATION_RECIPE_TYPE.get(), MutationRecipe::codec));
    public static final RegistryEntry<CodecRecipeSerializer<SolidificationRecipe>> SOLIDIFICATION_RECIPE = RECIPE_SERIALIZERS.register("solidification", () -> new CodecRecipeSerializer<>(ModRecipes.SOLIDIFICATION_RECIPE_TYPE.get(), SolidificationRecipe::codec));
    public static final RegistryEntry<CodecRecipeSerializer<FlowHiveRecipe>> FLOW_HIVE_RECIPE = RECIPE_SERIALIZERS.register("flow_hive", () -> new CodecRecipeSerializer<>(ModRecipes.FLOW_HIVE_RECIPE_TYPE.get(), FlowHiveRecipe::codec));
    public static final RegistryEntry<CodecRecipeSerializer<HoneyGenRecipe>> HONEY_GEN_RECIPE = RECIPE_SERIALIZERS.register("honey_gen", () -> new CodecRecipeSerializer<>(ModRecipes.HONEY_GEN_RECIPE_TYPE.get(), HoneyGenRecipe::codec));

}
