package com.teamresourceful.resourcefulbees.common.recipes.breeder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.Optional;


public record ChildOutput(
        ItemStackTemplate child,
        Optional<String> displayEntity,
        double weight,
        double chance
) {
    public static final Codec<ChildOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStackTemplate.CODEC.fieldOf("child").forGetter(ChildOutput::child),
            Codec.STRING.optionalFieldOf("entity").forGetter(ChildOutput::displayEntity),
            Codec.doubleRange(0.0d, Double.MAX_VALUE).fieldOf("weight").orElse(BeeConstants.DEFAULT_BREED_WEIGHT).forGetter(ChildOutput::weight),
            Codec.doubleRange(0.0d, 1.0d).fieldOf("chance").orElse(BeeConstants.DEFAULT_BREED_CHANCE).forGetter(ChildOutput::chance)
    ).apply(instance, ChildOutput::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChildOutput> STREAM_CODEC = StreamCodec.composite(
            ItemStackTemplate.STREAM_CODEC,
            ChildOutput::child,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8),
            ChildOutput::displayEntity,
            ByteBufCodecs.DOUBLE,
            ChildOutput::weight,
            ByteBufCodecs.DOUBLE,
            ChildOutput::chance,
            ChildOutput::new
    );
}
