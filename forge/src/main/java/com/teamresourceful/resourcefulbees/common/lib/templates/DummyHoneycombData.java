package com.teamresourceful.resourcefulbees.common.lib.templates;

import com.teamresourceful.resourcefulbees.api.honeycombdata.OutputVariation;
import com.teamresourceful.resourcefulbees.common.lib.defaults.DefaultApiaryTiers;
import com.teamresourceful.resourcefulbees.common.lib.defaults.DefaultBeehiveTiers;
import net.minecraft.world.item.Items;

import java.util.Map;
import java.util.Optional;

public class DummyHoneycombData {

    public static final OutputVariation DUMMY_OUTPUT_VARIATION = new OutputVariation("template",
            Map.of(
                    DefaultBeehiveTiers.T1_NEST, Items.HONEYCOMB.getDefaultInstance(),
                    DefaultBeehiveTiers.T2_NEST, Items.HONEYCOMB.getDefaultInstance(),
                    DefaultBeehiveTiers.T3_NEST, Items.HONEYCOMB.getDefaultInstance(),
                    DefaultBeehiveTiers.T4_NEST, Items.HONEYCOMB.getDefaultInstance()),
            Map.of(
                    DefaultApiaryTiers.T1_APIARY, Items.HONEYCOMB_BLOCK.getDefaultInstance(),
                    DefaultApiaryTiers.T2_APIARY, Items.HONEYCOMB_BLOCK.getDefaultInstance(),
                    DefaultApiaryTiers.T3_APIARY, Items.HONEYCOMB_BLOCK.getDefaultInstance(),
                    DefaultApiaryTiers.T4_APIARY, Items.HONEYCOMB_BLOCK.getDefaultInstance()),
            Optional.of(Items.HONEYCOMB.getDefaultInstance()),
            Optional.of(Items.HONEYCOMB_BLOCK.getDefaultInstance()));
}
