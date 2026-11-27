package com.teamresourceful.resourcefulbees.api.data.trait.abilities;

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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public record SpiderAbility(Item displayItem) implements TraitAbility {

    public static final MapCodec<SpiderAbility> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec()
                    .optionalFieldOf("displayItem", Items.COBWEB)
                    .forGetter(SpiderAbility::displayItem)
    ).apply(instance, SpiderAbility::new));

    @Override
    public ItemStack displayedItem() {
        return new ItemStack(displayItem);
    }

    @Override
    public Component getTitle() {
        return AbilityDisplay.title(ModIdentifier.of("spider"));
    }

    @Override
    public Component getDescription() {
        return AbilityDisplay.description(ModIdentifier.of("spider"));
    }

    @Override
    public boolean canRun() {
        return false;
    }

    @Override
    public void run(ResourcefulBee bee) {
        // Marker ability; handled elsewhere.
    }

    @Override
    public boolean ignoresBlockSlowdown(ResourcefulBee bee, BlockState state) {
        return state.is(Blocks.COBWEB);
    }
}