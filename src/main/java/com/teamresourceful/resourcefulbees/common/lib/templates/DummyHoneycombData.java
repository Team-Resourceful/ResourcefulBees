package com.teamresourceful.resourcefulbees.common.lib.templates;

import com.teamresourceful.resourcefulbees.api.data.honeycomb.OutputVariation;
import com.teamresourceful.resourcefulbees.api.tiers.ApiaryTier;
import com.teamresourceful.resourcefulbees.api.tiers.BeehiveTier;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;

import java.util.Map;
import java.util.Optional;

public final class DummyHoneycombData {

    private DummyHoneycombData() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final OutputVariation DUMMY_OUTPUT_VARIATION = new OutputVariation("template",
        Map.of(
            BeehiveTier.getOrThrow(ModIdentifier.of("t1")), ItemStackTemplate.fromNonEmptyStack(Items.HONEYCOMB.getDefaultInstance()),
            BeehiveTier.getOrThrow(ModIdentifier.of("t2")), ItemStackTemplate.fromNonEmptyStack(Items.HONEYCOMB.getDefaultInstance()),
            BeehiveTier.getOrThrow(ModIdentifier.of("t3")), ItemStackTemplate.fromNonEmptyStack(Items.HONEYCOMB.getDefaultInstance()),
            BeehiveTier.getOrThrow(ModIdentifier.of("t4")), ItemStackTemplate.fromNonEmptyStack(Items.HONEYCOMB.getDefaultInstance())
        ),
        Map.of(
            ApiaryTier.getOrThrow(ModIdentifier.of("t1")), ItemStackTemplate.fromNonEmptyStack(Items.HONEYCOMB_BLOCK.getDefaultInstance()),
            ApiaryTier.getOrThrow(ModIdentifier.of("t2")), ItemStackTemplate.fromNonEmptyStack(Items.HONEYCOMB_BLOCK.getDefaultInstance()),
            ApiaryTier.getOrThrow(ModIdentifier.of("t3")), ItemStackTemplate.fromNonEmptyStack(Items.HONEYCOMB_BLOCK.getDefaultInstance()),
            ApiaryTier.getOrThrow(ModIdentifier.of("t4")), ItemStackTemplate.fromNonEmptyStack(Items.HONEYCOMB_BLOCK.getDefaultInstance())
        ),
        Optional.of(ItemStackTemplate.fromNonEmptyStack(Items.HONEYCOMB.getDefaultInstance())),
        Optional.of(ItemStackTemplate.fromNonEmptyStack(Items.HONEYCOMB_BLOCK.getDefaultInstance())));
}
