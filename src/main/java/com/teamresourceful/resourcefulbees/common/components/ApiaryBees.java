package com.teamresourceful.resourcefulbees.common.components;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Consumer;

@NullMarked
public record ApiaryBees(List<ApiaryOccupant> bees) implements TooltipProvider {
    public static final Codec<ApiaryBees> CODEC = ApiaryOccupant.CODEC.listOf().xmap(ApiaryBees::new, ApiaryBees::bees);
    public static final StreamCodec<RegistryFriendlyByteBuf, ApiaryBees> STREAM_CODEC = ApiaryOccupant.STREAM_CODEC
            .apply(ByteBufCodecs.list())
            .map(ApiaryBees::new, ApiaryBees::bees);
    public static final ApiaryBees EMPTY = new ApiaryBees(List.of());

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        consumer.accept(Component.literal("Stored: %s".formatted(bees.size())).withStyle(ChatFormatting.GRAY));
    }
}
