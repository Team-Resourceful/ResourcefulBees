package com.teamresourceful.resourcefulbees.api.beedata.outputs;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemOutput extends AbstractOutput{

    public static final Codec<ItemOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CodecUtils.ITEM_STACK_CODEC.fieldOf("item").orElse(ItemStack.EMPTY).forGetter(ItemOutput::getItemStack),
            Codec.doubleRange(1.0d, Double.MAX_VALUE).fieldOf("weight").orElse(1.0d).forGetter(ItemOutput::getWeight),
            Codec.doubleRange(0.0d, 1.0).fieldOf("chance").orElse(1.0d).forGetter(ItemOutput::getChance)
    ).apply(instance, ItemOutput::new));

    public static final ItemOutput EMPTY = new ItemOutput(ItemStack.EMPTY, 0, 0);

    private final ItemStack itemStack;

    public ItemOutput(ItemStack itemStack, double weight, double chance) {
        super(weight, chance);
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
                .add("Chance", getChance())
                .add("Weight", getWeight()).toString();
    }
}
