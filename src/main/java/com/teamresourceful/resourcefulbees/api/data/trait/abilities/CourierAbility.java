package com.teamresourceful.resourcefulbees.api.data.trait.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.trait.AbilityDisplay;
import com.teamresourceful.resourcefulbees.api.data.trait.TraitAbility;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record CourierAbility(
        Item displayItem,
        int itemTransferLimit,
        int fluidTransferLimit,
        int energyTransferLimit
) implements TraitAbility {

    public static final MapCodec<CourierAbility> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec()
                    .optionalFieldOf("displayItem", Items.CHEST)
                    .forGetter(CourierAbility::displayItem),

            Codec.intRange(1, 64)
                    .optionalFieldOf("itemTransferLimit", 16)
                    .forGetter(CourierAbility::itemTransferLimit),

            Codec.intRange(1, 1_000_000)
                    .optionalFieldOf("fluidTransferLimit", 1000)
                    .forGetter(CourierAbility::fluidTransferLimit),

            Codec.intRange(1, 1_000_000)
                    .optionalFieldOf("energyTransferLimit", 1000)
                    .forGetter(CourierAbility::energyTransferLimit)

    ).apply(instance, CourierAbility::new));

    @Override
    public ItemStack displayedItem() {
        return new ItemStack(displayItem);
    }

    @Override
    public Component getTitle() {
        return AbilityDisplay.title(ModIdentifier.of("courier"));
    }

    @Override
    public Component getDescription() {
        return AbilityDisplay.description(ModIdentifier.of("courier"));
    }

    @Override
    public boolean canRun() {
        return false;
    }

    @Override
    public void run(ResourcefulBee bee) {
        // Physical courier behavior is handled by CourierDeliveryGoal.
    }
}