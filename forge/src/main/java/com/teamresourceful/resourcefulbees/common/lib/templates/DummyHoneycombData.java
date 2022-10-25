package com.teamresourceful.resourcefulbees.common.lib.templates;

import com.teamresourceful.resourcefulbees.api.honeycombdata.OutputVariation;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeehiveTier;
import net.minecraft.world.item.Items;

import java.util.Map;
import java.util.Optional;

public class DummyHoneycombData {

    public static final OutputVariation DUMMY_OUTPUT_VARIATION = new OutputVariation("template",
            Map.of(
                    BeehiveTier.T1_NEST, Items.HONEYCOMB.getDefaultInstance(),
                    BeehiveTier.T2_NEST, Items.HONEYCOMB.getDefaultInstance(),
                    BeehiveTier.T3_NEST, Items.HONEYCOMB.getDefaultInstance(),
                    BeehiveTier.T4_NEST, Items.HONEYCOMB.getDefaultInstance()),
            Map.of(
                    ApiaryTier.T1_APIARY, Items.HONEYCOMB_BLOCK.getDefaultInstance(),
                    ApiaryTier.T2_APIARY, Items.HONEYCOMB_BLOCK.getDefaultInstance(),
                    ApiaryTier.T3_APIARY, Items.HONEYCOMB_BLOCK.getDefaultInstance(),
                    ApiaryTier.T4_APIARY, Items.HONEYCOMB_BLOCK.getDefaultInstance()),
            Optional.of(Items.HONEYCOMB.getDefaultInstance()),
            Optional.of(Items.HONEYCOMB_BLOCK.getDefaultInstance()));
}
