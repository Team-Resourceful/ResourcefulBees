//package com.teamresourceful.resourcefulbees.common.recipes;
//
//import com.mojang.serialization.Codec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import com.teamresourceful.resourcefulbees.api.compat.BeeCompat;
//import com.teamresourceful.resourcefulbees.api.tiers.ApiaryTier;
//import com.teamresourceful.resourcefulbees.api.tiers.BeehiveTier;
//import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipeSerializers;
//import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
//import com.teamresourceful.resourcefullib.common.codecs.recipes.ItemStackCodec;
//import com.teamresourceful.resourcefullib.common.codecs.tags.HolderSetCodec;
//import com.teamresourceful.resourcefullib.common.item.OptionalItemStack;
//import com.teamresourceful.resourcefullib.common.recipe.CodecRecipe;
//import com.teamresourceful.resourcefullib.common.recipe.CodecRecipeSerializer;
//import net.minecraft.core.HolderSet;
//import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraft.resources.Identifier;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.crafting.*;
//import net.minecraft.world.level.Level;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//public record HiveRecipe(
//        Identifier id,
//        HolderSet<EntityType<?>> bees,
//        Map<BeehiveTier, ItemStack> hiveCombs,
//        Map<ApiaryTier,
//        ItemStack> apiaryCombs
//) implements Recipe<RecipeInput> {
//
//    public static Codec<HiveRecipe> codec(Identifier id) {
//        return RecordCodecBuilder.create(instance -> instance.group(
//                RecordCodecBuilder.point(id),
//                HolderSetCodec.of(BuiltInRegistries.ENTITY_TYPE).fieldOf("bees").forGetter(HiveRecipe::bees),
//                Codec.unboundedMap(BeehiveTier.CODEC, ItemStackCodec.CODEC).fieldOf("hiveCombs").orElseGet(HashMap::new).forGetter(HiveRecipe::hiveCombs),
//                Codec.unboundedMap(ApiaryTier.CODEC, ItemStackCodec.CODEC).fieldOf("apiaryCombs").orElseGet(HashMap::new).forGetter(HiveRecipe::apiaryCombs)
//        ).apply(instance, HiveRecipe::new));
//    }
//
//    private static Optional<RecipeHolder<HiveRecipe>> findRecipe(RecipeManager manager, EntityType<?> bee) {
//        return manager
//                .getAllRecipesFor(ModRecipes.HIVE_RECIPE_TYPE.get())
//                .stream()
//                .filter(recipe -> recipe.value().bees().contains(bee.builtInRegistryHolder())).findFirst();
//    }
//
//    public static Optional<ItemStack> getHiveOutput(BeehiveTier tier, Entity entity) {
//        Optional<RecipeHolder<HiveRecipe>> recipe = findRecipe((RecipeManager) entity.level().recipeAccess(), entity.getType());
//        return OptionalItemStack.ofNullable(recipe.map(t -> t.value().getHiveOutput(tier)).orElseGet(() -> {
//            if (entity instanceof BeeCompat compat) {
//                return compat.resourcefulBees$getHiveOutput(tier);
//            }
//            return ItemStack.EMPTY;
//        }));
//    }
//
//    public static Optional<ItemStack> getApiaryOutput(ApiaryTier tier, Entity entity) {
//        Optional<RecipeHolder<HiveRecipe>> recipe = findRecipe((RecipeManager) entity.level().recipeAccess(), entity.getType());
//        return OptionalItemStack.ofNullable(recipe.map(t -> t.value().getApiaryOutput(tier)).orElseGet(() -> {
//            if (entity instanceof BeeCompat compat) {
//                return compat.resourcefulBees$getApiaryOutput(tier);
//            }
//            return ItemStack.EMPTY;
//        }));
//    }
//
//
//    public ItemStack getHiveOutput(BeehiveTier tier) {
//        return hiveCombs().getOrDefault(tier, ItemStack.EMPTY).copy();
//    }
//
//    public ItemStack getApiaryOutput(ApiaryTier tier) {
//        return apiaryCombs().getOrDefault(tier, ItemStack.EMPTY).copy();
//    }
//
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
//        return ModRecipeSerializers.HIVE_RECIPE.get();
//    }
//
//    @Override
//    public RecipeType<? extends Recipe<RecipeInput>> getType() {
//        return ModRecipes.HIVE_RECIPE_TYPE.get();
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
