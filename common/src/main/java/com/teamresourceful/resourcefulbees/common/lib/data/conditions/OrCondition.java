package com.teamresourceful.resourcefulbees.common.lib.data.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.conditions.LoadCondition;
import com.teamresourceful.resourcefulbees.api.data.conditions.LoadConditionSerializer;
import com.teamresourceful.resourcefulbees.common.registries.custom.LoadConditionRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record OrCondition(List<LoadCondition<?>> conditions) implements LoadCondition<OrCondition> {

    private static final ResourceLocation ID = new ResourceLocation("or");
    private static final Codec<OrCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LoadConditionRegistry.CODEC.listOf().fieldOf("values").forGetter(OrCondition::conditions)
    ).apply(instance, OrCondition::new));
    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public boolean canLoad() {
        return conditions.stream().anyMatch(LoadCondition::canLoad);
    }

    @Override
    public LoadConditionSerializer<OrCondition> serializer() {
        return SERIALIZER;
    }

    private static final class Serializer implements LoadConditionSerializer<OrCondition> {

        @Override
        public ResourceLocation id() {
            return ID;
        }

        @Override
        public Codec<OrCondition> codec() {
            return CODEC;
        }
    }
}
