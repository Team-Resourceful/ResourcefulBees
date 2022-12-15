package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.*;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.CentrifugeRecipe;
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

    public static final RegistryEntry<CodecRecipeSerializer<CentrifugeRecipe>> CENTRIFUGE_RECIPE = RECIPE_SERIALIZERS.register("centrifuge", () -> new CodecRecipeSerializer<>(ModRecipeTypes.CENTRIFUGE_RECIPE_TYPE.get(), CentrifugeRecipe::codec));
    public static final RegistryEntry<CodecRecipeSerializer<SolidificationRecipe>> SOLIDIFICATION_RECIPE = RECIPE_SERIALIZERS.register("solidification", () -> new CodecRecipeSerializer<>(ModRecipeTypes.SOLIDIFICATION_RECIPE_TYPE.get(), SolidificationRecipe::codec));
    public static final RegistryEntry<CodecRecipeSerializer<BreederRecipe>> BREEDER_RECIPE = RECIPE_SERIALIZERS.register("breeder", () -> new CodecRecipeSerializer<>(ModRecipeTypes.BREEDER_RECIPE_TYPE.get(), BreederRecipe::codec));
    public static final RegistryEntry<CodecRecipeSerializer<FlowHiveRecipe>> FLOW_HIVE_RECIPE = RECIPE_SERIALIZERS.register("flow_hive", () -> new CodecRecipeSerializer<>(ModRecipeTypes.FLOW_HIVE_RECIPE_TYPE.get(), FlowHiveRecipe::codec));
    public static final RegistryEntry<CodecRecipeSerializer<HiveRecipe>> HIVE_RECIPE = RECIPE_SERIALIZERS.register("hive", () -> new CodecRecipeSerializer<>(ModRecipeTypes.HIVE_RECIPE_TYPE.get(), HiveRecipe::codec));
    public static final RegistryEntry<CodecRecipeSerializer<MutationRecipe>> MUTATION_RECIPE = RECIPE_SERIALIZERS.register("mutation", () -> new CodecRecipeSerializer<>(ModRecipeTypes.MUTATION_RECIPE_TYPE.get(), MutationRecipe::codec));

}
