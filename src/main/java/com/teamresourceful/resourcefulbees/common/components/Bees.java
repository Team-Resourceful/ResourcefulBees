package com.teamresourceful.resourcefulbees.common.components;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefulbees.common.blockentities.base.BlockBee;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.List;
import java.util.function.Consumer;

public record Bees(List<BlockBee.Occupant> bees) implements TooltipProvider {
    public static final Codec<Bees> CODEC = BlockBee.Occupant.LIST_CODEC.xmap(Bees::new, Bees::bees);
    public static final StreamCodec<RegistryFriendlyByteBuf, Bees> STREAM_CODEC = BlockBee.Occupant.STREAM_CODEC
            .apply(ByteBufCodecs.list())
            .map(Bees::new, Bees::bees);
    public static final Bees EMPTY = new Bees(List.of());

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        consumer.accept(Component.translatable("container.beehive.bees", this.bees.size(), 3).withStyle(ChatFormatting.GRAY));
    }
}
