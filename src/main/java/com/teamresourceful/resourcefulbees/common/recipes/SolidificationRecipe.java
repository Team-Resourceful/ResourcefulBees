//package com.teamresourceful.resourcefulbees.common.recipes;
//
//import com.mojang.serialization.Codec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import com.teamresourceful.resourcefulbees.common.recipes.base.RecipeFluid;
//import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipeSerializers;
//import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
//import com.teamresourceful.resourcefullib.common.codecs.recipes.ItemStackCodec;
//import com.teamresourceful.resourcefullib.common.recipe.CodecRecipe;
//import com.teamresourceful.resourcefullib.common.recipe.CodecRecipeSerializer;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.resources.Identifier;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.crafting.*;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.material.Fluid;
//import org.jspecify.annotations.NonNull;
//
//import java.util.Optional;
//
//public record SolidificationRecipe(Identifier id, RecipeFluid fluid, ItemStack stack) implements Recipe<RecipeInput> {
//
//    public static Codec<SolidificationRecipe> codec(Identifier id) {
//        return RecordCodecBuilder.create(instance -> instance.group(
//                RecordCodecBuilder.point(id),
//                RecipeFluid.CODEC.fieldOf("fluid").forGetter(SolidificationRecipe::fluid),
//                ItemStackCodec.CODEC.fieldOf("result").forGetter(SolidificationRecipe::stack)
//        ).apply(instance, SolidificationRecipe::new));
//    }
//
//    public static Optional<RecipeHolder<SolidificationRecipe>> findRecipe(RecipeManager manager, Fluid fluid, CompoundTag tag) {
//        return manager
//            .getAllRecipesFor(ModRecipes.SOLIDIFICATION_RECIPE_TYPE.get())
//            .stream()
//            .filter(recipe -> recipe.value().fluid().matches(fluid, tag))
//            .findFirst();
//    }
//
//    public static boolean matches(RecipeManager manager, Fluid fluid, CompoundTag tag) {
//        return findRecipe(manager, fluid, tag).isPresent();
//    }
//
//    @Override
//    public boolean matches(@NonNull RecipeInput recipeInput, @NonNull Level level) {
//        return false;
//    }
//
//    @Override
//    public ItemStack assemble(@NonNull RecipeInput input) {
//        return null;
//    }
//
//    @Override
//    public boolean showNotification() {
//        return false;
//    }
//
//    @Override
//    public String group() {
//        return "";
//    }
//
//    @Override
//    public @NonNull RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
//        return ModRecipeSerializers.SOLIDIFICATION_RECIPE.get();
//    }
//
//    @Override
//    public @NonNull RecipeType<? extends Recipe<RecipeInput>> getType() {
//        return ModRecipes.SOLIDIFICATION_RECIPE_TYPE.get();
//    }
//
//    @Override
//    public PlacementInfo placementInfo() {
//        return null;
//    }
//
//    @Override
//    public RecipeBookCategory recipeBookCategory() {
//        return null;
//    }
//}
