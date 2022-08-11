package com.teamresourceful.resourcefulbees.common.registry.custom;

import com.google.gson.JsonObject;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.honeycombdata.OutputVariation;
import com.teamresourceful.resourcefulbees.common.block.HoneycombBlock;
import com.teamresourceful.resourcefulbees.common.item.HoneycombItem;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ItemGroupResourcefulBees;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefullib.common.color.Color;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class HoneycombRegistry {

    private static final HoneycombRegistry INSTANCE = new HoneycombRegistry();
    private static final Map<String, JsonObject> RAW_DATA = new HashMap<>();
    private static final Map<String, OutputVariation> VARIATION_DATA = new HashMap<>();
    private static final Codec<List<OutputVariation>> VARIATION_CODEC = OutputVariation.CODEC.listOf().fieldOf("variations").orElse(new ArrayList<>()).codec();
    private static boolean itemsRegistered;

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

    /**
     * Returns an {@link OutputVariation} object for the given identifier.
     *
     * @param identifier The identifier of the variation requested.
     * @return Returns an {@link OutputVariation} object for the given identifier.
     */
    public static @Nullable OutputVariation getOutputVariation(String identifier) {
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

    /**
     * Returns an unmodifiable copy of the internal {@link OutputVariation} map.
     * This is useful for iterating over all variations without worry of changing registry data
     * as the objects contained in the map are immutable.
     *
     * @return Returns an unmodifiable copy of the internal {@link OutputVariation} map.
     */
    public Map<String, OutputVariation> getVariations() {
        return Collections.unmodifiableMap(VARIATION_DATA);
    }

    /**
     * A helper method that returns an unmodifiable set of the values contained in the internal
     * {@link OutputVariation} map. This is useful for iterating over all variations without
     * worry of changing registry data as the objects contained in the map are immutable.
     *
     * @return Returns an unmodifiable set of the values contained in the internal
     * {@link OutputVariation} map
     */
    public Set<OutputVariation> getSetOfVariations() {
        return Set.copyOf(VARIATION_DATA.values());
    }

    /**
     * A helper method that returns a stream using the {@link HoneycombRegistry#getSetOfVariations()} method.
     */
    public Stream<OutputVariation> getStreamOfVariations() {
        return getSetOfVariations().stream();
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

    private record RegistryData(String name, Color color, boolean edible, boolean block, boolean enchanted) {

        private static Codec<RegistryData> codec(String name) {
            return RecordCodecBuilder.create(instance -> instance.group(
                    MapCodec.of(Encoder.empty(), Decoder.unit(() -> name)).forGetter(RegistryData::name),
                    Color.CODEC.fieldOf("color").orElse(Color.DEFAULT).forGetter(RegistryData::color),
                    Codec.BOOL.fieldOf("edible").orElse(true).forGetter(RegistryData::edible),
                    Codec.BOOL.fieldOf("block").orElse(true).forGetter(RegistryData::block),
                    Codec.BOOL.fieldOf("enchanted").orElse(false).forGetter(RegistryData::enchanted)
            ).apply(instance, RegistryData::new));
        }

        private RegistryData {
            if (block) {
                RegistryObject<Block> customHoneycombBlock = ModBlocks.HONEYCOMB_BLOCKS.register(name + "_honeycomb_block", () -> new HoneycombBlock(color, BlockBehaviour.Properties.copy(Blocks.HONEYCOMB_BLOCK)));
                final RegistryObject<Item> blockItem = ModItems.HONEYCOMB_BLOCK_ITEMS.register(name + "_honeycomb_block", () -> new BlockItem(customHoneycombBlock.get(), new Item.Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES_COMBS)) {
                    @Override
                    public boolean isFoil(@NotNull ItemStack stack) {
                        return enchanted || stack.isEnchanted();
                    }
                });
                ModItems.HONEYCOMB_ITEMS.register(name + "_honeycomb", () -> new HoneycombItem(color, edible, blockItem, enchanted));
            } else {
                ModItems.HONEYCOMB_ITEMS.register(name + "_honeycomb", () -> new HoneycombItem(color, edible, null, enchanted));
            }
        }

    }

    //endregion
}
