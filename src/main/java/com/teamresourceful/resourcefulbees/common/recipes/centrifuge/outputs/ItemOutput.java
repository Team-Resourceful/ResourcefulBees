package com.teamresourceful.resourcefulbees.common.recipes.centrifuge.outputs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import org.jetbrains.annotations.Unmodifiable;

@Unmodifiable
public record ItemOutput(ItemStackTemplate template, double weight) implements AbstractOutput<ItemStack> {
    public static final Codec<ItemOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStackTemplate.CODEC.fieldOf("item").forGetter(ItemOutput::template),
            Codec.doubleRange(1.0d, 1000d).fieldOf("weight").orElse(1.0d).forGetter(ItemOutput::weight)
    ).apply(instance, ItemOutput::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemOutput> STREAM_CODEC = StreamCodec.composite(
            ItemStackTemplate.STREAM_CODEC,
            ItemOutput::template,
            ByteBufCodecs.DOUBLE,
            ItemOutput::weight,
            ItemOutput::new
    );

    public ItemStack itemStack() {
        return template.create();
    }

    public ItemStack multiply(int factor) {
        ItemStack stack = template.create();
        stack.setCount(stack.getCount() * factor);
        return stack;
    }
}
