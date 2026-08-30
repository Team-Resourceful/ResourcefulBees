package com.teamresourceful.resourcefulbees.datagen.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class BeekeeperTradeProvider implements DataProvider {

    private final PackOutput output;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    public BeekeeperTradeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.output = output;
        this.lookupProvider = lookupProvider;
    }

    @Override
    public @NonNull CompletableFuture<?> run(@NonNull CachedOutput cachedOutput) {
        return lookupProvider.thenCompose(provider -> {
            var registryOps = provider.createSerializationContext(JsonOps.INSTANCE);

            Path root = output.getOutputFolder()
                    .resolve("data")
                    .resolve(ModConstants.MOD_ID);

            Path tradePath = root
                    .resolve("villager_trade")
                    .resolve("beekeeper")
                    .resolve("4");

            Path tagPath = root
                    .resolve("tags")
                    .resolve("villager_trade")
                    .resolve("beekeeper")
                    .resolve("tiered_hives.json");

            JsonArray values = new JsonArray();
            CompletableFuture<?>[] writes = generateLevelFourTrades(cachedOutput, registryOps, tradePath, tagPath, values);

            return CompletableFuture.allOf(writes);
        });
    }

    private CompletableFuture<?>[] generateLevelFourTrades(CachedOutput cachedOutput, net.minecraft.resources.RegistryOps<JsonElement> registryOps, Path tradePath, Path tagPath, JsonArray values) {
        var futures = new java.util.ArrayList<CompletableFuture<?>>();

//        addNestTrade(
//                cachedOutput,
//                registryOps,
//                tradePath,
//                values,
//                "beehive",
//                Items.BEEHIVE,
//                futures
//        );
//
//        addNestTrade(
//                cachedOutput,
//                registryOps,
//                tradePath,
//                values,
//                "bee_nest",
//                Items.BEE_NEST,
//                futures
//        );

        for (RegistryEntry<Item> entry : ModItems.NEST_ITEMS.getEntries()) {
            addNestTrade(
                    cachedOutput,
                    registryOps,
                    tradePath,
                    values,
                    entry.getId().getPath(),
                    entry.getId(),
                    futures
            );
        }

        JsonObject tag = new JsonObject();
        tag.addProperty("replace", false);
        tag.add("values", values);

        futures.add(
                DataProvider.saveStable(
                        cachedOutput,
                        tag,
                        tagPath
                )
        );

        return futures.toArray(CompletableFuture[]::new);
    }

    private void addNestTrade(CachedOutput cachedOutput, RegistryOps<JsonElement> registryOps, Path tradePath, JsonArray values, String name, Identifier nest, List<CompletableFuture<?>> futures) {
        VillagerTrade trade = createNestTrade(nest);

        Identifier id = ModIdentifier.of("beekeeper/4/" + name);

        VillagerTrade.CODEC
                .encodeStart(registryOps, trade)
                .resultOrPartial(error -> {
                    throw new IllegalStateException("Failed to encode villager trade " + id + ": " + error);
                })
                .ifPresent(json -> {
                    futures.add(DataProvider.saveStable(cachedOutput, json, tradePath.resolve(name + ".json")));
                    values.add(id.toString());
                });
    }

    private VillagerTrade createNestTrade(Identifier nest) {
        TradeCost wants = new TradeCost(ModItems.GOLD_FLOWER_ITEM.get(), UniformGenerator.between(16.0F, 32.0F));
        TradeCost additionalWants = new TradeCost(Items.GRASS_BLOCK, UniformGenerator.between(8.0F, 24.0F));
        ItemStackTemplate gives = new ItemStackTemplate(BuiltInRegistries.ITEM.get(nest).orElseThrow(), 1, DataComponentPatch.EMPTY);

        return new VillagerTrade(
                wants,
                Optional.of(additionalWants),
                gives,
                4,
                3,
                0.1F,
                Optional.empty(),
                List.of()
        );
    }

    @Override
    public @NonNull String getName() {
        return "Resourceful Bees Beekeeper Trade Provider";
    }
}