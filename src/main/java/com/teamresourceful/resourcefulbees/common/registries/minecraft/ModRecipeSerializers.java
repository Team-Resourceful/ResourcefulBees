package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.mojang.serialization.MapCodec;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.recipes.HiveRecipe;
import com.teamresourceful.resourcefulbees.common.registries.RegistryHelper;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class ModRecipeSerializers {

    private ModRecipeSerializers() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final ResourcefulRegistry<RecipeSerializer<?>> RECIPE_SERIALIZERS = RegistryHelper.create(BuiltInRegistries.RECIPE_SERIALIZER, ModConstants.MOD_ID);

    public static final RegistryEntry<RecipeSerializer<HiveRecipe>> HIVE_RECIPE = RECIPE_SERIALIZERS.register("hive", () -> new RecipeSerializer<>(HiveRecipe.MAP_CODEC, HiveRecipe.STREAM_CODEC));
//    public static final RegistryEntry<RecipeSerializer<BreederRecipe>> BREEDER_RECIPE = RECIPE_SERIALIZERS.register("breeder", () -> new RecipeSerializer<>(BreederRecipe::codec, BreederRecipe::packetCodec));
//    public static final RegistryEntry<RecipeSerializer<MutationRecipe>> MUTATION_RECIPE = RECIPE_SERIALIZERS.register("mutation", () -> new RecipeSerializer<>(ModRecipes.MUTATION_RECIPE_TYPE.get(), MutationRecipe::codec));
//    public static final RegistryEntry<RecipeSerializer<SolidificationRecipe>> SOLIDIFICATION_RECIPE = RECIPE_SERIALIZERS.register("solidification", () -> new RecipeSerializer<>(ModRecipes.SOLIDIFICATION_RECIPE_TYPE.get(), SolidificationRecipe::codec));
//    public static final RegistryEntry<RecipeSerializer<FlowHiveRecipe>> FLOW_HIVE_RECIPE = RECIPE_SERIALIZERS.register("flow_hive", () -> new RecipeSerializer<>(ModRecipes.FLOW_HIVE_RECIPE_TYPE.get(), FlowHiveRecipe::codec));
//    public static final RegistryEntry<RecipeSerializer<HoneyGenRecipe>> HONEY_GEN_RECIPE = RECIPE_SERIALIZERS.register("honey_gen", () -> new RecipeSerializer<>(ModRecipes.HONEY_GEN_RECIPE_TYPE.get(), HoneyGenRecipe::codec));
//    public static final RegistryEntry<RecipeSerializer<CentrifugeRecipe>> CENTRIFUGE_RECIPE = RECIPE_SERIALIZERS.register("centrifuge", () -> new RecipeSerializer<>(ModRecipes.CENTRIFUGE_RECIPE_TYPE.get(), CentrifugeRecipe::codec));

}
