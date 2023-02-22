package com.teamresourceful.resourcefulbees.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.BreederConstants;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.codecs.recipes.IngredientCodec;
import com.teamresourceful.resourcefullib.common.codecs.recipes.ItemStackCodec;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public record BreederRecipe(ResourceLocation id, BreederPair parent1, BreederPair parent2, Optional<Ingredient> input, WeightedCollection<BreederOutput> outputs, int time) implements CodecRecipe<Container> {

    public static final Codec<WeightedCollection<BreederOutput>> RANDOM_COLLECTION_CODEC = CodecExtras.set(BreederOutput.CODEC).comapFlatMap(BreederOutput::convertToRandomCollection, BreederOutput::convertToSet);
    public static final Codec<WeightedCollection<BreederOutput>> RANDOM_COLLECTION_NETWORK_CODEC = CodecExtras.set(BreederOutput.NETWORK_CODEC).comapFlatMap(BreederOutput::convertToRandomCollection, BreederOutput::convertToSet);

    public static Codec<BreederRecipe> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
            RecordCodecBuilder.point(id),
            BreederPair.CODEC.fieldOf("parent1").forGetter(BreederRecipe::parent1),
            BreederPair.CODEC.fieldOf("parent2").forGetter(BreederRecipe::parent2),
            IngredientCodec.CODEC.optionalFieldOf("input").forGetter(BreederRecipe::input),
            BreederRecipe.RANDOM_COLLECTION_CODEC.fieldOf("outputs").forGetter(BreederRecipe::outputs),
            Codec.intRange(100, 72000).fieldOf("time").orElse(BreederConstants.DEFAULT_BREEDER_TIME).forGetter(BreederRecipe::time)
        ).apply(instance, BreederRecipe::new));
    }

    public static Codec<BreederRecipe> networkCodec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                BreederPair.NETWORK_CODEC.fieldOf("parent1").forGetter(BreederRecipe::parent1),
                BreederPair.NETWORK_CODEC.fieldOf("parent2").forGetter(BreederRecipe::parent2),
                IngredientCodec.NETWORK_CODEC.optionalFieldOf("input").forGetter(BreederRecipe::input),
                BreederRecipe.RANDOM_COLLECTION_NETWORK_CODEC.fieldOf("outputs").forGetter(BreederRecipe::outputs),
                Codec.intRange(100, 72000).fieldOf("time").orElse(BreederConstants.DEFAULT_BREEDER_TIME).forGetter(BreederRecipe::time)
        ).apply(instance, BreederRecipe::new));
    }

    @Override
    public boolean matches(@NotNull Container inventory, @NotNull Level level) {
        final boolean hasInput = (input.isEmpty() || input.get().test(inventory.getItem(4)));
        return ((parent1.matches(inventory, 0) && parent2.matches(inventory, 2)) || (parent1.matches(inventory, 2) && parent2.matches(inventory, 0))) && hasInput;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.BREEDER_RECIPE.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipes.BREEDER_RECIPE_TYPE.get();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, parent1.parent(), parent1.feedItem(), parent2.parent(), parent2.feedItem());
    }

    public record BreederPair(Ingredient parent, Optional<String> displayEntity, int feedAmount, Ingredient feedItem, Optional<ItemStack> returnItem){
        public static final Codec<BreederPair> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                IngredientCodec.CODEC.fieldOf("parent").forGetter(BreederPair::parent),
                Codec.STRING.optionalFieldOf("entity").forGetter(BreederPair::displayEntity),
                Codec.INT.fieldOf("feedAmount").orElse(1).forGetter(BreederPair::feedAmount),
                IngredientCodec.CODEC.fieldOf("feedItem").forGetter(BreederPair::feedItem),
                ItemStackCodec.CODEC.optionalFieldOf("returnItem").forGetter(BreederPair::returnItem)
        ).apply(instance, BreederPair::new));

        public static final Codec<BreederPair> NETWORK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                IngredientCodec.NETWORK_CODEC.fieldOf("parent").forGetter(BreederPair::parent),
                Codec.STRING.optionalFieldOf("entity").forGetter(BreederPair::displayEntity),
                Codec.INT.fieldOf("feedAmount").orElse(1).forGetter(BreederPair::feedAmount),
                IngredientCodec.NETWORK_CODEC.fieldOf("feedItem").forGetter(BreederPair::feedItem),
                ItemStackCodec.NETWORK_CODEC.optionalFieldOf("returnItem").forGetter(BreederPair::returnItem)
        ).apply(instance, BreederPair::new));

        public boolean matches(Container inventory, int offset) {
            return parent.test(inventory.getItem(offset)) && feedItem.test(inventory.getItem(offset+1));
        }
    }

    public record BreederOutput(ItemStack output, Optional<String> displayEntity, double weight, double chance){
        public static final Codec<BreederOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStackCodec.CODEC.fieldOf("output").forGetter(BreederOutput::output),
                Codec.STRING.optionalFieldOf("entity").forGetter(BreederOutput::displayEntity),
                Codec.doubleRange(0.0d, Double.MAX_VALUE).fieldOf("weight").orElse(BeeConstants.DEFAULT_BREED_WEIGHT).forGetter(BreederOutput::weight),
                Codec.doubleRange(0.0d, 1.0d).fieldOf("chance").orElse(BeeConstants.DEFAULT_BREED_CHANCE).forGetter(BreederOutput::chance)
        ).apply(instance, BreederOutput::new));

        public static final Codec<BreederOutput> NETWORK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStackCodec.NETWORK_CODEC.fieldOf("output").forGetter(BreederOutput::output),
                Codec.STRING.optionalFieldOf("entity").forGetter(BreederOutput::displayEntity),
                Codec.doubleRange(0.0d, Double.MAX_VALUE).fieldOf("weight").orElse(BeeConstants.DEFAULT_BREED_WEIGHT).forGetter(BreederOutput::weight),
                Codec.doubleRange(0.0d, 1.0d).fieldOf("chance").orElse(BeeConstants.DEFAULT_BREED_CHANCE).forGetter(BreederOutput::chance)
        ).apply(instance, BreederOutput::new));

        private static DataResult<WeightedCollection<BreederOutput>> convertToRandomCollection(Set<BreederOutput> set) {
            return DataResult.success(set.stream().collect(WeightedCollection.getCollector(BreederOutput::weight)));
        }

        private static Set<BreederOutput> convertToSet(WeightedCollection<BreederOutput> randomCollection) {
            return new HashSet<>(randomCollection.getMap().values());
        }
    }
}
