package com.teamresourceful.resourcefulbees.common.recipe.base;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class CodecRecipeSerializer<R extends Recipe<?>> implements RecipeSerializer<R>{

    private final RecipeType<R> recipeType;
    private final Function<ResourceLocation, Codec<R>> codecInitializer;

    public CodecRecipeSerializer(RecipeType<R> recipeType, Function<ResourceLocation, Codec<R>> codecInitializer) {
        this.recipeType = recipeType;
        this.codecInitializer = codecInitializer;
    }

    @Override
    public @NotNull R fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
        return codecInitializer.apply(id).parse(JsonOps.INSTANCE, json).getOrThrow(false, s -> ResourcefulBees.LOGGER.error("Could not parse {}", id));
    }

    @Nullable
    @Override
    public R fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buffer) {
        Optional<R> result = codecInitializer.apply(id).parse(JsonOps.COMPRESSED, ModConstants.GSON.fromJson(buffer.readUtf(), JsonArray.class)).result();
        return result.orElse(null);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull R recipe) {
        codecInitializer.apply(recipe.getId()).encodeStart(JsonOps.COMPRESSED, recipe).result().ifPresent(element -> buffer.writeUtf(element.toString()));
    }

    public RecipeType<R> type() {
        return recipeType;
    }
}
