package com.teamresourceful.resourcefulbees.common.registry.custom;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.honeycomb.OutputVariation;
import com.teamresourceful.resourcefulbees.common.block.HoneycombBlock;
import com.teamresourceful.resourcefulbees.common.config.ApiaryConfig;
import com.teamresourceful.resourcefulbees.common.data.beedata.TradeData;
import com.teamresourceful.resourcefulbees.common.item.HoneycombItem;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryOutputType;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.RegistryEntry;
import com.teamresourceful.resourcefullib.common.color.Color;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public final class HoneycombRegistry implements com.teamresourceful.resourcefulbees.api.registry.HoneycombRegistry {

    private static final HoneycombRegistry INSTANCE = new HoneycombRegistry();
    private static final Map<String, JsonObject> RAW_DATA = new HashMap<>();
    private static final Map<String, OutputVariation> VARIATION_DATA = new HashMap<>();
    private static final Codec<List<OutputVariation>> VARIATION_CODEC = OutputVariation.CODEC.listOf().fieldOf("variations").orElse(new ArrayList<>()).codec();
    private static boolean itemsRegistered;

    private HoneycombRegistry() {
        // Single instanced classes do not need to be able to be extended
    }

    public static boolean areItemsRegistered() {
        return itemsRegistered;
    }

    /**
     * Returns an instance of the {@link HoneycombRegistry} for accessing data from the registry.
     * The honeycomb registry is a central point for getting any honeycomb data pertinent to
     * <i>Resourceful Bees</i>. The registry contains a cache of {@link JsonObject}'s and
     * {@link OutputVariation} objects for all honeycombs registered to the mod in addition
     * to other items bees can output in hives and apiaries.
     *
     * @return Returns an instance of the {@link HoneycombRegistry} for accessing data from the registry.
     */
    public static HoneycombRegistry getRegistry() {
        return INSTANCE;
    }

    @Override
    public boolean containsHoneycomb(String name) {
        return VARIATION_DATA.containsKey(name);
    }

    @Override
    public @Nullable OutputVariation getHoneycomb(String identifier) {
        return VARIATION_DATA.get(identifier);
    }

    /**
     * Registers the supplied honeycomb and associated data, in the form
     * of a {@link JsonObject}, to the mod.
     *
     * @param name The name of the honeycomb being registered.
     * @param honeycombData The raw json data of the honeycomb being registered
     */
    public void cacheRawHoneycombData(String name, JsonObject honeycombData) {
        RAW_DATA.computeIfAbsent(name.toLowerCase(Locale.ENGLISH).replace(" ", "_"), s -> Objects.requireNonNull(honeycombData));
    }

    @Override
    public Set<OutputVariation> getSetOfHoneycombs() {
        return Set.copyOf(VARIATION_DATA.values());
    }

    @Override
    public Stream<OutputVariation> getStreamOfHoneycombs() {
        return getSetOfHoneycombs().stream();
    }

    @Override
    public Set<String> getHoneycombTypes() {
        return Set.copyOf(VARIATION_DATA.keySet());
    }

    //region Regeneration Methods

    public void regenerateVariationData() {
        RAW_DATA.forEach(HoneycombRegistry::parseVariationData);
    }

    private static void parseVariationData(String s, JsonObject jsonObject) {
        VARIATION_CODEC.parse(JsonOps.INSTANCE, jsonObject)
                .getOrThrow(false, s2 -> ResourcefulBees.LOGGER.error("Could not create output variation from {} json file!", s))
                .forEach(HoneycombRegistry::computeVariation);
    }

    private static void computeVariation(OutputVariation variation) {
        VARIATION_DATA.compute(variation.id(), (s1, outputVariation1) -> variation);
    }

    //endregion

    //region Honeycomb Registration

    public static void registerHoneycombItems() {
        RAW_DATA.forEach(HoneycombRegistry::parseRegistryData);
        itemsRegistered = true;
    }

    private static void parseRegistryData(String s, JsonObject jsonObject) {
        RegistryData.codec(s).optionalFieldOf("honeycomb").codec().parse(JsonOps.INSTANCE, jsonObject)
                .getOrThrow(false, s2 -> ResourcefulBees.LOGGER.warn("Could not create honeycomb registry item from {} json file.", s));
    }

    @Override
    public Map<String, OutputVariation> getData() {
        return Collections.unmodifiableMap(VARIATION_DATA);
    }

    private record RegistryData(
            String name,
            Color color,
            boolean edible,
            boolean block,
            boolean enchanted,
            BeekeeperTradeData tradeData
    ) {

        private static Codec<RegistryData> codec(String name) {
            return RecordCodecBuilder.create(instance -> instance.group(
                    RecordCodecBuilder.point(name),
                    Color.CODEC.fieldOf("color").orElse(Color.DEFAULT).forGetter(RegistryData::color),
                    Codec.BOOL.fieldOf("edible").orElse(true).forGetter(RegistryData::edible),
                    Codec.BOOL.fieldOf("block").orElse(true).forGetter(RegistryData::block),
                    Codec.BOOL.fieldOf("enchanted").orElse(false).forGetter(RegistryData::enchanted),
                    TradeData.CODEC.fieldOf("tradeData").orElse(TradeData.DEFAULT).forGetter(RegistryData::tradeData)
            ).apply(instance, RegistryData::new));
        }

        private RegistryData {
            if (block) {
                RegistryEntry<Block> customHoneycombBlock = ModBlocks.HONEYCOMB_BLOCKS.register(name + "_honeycomb_block", () -> new HoneycombBlock(color, BlockBehaviour.Properties.copy(Blocks.HONEYCOMB_BLOCK)));
                final RegistryEntry<Item> blockItem = ModItems.HONEYCOMB_BLOCK_ITEMS.register(name + "_honeycomb_block", () -> new BlockItem(customHoneycombBlock.get(), new Item.Properties()) {
                    @Override
                    public boolean isFoil(@NotNull ItemStack stack) {
                        return enchanted || stack.isEnchanted();
                    }
                });
                ModItems.HONEYCOMB_ITEMS.register(name + "_honeycomb", () -> new HoneycombItem(color, edible, blockItem, enchanted, tradeData));
            } else {
                ModItems.HONEYCOMB_ITEMS.register(name + "_honeycomb", () -> new HoneycombItem(color, edible, null, enchanted, tradeData));
            }
        }

    }

    //endregion

    private static final List<ApiaryOutputType> DEFAULT_APIARY_OUTPUT_TYPES = List.of(ApiaryConfig.tierOneApiaryOutput, ApiaryConfig.tierTwoApiaryOutput, ApiaryConfig.tierThreeApiaryOutput, ApiaryConfig.tierFourApiaryOutput);
    private static final boolean DEFAULT_OUTPUT_TYPE_INCLUDES_COMB = DEFAULT_APIARY_OUTPUT_TYPES.contains(ApiaryOutputType.COMB);
    private static final boolean DEFAULT_OUTPUT_TYPE_INCLUDES_BLOCK = DEFAULT_APIARY_OUTPUT_TYPES.contains(ApiaryOutputType.BLOCK);

    @Override
    public void validateDefaults(String id, Optional<ItemStack> defaultComb, Optional<ItemStack> defaultCombBlock) {
        if (DEFAULT_OUTPUT_TYPE_INCLUDES_COMB && defaultComb.isEmpty()) {
            throw new IllegalArgumentException(id + " : Default comb must be present when list is empty and config contains combs!!!");
        } else if (DEFAULT_OUTPUT_TYPE_INCLUDES_BLOCK && defaultCombBlock.isEmpty()) {
            throw new IllegalArgumentException(id + " : Default block must be present when list is empty and config contains blocks!!!");
        }
    }
}
