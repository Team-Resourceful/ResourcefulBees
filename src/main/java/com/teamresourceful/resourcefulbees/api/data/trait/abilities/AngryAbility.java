package com.teamresourceful.resourcefulbees.api.data.trait.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.trait.AbilityDisplay;
import com.teamresourceful.resourcefulbees.api.data.trait.TraitAbility;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record AngryAbility(
        Item displayItem,
        double targetRange,
        int angerDuration
) implements TraitAbility {

    public static final MapCodec<AngryAbility> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec()
                    .optionalFieldOf("displayItem", Items.IRON_AXE)
                    .forGetter(AngryAbility::displayItem),
            Codec.doubleRange(0.0D, 256.0D)
                    .optionalFieldOf("targetRange", 20.0D)
                    .forGetter(AngryAbility::targetRange),
            Codec.intRange(1, 72000)
                    .optionalFieldOf("angerDuration", 1000)
                    .forGetter(AngryAbility::angerDuration)
    ).apply(instance, AngryAbility::new));

    @Override
    public ItemStack displayedItem() {
        return new ItemStack(displayItem);
    }

    @Override
    public Component getTitle() {
        return AbilityDisplay.title(ModIdentifier.of("angry"));
    }

    @Override
    public Component getDescription() {
        return AbilityDisplay.description(ModIdentifier.of("angry"));
    }

    @Override
    public void run(ResourcefulBee bee) {
        if (!bee.hasEffect(Holder.direct(ModEffects.CALMING.get()))) {
            LivingEntity player = bee.level().getNearestPlayer(bee, targetRange);

            bee.setPersistentAngerTarget(
                    player != null ? EntityReference.of(player) : null
            );
            bee.setPersistentAngerEndTime(angerDuration);
        }
    }
}