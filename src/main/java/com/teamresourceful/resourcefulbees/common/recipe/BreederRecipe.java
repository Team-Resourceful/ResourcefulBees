package com.teamresourceful.resourcefulbees.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefulbees.common.utils.RandomCollection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public record BreederRecipe(ResourceLocation id, BreederPair parent1, BreederPair parent2, Optional<Ingredient> input, RandomCollection<BreederOutput> outputs, int time) implements Recipe<Container> {

    public static final RecipeType<CentrifugeRecipe> BREEDER_RECIPE_TYPE = RecipeType.register(ResourcefulBees.MOD_ID + ":breeder");

    public static final Codec<RandomCollection<BreederOutput>> RANDOM_COLLECTION_CODEC = CodecUtils.createSetCodec(BreederOutput.CODEC).comapFlatMap(BreederOutput::convertToRandomCollection, BreederOutput::convertToSet);

    public static Codec<BreederRecipe> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
            MapCodec.of(Encoder.empty(), Decoder.unit(() -> id)).forGetter(BreederRecipe::getId),
            BreederPair.CODEC.fieldOf("parent1").forGetter(BreederRecipe::parent1),
            BreederPair.CODEC.fieldOf("parent2").forGetter(BreederRecipe::parent2),
            CodecUtils.INGREDIENT_CODEC.optionalFieldOf("input").forGetter(BreederRecipe::input),
            BreederRecipe.RANDOM_COLLECTION_CODEC.fieldOf("outputs").forGetter(BreederRecipe::outputs),
            Codec.intRange(100, 72000).fieldOf("time").orElse(1200).forGetter(BreederRecipe::time)
        ).apply(instance, BreederRecipe::new));
    }

    @Override
    public boolean matches(@NotNull Container inventory, @NotNull Level level) {
        return parent1.matches(inventory, 0) && parent2.matches(inventory, 2) && (input.isEmpty() || input.get().test(inventory.getItem(4)));
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container inventory) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.BREEDER_RECIPE.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return BREEDER_RECIPE_TYPE;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<BreederRecipe> {

        @Override
        public @NotNull BreederRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            return BreederRecipe.codec(id).parse(JsonOps.INSTANCE, json).getOrThrow(false, s -> ResourcefulBees.LOGGER.error("Could not parse Solidification Recipe!!"));

        }

        @Nullable
        @Override
        public BreederRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buffer) {
            Optional<BreederRecipe> result = BreederRecipe.codec(id).parse(JsonOps.COMPRESSED, ModConstants.GSON.fromJson(buffer.readUtf(), JsonArray.class)).result();
            return result.orElse(null);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull BreederRecipe recipe) {
            BreederRecipe.codec(recipe.id).encodeStart(JsonOps.COMPRESSED, recipe).result().ifPresent(element -> buffer.writeUtf(element.toString()));
        }
    }

    public static record BreederPair(Ingredient parent, Optional<String> displayEntity, Ingredient feedItem){
        public static final Codec<BreederPair> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CodecUtils.INGREDIENT_CODEC.fieldOf("parent").forGetter(BreederPair::parent),
                Codec.STRING.optionalFieldOf("entity").forGetter(BreederPair::displayEntity),
                CodecUtils.INGREDIENT_CODEC.fieldOf("feedItem").forGetter(BreederPair::feedItem)
        ).apply(instance, BreederPair::new));

        public boolean matches(Container inventory, int offset) {
            return parent.test(inventory.getItem(offset)) && feedItem.test(inventory.getItem(offset+1));
        }
    }

    public static record BreederOutput(ItemStack output, ResourceLocation displayEntity, double weight, double chance){
        public static final Codec<BreederOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CodecUtils.ITEM_STACK_CODEC.fieldOf("output").forGetter(BreederOutput::output),
                ResourceLocation.CODEC.fieldOf("entity").forGetter(BreederOutput::displayEntity),
                Codec.doubleRange(0.0d, Double.MAX_VALUE).fieldOf("weight").orElse(BeeConstants.DEFAULT_BREED_WEIGHT).forGetter(BreederOutput::weight),
                Codec.doubleRange(0.0d, 1.0d).fieldOf("chance").orElse(BeeConstants.DEFAULT_BREED_CHANCE).forGetter(BreederOutput::chance)
        ).apply(instance, BreederOutput::new));

        private static DataResult<RandomCollection<BreederOutput>> convertToRandomCollection(Set<BreederOutput> set) {
            return DataResult.success(set.stream().collect(RandomCollection.getCollector(BreederOutput::weight)));
        }

        private static Set<BreederOutput> convertToSet(RandomCollection<BreederOutput> randomCollection) {
            return new HashSet<>(randomCollection.getMap().values());
        }
    }
}
