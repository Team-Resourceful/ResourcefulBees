package com.teamresourceful.resourcefulbees.common.data.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.conditions.LoadCondition;
import com.teamresourceful.resourcefulbees.api.data.conditions.LoadConditionSerializer;
import com.teamresourceful.resourcefulbees.common.registries.custom.LoadConditionRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record AndCondition(List<LoadCondition<?>> conditions) implements LoadCondition<AndCondition> {

    private static final ResourceLocation ID = new ResourceLocation("and");
    private static final Codec<AndCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LoadConditionRegistry.CODEC.listOf().fieldOf("values").forGetter(AndCondition::conditions)
    ).apply(instance, AndCondition::new));
    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public boolean canLoad() {
        return conditions.stream().allMatch(LoadCondition::canLoad);
    }

    @Override
    public LoadConditionSerializer<AndCondition> serializer() {
        return SERIALIZER;
    }

    private static final class Serializer implements LoadConditionSerializer<AndCondition> {

        @Override
        public ResourceLocation id() {
            return ID;
        }

        @Override
        public Codec<AndCondition> codec() {
            return CODEC;
        }
    }
}
