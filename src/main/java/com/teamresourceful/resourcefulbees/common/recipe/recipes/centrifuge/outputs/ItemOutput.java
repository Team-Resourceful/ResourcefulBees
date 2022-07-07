package com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.recipes.ItemStackCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Unmodifiable;

@Unmodifiable
public class ItemOutput extends AbstractOutput {

    public static final Codec<ItemOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStackCodec.CODEC.fieldOf("item").orElse(ItemStack.EMPTY).forGetter(ItemOutput::getItemStack),
            Codec.doubleRange(1.0d, 1000d).fieldOf("weight").orElse(1.0d).forGetter(ItemOutput::getWeight)
    ).apply(instance, ItemOutput::new));

    public static final ItemOutput EMPTY = new ItemOutput(ItemStack.EMPTY, 0);

    protected final ItemStack itemStack;

    public ItemOutput(ItemStack itemStack, double weight) {
        super(weight);
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack.copy();
    }

    public Item getItem() {
        return itemStack.getItem();
    }

    public int getCount() {
        return itemStack.getCount();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("Item", getItem())
                .add("Count", getCount())
                .add("Weight", getWeight()).toString();
    }
}
