package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.BreederRecipe;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.SolidificationRecipe;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.CentrifugeRecipe;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipeSerializerV2;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModRecipeSerializers {

    private ModRecipeSerializers() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ResourcefulBees.MOD_ID);

    public static final RegistryObject<RecipeSerializer<?>> CENTRIFUGE_RECIPE = RECIPE_SERIALIZERS.register("centrifuge", () -> new CodecRecipeSerializerV2<>(ModRecipeTypes.CENTRIFUGE_RECIPE_TYPE.get(), CentrifugeRecipe::codec));
    public static final RegistryObject<RecipeSerializer<?>> SOLIDIFICATION_RECIPE = RECIPE_SERIALIZERS.register("solidification", () -> new CodecRecipeSerializerV2<>(ModRecipeTypes.SOLIDIFICATION_RECIPE_TYPE.get(), SolidificationRecipe::codec));
    public static final RegistryObject<RecipeSerializer<?>> BREEDER_RECIPE = RECIPE_SERIALIZERS.register("breeder", () -> new CodecRecipeSerializerV2<>(ModRecipeTypes.BREEDER_RECIPE_TYPE.get(), BreederRecipe::codec));

}
