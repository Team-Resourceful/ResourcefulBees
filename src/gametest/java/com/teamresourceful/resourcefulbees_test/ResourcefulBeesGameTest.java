package com.teamresourceful.resourcefulbees_test;

import com.teamresourceful.resourcefulbees.api.data.honeycomb.HoneycombDefinition;
import com.teamresourceful.resourcefulbees.api.data.honeycomb.OutputVariation;
import com.teamresourceful.resourcefulbees.api.registry.HoneycombRegistration;
import com.teamresourceful.resourcefulbees.api.tiers.ApiaryTier;
import com.teamresourceful.resourcefulbees.api.tiers.BeehiveTier;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.setup.data.beedata.TradeData;
import com.teamresourceful.resourcefullib.common.color.Color;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mod("resourcefulbees_gametest")
public class ResourcefulBeesGameTest {

    public ResourcefulBeesGameTest(IEventBus modBus) {
        GameTestRegistration.register(modBus);

        HoneycombRegistration.register(
                new HoneycombDefinition(
                        ModIdentifier.of("gametest_java"),
                        Color.DEFAULT,
                        false,
                        true,
                        false,
                        TradeData.DEFAULT,
                        List.of(() -> {
                            Item comb = BuiltInRegistries.ITEM.getValue(ModIdentifier.of("gametest_java_honeycomb"));
                            Item block = BuiltInRegistries.ITEM.getValue(ModIdentifier.of("gametest_java_honeycomb_block"));

                            int count = 1;
                            var defaultComb = new ItemStackTemplate(comb);
                            var defaultBlock = new ItemStackTemplate(block);

                            Map<BeehiveTier, ItemStackTemplate> hiveCombs = new HashMap<>();

                            for (BeehiveTier tier : BeehiveTier.values()) {
                                hiveCombs.put(tier, defaultComb.withCount(count++));
                            }

                            Map<ApiaryTier, ItemStackTemplate> apiaryCombs = new HashMap<>();
                            count = 1;
                            for (ApiaryTier tier : ApiaryTier.values()) {
                                apiaryCombs.put(tier, tier.outputType().get().isComb()
                                        ? defaultComb.withCount(count++)
                                        : defaultBlock.withCount(count++));
                            }

                            return OutputVariation.of(
                                    ModIdentifier.of("gametest_java"),
                                    hiveCombs,
                                    apiaryCombs,
                                    Optional.of(defaultComb),
                                    Optional.of(defaultBlock)
                            );
                        })
                )
        );
    }
}