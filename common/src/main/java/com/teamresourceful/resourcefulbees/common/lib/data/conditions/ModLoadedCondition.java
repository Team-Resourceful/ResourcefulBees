package com.teamresourceful.resourcefulbees.common.lib.data.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.conditions.LoadCondition;
import com.teamresourceful.resourcefulbees.api.data.conditions.LoadConditionSerializer;
import com.teamresourceful.resourcefulbees.platform.common.util.ModUtils;
import net.minecraft.resources.ResourceLocation;

public record ModLoadedCondition(String modid) implements LoadCondition<ModLoadedCondition> {

    private static final ResourceLocation ID = new ResourceLocation("mod_loaded");
    private static final Codec<ModLoadedCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("modid").forGetter(ModLoadedCondition::modid)
    ).apply(instance, ModLoadedCondition::new));
    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public boolean canLoad() {
        return ModUtils.isModLoaded(modid);
    }

    @Override
    public LoadConditionSerializer<ModLoadedCondition> serializer() {
        return SERIALIZER;
    }

    private static final class Serializer implements LoadConditionSerializer<ModLoadedCondition> {

        @Override
        public ResourceLocation id() {
            return ID;
        }

        @Override
        public Codec<ModLoadedCondition> codec() {
            return CODEC;
        }
    }
}
