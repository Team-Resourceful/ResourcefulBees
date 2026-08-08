package com.teamresourceful.resourcefulbees.common.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import com.teamresourceful.resourcefullib.common.codecs.tags.HolderSetCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public record FlowHiveRecipe(HolderSet<EntityType<?>> bees, FluidStackTemplate fluid) implements Recipe<FlowHiveRecipe.Input> {

    public static final MapCodec<FlowHiveRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                HolderSetCodec.of(BuiltInRegistries.ENTITY_TYPE).fieldOf("bees").forGetter(FlowHiveRecipe::bees),
                FluidStackTemplate.CODEC.fieldOf("fluid").forGetter(FlowHiveRecipe::fluid)
        ).apply(instance, FlowHiveRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FlowHiveRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderSet(Registries.ENTITY_TYPE),
            FlowHiveRecipe::bees,

            FluidStackTemplate.STREAM_CODEC,
            FlowHiveRecipe::fluid,

            FlowHiveRecipe::new
    );

    public static Optional<RecipeHolder<FlowHiveRecipe>> findRecipe(RecipeManager manager, EntityType<?> bee, Level level) {
        return manager.getRecipeFor(ModRecipes.FLOW_HIVE_RECIPE_TYPE.get(), new Input(bee.builtInRegistryHolder()), level);
    }

    @Override
    public boolean matches(@NonNull Input input, @NonNull Level level) {
        return bees().contains(input.bee());
    }

    @Override
    public @NonNull ItemStack assemble(@NonNull Input input) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public @NonNull String group() {
        return "";
    }

    @Override
    public RecipeSerializer<? extends Recipe<Input>> getSerializer() {
        return ModRecipeSerializers.FLOW_HIVE_RECIPE.get();
    }

    @Override
    public RecipeType<? extends Recipe<Input>> getType() {
        return ModRecipes.FLOW_HIVE_RECIPE_TYPE.get();
    }

    @Override
    public @NonNull PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public @NonNull RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public record Input(Holder.Reference<EntityType<?>> bee) implements RecipeInput {

        @Override
        public @NonNull ItemStack getItem(int index) {
            return ItemStack.EMPTY;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}
