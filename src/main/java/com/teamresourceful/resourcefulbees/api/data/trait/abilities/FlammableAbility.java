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

public record FlammableAbility(
        Item displayItem,
        int cooldown,
        int duration
) implements TraitAbility {

    public static final MapCodec<FlammableAbility> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec()
                    .optionalFieldOf("displayItem", Items.BLAZE_POWDER)
                    .forGetter(FlammableAbility::displayItem),
            Codec.intRange(1, 72000)
                    .optionalFieldOf("cooldown", 150)
                    .forGetter(FlammableAbility::cooldown),
            Codec.intRange(1, 3600)
                    .optionalFieldOf("duration", 3)
                    .forGetter(FlammableAbility::duration)
    ).apply(instance, FlammableAbility::new));

    @Override
    public ItemStack displayedItem() {
        return new ItemStack(displayItem);
    }

    @Override
    public Component getTitle() {
        return AbilityDisplay.title(ModIdentifier.of("flammable"));
    }

    @Override
    public Component getDescription() {
        return AbilityDisplay.description(ModIdentifier.of("flammable"));
    }

    @Override
    public void run(ResourcefulBee bee) {
        if (bee.tickCount % cooldown == 0) {
            bee.igniteForSeconds((float) duration);
        }
    }
}