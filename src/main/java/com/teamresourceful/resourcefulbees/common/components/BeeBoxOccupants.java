package com.teamresourceful.resourcefulbees.common.components;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.ItemTranslations;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.jspecify.annotations.NonNull;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public record BeeBoxOccupants(
        List<BeeBoxOccupant> occupants
) implements TooltipProvider {

    public static final BeeBoxOccupants EMPTY = new BeeBoxOccupants(List.of());
    public static final Codec<BeeBoxOccupants> CODEC = BeeBoxOccupant.CODEC.listOf().xmap(BeeBoxOccupants::new, BeeBoxOccupants::occupants);
    public static final StreamCodec<RegistryFriendlyByteBuf, BeeBoxOccupants> STREAM_CODEC = BeeBoxOccupant.STREAM_CODEC.apply(ByteBufCodecs.list()).map(BeeBoxOccupants::new, BeeBoxOccupants::occupants);

    public BeeBoxOccupants {
        occupants = List.copyOf(occupants);
    }

    public boolean isEmpty() {
        return this.occupants.isEmpty();
    }

    public int size() {
        return this.occupants.size();
    }

    public BeeBoxOccupants add(BeeBoxOccupant occupant) {
        List<BeeBoxOccupant> occupants = new ArrayList<>(this.occupants);
        occupants.add(occupant);
        return new BeeBoxOccupants(occupants);
    }

    public boolean isFull() {
        return this.occupants.size() >= BeeConstants.MAX_BEES_BEE_BOX;
    }

    @Override
    public void addToTooltip(Item.@NonNull TooltipContext tooltipContext, Consumer<Component> consumer, @NonNull TooltipFlag tooltipFlag, @NonNull DataComponentGetter dataComponentGetter) {
        consumer.accept(ItemTranslations.BEES.withStyle(ChatFormatting.YELLOW));

        if (this.occupants.isEmpty()) {
            consumer.accept(ItemTranslations.NO_BEES.withStyle(ChatFormatting.GOLD));
            return;
        }

        for (BeeBoxOccupant occupant : this.occupants) {
            consumer.accept(Component.translatable(ItemTranslations.BEE_BOX_ENTITY_NAME, occupant.entityData().type().getDescription()).withStyle(ChatFormatting.GRAY));
        }
    }
}