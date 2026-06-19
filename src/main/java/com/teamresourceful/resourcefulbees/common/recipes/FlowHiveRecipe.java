//package com.teamresourceful.resourcefulbees.common.recipes;
//
//import com.mojang.serialization.Codec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import com.teamresourceful.resourcefulbees.common.recipes.base.RecipeFluid;
//import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipeSerializers;
//import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
//import com.teamresourceful.resourcefullib.common.codecs.tags.HolderSetCodec;
//import net.minecraft.core.HolderSet;
//import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraft.resources.Identifier;
//import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.crafting.*;
//import net.minecraft.world.level.Level;
//import org.jspecify.annotations.NonNull;
//
//import java.util.Optional;
//
//public record FlowHiveRecipe(Identifier id, HolderSet<EntityType<?>> bees, RecipeFluid fluid) implements Recipe<RecipeInput> {
//
//    public static Codec<FlowHiveRecipe> codec(Identifier id) {
//        return RecordCodecBuilder.create(instance -> instance.group(
//                RecordCodecBuilder.point(id),
//                HolderSetCodec.of(BuiltInRegistries.ENTITY_TYPE).fieldOf("bees").forGetter(FlowHiveRecipe::bees),
//                RecipeFluid.CODEC.fieldOf("fluid").forGetter(FlowHiveRecipe::fluid)
//        ).apply(instance, FlowHiveRecipe::new));
//    }
//
//    public static Optional<RecipeHolder<FlowHiveRecipe>> findRecipe(RecipeManager manager, EntityType<?> bee) {
//        return manager
//                .getAllRecipesFor(ModRecipes.FLOW_HIVE_RECIPE_TYPE.get())
//                .stream()
//                .filter(recipe -> recipe.value().bees().contains(bee.builtInRegistryHolder()))
//                .findFirst();
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
//        return ModRecipeSerializers.FLOW_HIVE_RECIPE.get();
//    }
//
//    @Override
//    public @NonNull RecipeType<? extends Recipe<RecipeInput>> getType() {
//        return ModRecipes.FLOW_HIVE_RECIPE_TYPE.get();
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
