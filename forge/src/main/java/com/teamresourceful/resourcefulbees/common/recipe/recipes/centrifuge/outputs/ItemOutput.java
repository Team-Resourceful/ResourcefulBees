package com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.recipes.ItemStackCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Unmodifiable;

@Unmodifiable
public record ItemOutput(ItemStack itemStack, double weight) implements AbstractOutput<ItemStack> {

    public static final Codec<ItemOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStackCodec.CODEC.fieldOf("item").orElse(ItemStack.EMPTY).forGetter(ItemOutput::itemStack),
            Codec.doubleRange(1.0d, 1000d).fieldOf("weight").orElse(1.0d).forGetter(ItemOutput::weight)
    ).apply(instance, ItemOutput::new));

    public static final ItemOutput EMPTY = new ItemOutput(ItemStack.EMPTY, 0);

    @Override
    public ItemStack itemStack() {
        return itemStack.copy();
    }

    public ItemStack multiply(int factor) {
        ItemStack stack = itemStack.copy();
        stack.setCount(stack.getCount() * factor);
        return stack;
    }
}
