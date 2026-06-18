package com.teamresourceful.resourcefulbees.common.setup.data.beedata.mutation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeDataSerializer;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.BeeMutationData;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.recipes.MutationRecipe;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.Level;

import java.util.Map;

public record MutationData(int count, Identifier id) implements BeeMutationData {

    private static final BeeMutationData DEFAULT = new MutationData(0, ModIdentifier.of("empty"));
    private static final Codec<BeeMutationData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 10).forGetter(BeeMutationData::count),
            Identifier.CODEC.fieldOf("mutation").forGetter(BeeMutationData::id)
    ).apply(instance, MutationData::new));

    public static final BeeDataSerializer<BeeMutationData> SERIALIZER = BeeDataSerializer.of(ModIdentifier.of("mutation"), 1, id -> CODEC, DEFAULT);

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
