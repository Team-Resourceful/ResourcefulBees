package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.google.common.collect.ImmutableSet;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.registries.RegistryHelper;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.registry.HolderRegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.trading.TradeSet;


public final class ModVillagerProfessions {

    private ModVillagerProfessions() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final ResourcefulRegistry<VillagerProfession> PROFESSIONS = RegistryHelper.create(BuiltInRegistries.VILLAGER_PROFESSION, ModConstants.MOD_ID);

    private static final ResourceKey<TradeSet> BEEKEEPER_LEVEL_1 = beekeeperTrades(1);
    private static final ResourceKey<TradeSet> BEEKEEPER_LEVEL_2 = beekeeperTrades(2);
    private static final ResourceKey<TradeSet> BEEKEEPER_LEVEL_3 = beekeeperTrades(3);
    private static final ResourceKey<TradeSet> BEEKEEPER_LEVEL_4 = beekeeperTrades(4);
    private static final ResourceKey<TradeSet> BEEKEEPER_LEVEL_5 = beekeeperTrades(5);

    private static final Int2ObjectMap<ResourceKey<TradeSet>> TRADE_SETS =
            Int2ObjectMap.ofEntries(
                Int2ObjectMap.entry(1, BEEKEEPER_LEVEL_1),
                Int2ObjectMap.entry(2, BEEKEEPER_LEVEL_2),
                Int2ObjectMap.entry(3, BEEKEEPER_LEVEL_3),
                Int2ObjectMap.entry(4, BEEKEEPER_LEVEL_4),
                Int2ObjectMap.entry(5, BEEKEEPER_LEVEL_5)
            );

    public static final HolderRegistryEntry<VillagerProfession> BEEKEEPER = PROFESSIONS.registerHolder("beekeeper", () -> new VillagerProfession(Component.translatable("entity.resourcefulbees.villager.beekeeper"), poi -> poi.is(ModPOIs.TIERED_BEEHIVE_POI_KEY), poi -> poi.is(ModPOIs.TIERED_BEEHIVE_POI_KEY), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.BOTTLE_FILL, TRADE_SETS));

    private static ResourceKey<TradeSet> beekeeperTrades(int level) {
        return ResourceKey.create(Registries.TRADE_SET, ModIdentifier.of("beekeeper/level_" + level));
    }
}