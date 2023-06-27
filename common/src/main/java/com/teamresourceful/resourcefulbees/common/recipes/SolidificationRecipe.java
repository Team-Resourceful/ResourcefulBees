package com.teamresourceful.resourcefulbees.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.recipes.base.RecipeFluid;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import com.teamresourceful.resourcefullib.common.codecs.recipes.ItemStackCodec;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Predicate;

public record SolidificationRecipe(ResourceLocation id, RecipeFluid fluid, ItemStack stack) implements CodecRecipe<Container> {

    public static Codec<SolidificationRecipe> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                RecipeFluid.CODEC.fieldOf("fluid").forGetter(SolidificationRecipe::fluid),
                ItemStackCodec.CODEC.fieldOf("result").forGetter(SolidificationRecipe::stack)
        ).apply(instance, SolidificationRecipe::new));
    }

    public static Optional<SolidificationRecipe> findRecipe(RecipeManager manager, Predicate<RecipeFluid> tester) {
        return manager
            .getAllRecipesFor(ModRecipes.SOLIDIFICATION_RECIPE_TYPE.get())
            .stream()
            .filter(recipe -> recipe.fluid().matches(tester))
            .findFirst();
    }

    public static boolean matches(RecipeManager manager, Predicate<RecipeFluid> tester) {
        return findRecipe(manager, tester).isPresent();
    }

    @Override
    public boolean matches(@NotNull Container inventory, @NotNull Level world) {
        return false;
    }

    @Override
    public @NotNull
    RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SOLIDIFICATION_RECIPE.get();
    }

    @Override
    public @NotNull
    RecipeType<?> getType() {
        return ModRecipes.SOLIDIFICATION_RECIPE_TYPE.get();
    }
}
