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
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Consumer;

@NullMarked
public record HiveBees(List<BeehiveBlockEntity.Occupant> bees) implements TooltipProvider {
    public static final Codec<HiveBees> CODEC = BeehiveBlockEntity.Occupant.CODEC.listOf().xmap(HiveBees::new, HiveBees::bees);
    public static final StreamCodec<RegistryFriendlyByteBuf, HiveBees> STREAM_CODEC = BeehiveBlockEntity.Occupant.STREAM_CODEC
            .apply(ByteBufCodecs.list())
            .map(HiveBees::new, HiveBees::bees);
    public static final HiveBees EMPTY = new HiveBees(List.of());

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        consumer.accept(Component.literal("Stored: %s".formatted(bees.size())).withStyle(ChatFormatting.GRAY));
    }
}
