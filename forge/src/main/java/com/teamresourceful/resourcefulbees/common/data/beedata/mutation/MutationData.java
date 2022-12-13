package com.teamresourceful.resourcefulbees.common.data.beedata.mutation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeDataSerializer;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.BeeMutationData;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
import com.teamresourceful.resourcefulbees.common.mixin.invokers.RecipeManagerInvoker;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.MutationRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeTypes;
import com.teamresourceful.resourcefulbees.common.util.ModResourceLocation;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Map;

public record MutationData(int count, ResourceLocation id) implements BeeMutationData {

    private static final BeeMutationData DEFAULT = new MutationData(0, new ModResourceLocation("empty"));
    private static final Codec<BeeMutationData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("count").orElse(10).forGetter(BeeMutationData::count),
            ResourceLocation.CODEC.fieldOf("mutation").forGetter(BeeMutationData::id)
    ).apply(instance, MutationData::new));

    public static final BeeDataSerializer<BeeMutationData> SERIALIZER = BeeDataSerializer.of(new ModResourceLocation("mutation"), 1, id -> CODEC, DEFAULT);

    @Override
    public BeeDataSerializer<BeeMutationData> serializer() {
        return SERIALIZER;
    }

    @Override
    public Map<MutationType, WeightedCollection<MutationType>> mutations(Level level) {
        MutationRecipe recipe = ((RecipeManagerInvoker) level.getRecipeManager()).callByType(ModRecipeTypes.MUTATION_RECIPE_TYPE.get()).get(id);
        return recipe != null ? recipe.mutations() : Map.of();
    }

    @Override
    public boolean hasMutation(Level level) {
        return !mutations(level).isEmpty();
    }
}
