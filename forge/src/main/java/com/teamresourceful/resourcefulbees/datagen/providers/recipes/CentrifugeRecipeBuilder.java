package com.teamresourceful.resourcefulbees.datagen.providers.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class CentrifugeRecipeBuilder implements RecipeBuilder {

    private final Ingredient ingredient;
    private final ResourceLocation id;

    private final List<CentrifugeRecipe.Output<ItemOutput, ItemStack>> itemOutputs = new ArrayList<>();
    private final List<CentrifugeRecipe.Output<FluidOutput, FluidStack>> fluidOutputs = new ArrayList<>();

    private Integer time;
    private Integer rfPerTick;
    private Integer uses;

    private final List<JsonObject> conditions = new ArrayList<>();

    private CentrifugeRecipeBuilder(Ingredient ingredient, ResourceLocation id) {
        this.ingredient = ingredient;
        this.id = id;
    }

    public static CentrifugeRecipeBuilder of(Ingredient ingredient, ResourceLocation id) {
        return new CentrifugeRecipeBuilder(ingredient, id);
    }

    public CentrifugeRecipeBuilder time(int time) {
        this.time = time;
        return this;
    }

    public CentrifugeRecipeBuilder rf(int rfPerTick) {
        this.rfPerTick = rfPerTick;
        return this;
    }

    public CentrifugeRecipeBuilder uses(int uses) {
        this.uses = uses;
        return this;
    }

    public CentrifugeRecipeBuilder addOutput(ItemOutputBuilder output) {
        this.itemOutputs.add(output.build());
        return this;
    }

    public CentrifugeRecipeBuilder addOutput(FluidOutputBuilder output) {
        this.fluidOutputs.add(output.build());
        return this;
    }

    public CentrifugeRecipeBuilder addCondition(String id) {
        JsonObject object = new JsonObject();
        object.addProperty("type", id);
        conditions.add(object);
        return this;
    }

    public CentrifugeRecipeBuilder addCondition(String id, JsonObject json) {
        JsonObject object = new JsonObject();
        object.addProperty("type", id);
        json.entrySet().forEach(entry -> object.add(entry.getKey(), entry.getValue()));
        conditions.add(object);
        return this;
    }

    @Override
    public @NotNull RecipeBuilder unlockedBy(@NotNull String name, @NotNull CriterionTriggerInstance trigger) {
        return this;
    }

    @Override
    public @NotNull RecipeBuilder group(@Nullable String group) {
        return this;
    }

    @Override
    public @NotNull Item getResult() {
        return Items.AIR;
    }

    @Override
    public void save(@NotNull Consumer<FinishedRecipe> consumer, @NotNull ResourceLocation id) {
        consumer.accept(new CentrifugeFinishedRecipe(this.ingredient, this.id, itemOutputs, fluidOutputs, time, rfPerTick, Optional.ofNullable(uses), conditions));
    }

    public record CentrifugeFinishedRecipe(Ingredient ingredient, ResourceLocation id,
                                           List<CentrifugeRecipe.Output<ItemOutput, ItemStack>> itemOutputs,
                                           List<CentrifugeRecipe.Output<FluidOutput, FluidStack>> fluidOutputs,
                                           Integer time, Integer rfPerTick, Optional<Integer> uses,
                                           List<JsonObject> conditions) implements FinishedRecipe {

        @Override
        public void serializeRecipeData(@NotNull JsonObject json) {
            CentrifugeRecipe recipe = new CentrifugeRecipe(id(), ingredient(), itemOutputs(), fluidOutputs(), time(), rfPerTick(), uses());
            CentrifugeRecipe.codec(id())
                    .encodeStart(JsonOps.INSTANCE, recipe)
                    .getOrThrow(false, s -> ResourcefulBees.LOGGER.error("COULD NOT SERIALIZE DATA GEN RECIPE"))
                    .getAsJsonObject()
                    .entrySet()
                    .forEach(entry -> json.add(entry.getKey(), entry.getValue()));
            JsonArray jsonConditions = new JsonArray();
            conditions.forEach(jsonConditions::add);
            json.add("conditions", jsonConditions);
        }

        @Override
        public @NotNull
        ResourceLocation getId() {
            return id;
        }

        @Override
        public @NotNull
        RecipeSerializer<?> getType() {
            return ModRecipeSerializers.CENTRIFUGE_RECIPE.get();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }

    public record ItemOutputBuilder(double chance, List<ItemOutput> itemOutputs) {

        public ItemOutputBuilder(double chance) {
            this(chance, new ArrayList<>());
        }

        public ItemOutputBuilder addOutput(ItemOutput output) {
            itemOutputs.add(output);
            return this;
        }

        public ItemOutputBuilder addOutput(ItemStack stack, double weight) {
            return addOutput(new ItemOutput(stack, weight));
        }

        public CentrifugeRecipe.Output<ItemOutput, ItemStack> build() {
            return new CentrifugeRecipe.Output<>(chance, WeightedCollection.of(itemOutputs, ItemOutput::weight));
        }

    }

    public record FluidOutputBuilder(double chance, List<FluidOutput> fluidOutputs) {

        public FluidOutputBuilder(double chance) {
            this(chance, new ArrayList<>());
        }

        public FluidOutputBuilder addOutput(FluidOutput output) {
            fluidOutputs.add(output);
            return this;
        }

        public FluidOutputBuilder addOutput(FluidStack stack, double weight) {
            return addOutput(new FluidOutput(stack, weight));
        }

        public CentrifugeRecipe.Output<FluidOutput, FluidStack> build() {
            return new CentrifugeRecipe.Output<>(chance, fluidOutputs.stream().collect(WeightedCollection.getCollector(FluidOutput::weight)));
        }

    }
}
