package com.teamresourceful.resourcefulbees.common.recipes.breeder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Optional;

public record ParentInput(
        Ingredient parent,
        Optional<Identifier> displayEntity,
        int feedAmount,
        Ingredient feedItems,
        Optional<ItemStackTemplate> returnItem
) {
    public static final Codec<ParentInput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC.fieldOf("parent").forGetter(ParentInput::parent),
            Identifier.CODEC.optionalFieldOf("displayEntity").forGetter(ParentInput::displayEntity),
            Codec.INT.fieldOf("feedAmount").orElse(1).forGetter(ParentInput::feedAmount),
            Ingredient.CODEC.fieldOf("feedItems").forGetter(ParentInput::feedItems),
            ItemStackTemplate.CODEC.optionalFieldOf("returnItem").forGetter(ParentInput::returnItem)
    ).apply(instance, ParentInput::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ParentInput> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            ParentInput::parent,

            ByteBufCodecs.optional(Identifier.STREAM_CODEC),
            ParentInput::displayEntity,

            ByteBufCodecs.VAR_INT,
            ParentInput::feedAmount,

            Ingredient.CONTENTS_STREAM_CODEC,
            ParentInput::feedItems,

            ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC),
            ParentInput::returnItem,

            ParentInput::new
    );

    public boolean matches(ItemStack input, ItemStack feedItem) {
        return parent().test(input) && feedItems.test(feedItem);
    }
}
