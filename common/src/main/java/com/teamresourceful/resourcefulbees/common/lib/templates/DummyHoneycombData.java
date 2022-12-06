package com.teamresourceful.resourcefulbees.common.lib.templates;

import com.teamresourceful.resourcefulbees.api.data.honeycomb.OutputVariation;
import com.teamresourceful.resourcefulbees.api.tiers.ApiaryTier;
import com.teamresourceful.resourcefulbees.api.tiers.BeehiveTier;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.Map;
import java.util.Optional;

public class DummyHoneycombData {

    public static final OutputVariation DUMMY_OUTPUT_VARIATION = new OutputVariation("template",
        Map.of(
            BeehiveTier.getOrThrow(new ResourceLocation(BeeConstants.MOD_ID, "t1")), Items.HONEYCOMB.getDefaultInstance(),
            BeehiveTier.getOrThrow(new ResourceLocation(BeeConstants.MOD_ID, "t2")), Items.HONEYCOMB.getDefaultInstance(),
            BeehiveTier.getOrThrow(new ResourceLocation(BeeConstants.MOD_ID, "t3")), Items.HONEYCOMB.getDefaultInstance(),
            BeehiveTier.getOrThrow(new ResourceLocation(BeeConstants.MOD_ID, "t4")), Items.HONEYCOMB.getDefaultInstance()
        ),
        Map.of(
            ApiaryTier.getOrThrow(new ResourceLocation(BeeConstants.MOD_ID, "t1")), Items.HONEYCOMB_BLOCK.getDefaultInstance(),
            ApiaryTier.getOrThrow(new ResourceLocation(BeeConstants.MOD_ID, "t2")), Items.HONEYCOMB_BLOCK.getDefaultInstance(),
            ApiaryTier.getOrThrow(new ResourceLocation(BeeConstants.MOD_ID, "t3")), Items.HONEYCOMB_BLOCK.getDefaultInstance(),
            ApiaryTier.getOrThrow(new ResourceLocation(BeeConstants.MOD_ID, "t4")), Items.HONEYCOMB_BLOCK.getDefaultInstance()
        ),
        Optional.of(Items.HONEYCOMB.getDefaultInstance()),
        Optional.of(Items.HONEYCOMB_BLOCK.getDefaultInstance()));
}
