package com.teamresourceful.resourcefulbees.common.lib.defaults;

import com.teamresourceful.resourcefulbees.api.tiers.BeehiveTier;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;

public final class DefaultBeehiveTiers {

    private DefaultBeehiveTiers() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final BeehiveTier T1_NEST = new BeehiveTier.Builder()
            .maxBees(2)
            .maxCombs(4)
            .timeModifier(2.0)
            .displayItems(() -> ModItems.T1_NEST_ITEMS.getEntries().stream().map(RegistryEntry::get).toList())
            .entityType(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY)
            .build(ModIdentifier.of("t1"));

    public static final BeehiveTier T2_NEST = new BeehiveTier.Builder()
            .maxBees(4)
            .maxCombs(8)
            .timeModifier(1.6)
            .displayItems(() -> ModItems.T2_NEST_ITEMS.getEntries().stream().map(RegistryEntry::get).toList())
            .entityType(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY)
            .build(ModIdentifier.of("t2"));

    public static final BeehiveTier T3_NEST = new BeehiveTier.Builder()
            .maxBees(6)
            .maxCombs(16)
            .timeModifier(1.3)
            .displayItems(() -> ModItems.T3_NEST_ITEMS.getEntries().stream().map(RegistryEntry::get).toList())
            .entityType(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY)
            .build(ModIdentifier.of("t3"));

    public static final BeehiveTier T4_NEST = new BeehiveTier.Builder()
            .maxBees(8)
            .maxCombs(32)
            .timeModifier(1.0)
            .displayItems(() -> ModItems.T4_NEST_ITEMS.getEntries().stream().map(RegistryEntry::get).toList())
            .entityType(ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY)
            .build(ModIdentifier.of("t4"));

    public static void loadDefaults() {
        // NO-OP
    }

    public static BeehiveTier ordinalOf(int index) {
        return switch (index) {
            case 4 -> T4_NEST;
            case 3 -> T3_NEST;
            case 2 -> T2_NEST;
            default -> T1_NEST;
        };
    }
}
