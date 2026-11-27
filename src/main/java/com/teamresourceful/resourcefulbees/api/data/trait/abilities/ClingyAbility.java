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

public record ClingyAbility(
        Item displayItem,
        double followRange,
        double speedModifier,
        double stopDistance,
        double startDistance
) implements TraitAbility {

    public static final MapCodec<ClingyAbility> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec()
                    .optionalFieldOf("displayItem", Items.LEAD)
                    .forGetter(ClingyAbility::displayItem),
            Codec.doubleRange(1.0, 64.0)
                    .optionalFieldOf("followRange", 12.0)
                    .forGetter(ClingyAbility::followRange),
            Codec.doubleRange(0.1, 4.0)
                    .optionalFieldOf("speedModifier", 1.0)
                    .forGetter(ClingyAbility::speedModifier),
            Codec.doubleRange(0.5, 16.0)
                    .optionalFieldOf("stopDistance", 2.0)
                    .forGetter(ClingyAbility::stopDistance),
            Codec.doubleRange(0.5, 32.0)
                    .optionalFieldOf("startDistance", 3.0)
                    .forGetter(ClingyAbility::startDistance)
    ).apply(instance, ClingyAbility::new));

    public ClingyAbility {
        if (stopDistance >= startDistance) {
            throw new IllegalArgumentException(
                    "stopDistance must be less than startDistance"
            );
        }

        if (startDistance > followRange) {
            throw new IllegalArgumentException(
                    "startDistance must be less than or equal to followRange"
            );
        }
    }

    @Override
    public ItemStack displayedItem() {
        return new ItemStack(displayItem);
    }

    @Override
    public Component getTitle() {
        return AbilityDisplay.title(ModIdentifier.of("clingy"));
    }

    @Override
    public Component getDescription() {
        return AbilityDisplay.description(ModIdentifier.of("clingy"));
    }

    @Override
    public boolean canRun() {
        return false;
    }

    @Override
    public void run(ResourcefulBee bee) {
        // Movement is handled by ClingyFollowGoal.
    }
}