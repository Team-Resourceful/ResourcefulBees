package com.teamresourceful.resourcefulbees.common.setup.data.beedata.mutation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeDataSerializer;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.BeeMutationData;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
import com.teamresourceful.resourcefulbees.common.recipes.MutationRecipe;
import com.teamresourceful.resourcefulbees.common.util.ModResourceLocation;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.Level;

import java.util.Map;

public record MutationData(int count, ResourceLocation id) implements BeeMutationData {

    private static final BeeMutationData DEFAULT = new MutationData(0, new ModResourceLocation("empty"));
    private static final Codec<BeeMutationData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 10).forGetter(BeeMutationData::count),
            ResourceLocation.CODEC.fieldOf("mutation").forGetter(BeeMutationData::id)
    ).apply(instance, MutationData::new));

    public static final BeeDataSerializer<BeeMutationData> SERIALIZER = BeeDataSerializer.of(new ModResourceLocation("mutation"), 1, id -> CODEC, DEFAULT);

    @Override
    public BeeDataSerializer<BeeMutationData> serializer() {
        return SERIALIZER;
    }

    @Override
    public Map<MutationType, WeightedCollection<MutationType>> mutations(Level level) {
        MutationRecipe recipe = MutationRecipe.getRecipe(level, id);
        return recipe != null ? recipe.mutations() : Map.of();
    }

    @Override
    public boolean hasMutation(Level level) {
        return !mutations(level).isEmpty();
    }
}
