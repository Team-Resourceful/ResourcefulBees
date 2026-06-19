//package com.teamresourceful.resourcefulbees.common.recipes;
//
//import com.mojang.serialization.Codec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
//import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
//import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipeSerializers;
//import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
//import com.teamresourceful.resourcefulbees.common.setup.data.beedata.mutation.MutationEntry;
//import com.teamresourceful.resourcefulbees.mixin.common.RecipeManagerInvoker;
//import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
//import com.teamresourceful.resourcefullib.common.color.Color;
//import com.teamresourceful.resourcefullib.common.recipe.CodecRecipe;
//import com.teamresourceful.resourcefullib.common.recipe.CodecRecipeSerializer;
//import net.minecraft.resources.Identifier;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.crafting.*;
//import net.minecraft.world.level.Level;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public record MutationRecipe(Identifier id, Color pollenBaseColor, Color pollenTopColor, Map<MutationType, WeightedCollection<MutationType>> mutations) implements Recipe<RecipeInput> {
//
//    public static Codec<MutationRecipe> codec(Identifier id) {
//        return RecordCodecBuilder.create(instance -> instance.group(
//            RecordCodecBuilder.point(id),
//            Color.CODEC.fieldOf("pollenBaseColor").orElse(BeeConstants.DEFAULT_POLLEN_BASE_COLOR).forGetter(MutationRecipe::getPollenBaseColor),
//            Color.CODEC.fieldOf("pollenTopColor").orElse(BeeConstants.DEFAULT_POLLEN_TOP_COLOR).forGetter(MutationRecipe::getPollenTopColor),
//            MutationEntry.MUTATION_MAP_CODEC.fieldOf("mutations").orElse(new HashMap<>()).forGetter(MutationRecipe::mutations)
//        ).apply(instance, MutationRecipe::new));
//    }
//
//    public static MutationRecipe getRecipe(@NotNull Level level, Identifier id) {
//        return (MutationRecipe) ((RecipeManagerInvoker) level.getRecipeManager()).callByType(ModRecipes.MUTATION_RECIPE_TYPE.get()).get(id);
//    }
//
//    public Color getPollenBaseColor() {
//        return pollenBaseColor;
//    }
//
//    public Color getPollenTopColor() {
//        return pollenTopColor;
//    }
//
//    @Override
//    public boolean matches(RecipeInput recipeInput, Level level) {
//        return false;
//    }
//
//    @Override
//    public ItemStack assemble(RecipeInput input) {
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
//    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
//        return ModRecipeSerializers.MUTATION_RECIPE.get();
//    }
//
//    @Override
//    public CodecRecipeSerializer<? extends CodecRecipe<RecipeInput>> serializer() {
//
//    }
//
//    @Override
//    public @NotNull
//    RecipeType<?> getType() {
//        return ModRecipes.MUTATION_RECIPE_TYPE.get();
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
