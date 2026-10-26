package com.teamresourceful.resourcefulbees.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.lib.util.bytecodecs.StreamCodecExtras;
import com.teamresourceful.resourcefulbees.common.recipes.MutationRecipe;
import com.teamresourceful.resourcefullib.common.color.Color;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public record PollenRecipe(ResourceKey<Recipe<?>> recipe, Color baseColor, Color topColor) implements TooltipProvider {

    public static final Codec<PollenRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(Registries.RECIPE).fieldOf("recipe").forGetter(PollenRecipe::recipe),
            Color.CODEC.fieldOf("baseColor").forGetter(PollenRecipe::baseColor),
            Color.CODEC.fieldOf("topColor").forGetter(PollenRecipe::topColor)
    ).apply(instance, PollenRecipe::new));

    public static final StreamCodec<ByteBuf, PollenRecipe> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.RECIPE),
            PollenRecipe::recipe,

            StreamCodecExtras.COLOR_STREAM_CODEC,
            PollenRecipe::baseColor,

            StreamCodecExtras.COLOR_STREAM_CODEC,
            PollenRecipe::topColor,

            PollenRecipe::new
    );

    public static PollenRecipe of(RecipeHolder<MutationRecipe> recipe) {
        return new PollenRecipe(recipe.id(), recipe.value().pollenBaseColor(), recipe.value().pollenTopColor());
    }

    @Override
    public void addToTooltip(Item.@NonNull TooltipContext tooltipContext, @NonNull Consumer<Component> consumer, @NonNull TooltipFlag tooltipFlag, @NonNull DataComponentGetter dataComponentGetter) {
        consumer.accept(Component.translatable(Util.makeDescriptionId("mutation", recipe.identifier())).withStyle(ChatFormatting.DARK_PURPLE));
    }
}
