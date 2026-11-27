package com.teamresourceful.resourcefulbees.api.data.trait.abilities;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.trait.AbilityDisplay;
import com.teamresourceful.resourcefulbees.api.data.trait.TraitAbility;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record SculkAbility(Item displayItem) implements TraitAbility {

    public static final MapCodec<SculkAbility> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec()
                    .optionalFieldOf("displayItem", Items.SCULK_CATALYST)
                    .forGetter(SculkAbility::displayItem)
    ).apply(instance, SculkAbility::new));

    @Override
    public ItemStack displayedItem() {
        return new ItemStack(displayItem);
    }

    @Override
    public Component getTitle() {
        return AbilityDisplay.title(ModIdentifier.of("sculk"));
    }

    @Override
    public Component getDescription() {
        return AbilityDisplay.description(ModIdentifier.of("sculk"));
    }

    @Override
    public void run(ResourcefulBee bee) {
        bee.tickSculkAbility();
    }

    @Override
    public void onPollinationComplete(ResourcefulBee bee, BlockPos flowerPos) {
        bee.addSculkCharge(
                flowerPos,
                bee.getRandom().nextInt(1, bee.getTraitData().sculkCharge())
        );
    }
}