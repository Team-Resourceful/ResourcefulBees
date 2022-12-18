package com.teamresourceful.resourcefulbees.common.lib.defaults;

import com.teamresourceful.resourcefulbees.api.tiers.BeehiveTier;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.RegistryEntry;
import net.minecraft.resources.ResourceLocation;

public final class DefaultBeehiveTiers {

    private DefaultBeehiveTiers() {
        throw new UtilityClassError();
    }

    public static final BeehiveTier T1_NEST = new BeehiveTier.Builder()
            .maxBees(2)
            .maxCombs(4)
            .timeModifier(2.0)
            .displayItems(() -> ModItems.T1_NEST_ITEMS.getEntries().stream().map(RegistryEntry::get).toList())
            .build(new ResourceLocation(ModConstants.MOD_ID, "t1"));

    public static final BeehiveTier T2_NEST = new BeehiveTier.Builder()
            .maxBees(4)
            .maxCombs(8)
            .timeModifier(1.6)
            .displayItems(() -> ModItems.T2_NEST_ITEMS.getEntries().stream().map(RegistryEntry::get).toList())
            .build(new ResourceLocation(ModConstants.MOD_ID, "t2"));

    public static final BeehiveTier T3_NEST = new BeehiveTier.Builder()
            .maxBees(6)
            .maxCombs(16)
            .timeModifier(1.3)
            .displayItems(() -> ModItems.T3_NEST_ITEMS.getEntries().stream().map(RegistryEntry::get).toList())
            .build(new ResourceLocation(ModConstants.MOD_ID, "t3"));

    public static final BeehiveTier T4_NEST = new BeehiveTier.Builder()
            .maxBees(8)
            .maxCombs(32)
            .timeModifier(1.0)
            .displayItems(() -> ModItems.T4_NEST_ITEMS.getEntries().stream().map(RegistryEntry::get).toList())
            .build(new ResourceLocation(ModConstants.MOD_ID, "t4"));

    public static void loadDefaults() {
        // NO-OP
    }
}
