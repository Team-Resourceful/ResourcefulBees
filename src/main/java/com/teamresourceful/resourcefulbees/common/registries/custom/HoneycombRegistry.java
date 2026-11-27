package com.teamresourceful.resourcefulbees.common.registries.custom;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.api.data.honeycomb.HoneycombDefinition;
import com.teamresourceful.resourcefulbees.api.data.honeycomb.OutputVariation;
import com.teamresourceful.resourcefulbees.api.registry.HoneycombRegistration;
import com.teamresourceful.resourcefulbees.common.blocks.HoneycombBlock;
import com.teamresourceful.resourcefulbees.common.config.ApiaryConfig;
import com.teamresourceful.resourcefulbees.common.config.HoneycombConfig;
import com.teamresourceful.resourcefulbees.common.items.honey.CustomHoneycombItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryOutputType;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class HoneycombRegistry implements com.teamresourceful.resourcefulbees.api.registry.HoneycombRegistry {

    private static final HoneycombRegistry INSTANCE = new HoneycombRegistry();
    private static final Map<Identifier, JsonObject> RAW_DATA = new LinkedHashMap<>();
    private static final List<Supplier<OutputVariation>> JAVA_VARIATIONS = new ArrayList<>();
    private static final Map<Identifier, OutputVariation> VARIATION_DATA = new LinkedHashMap<>();
    private static final Codec<List<OutputVariation>> VARIATION_CODEC = OutputVariation.CODEC.listOf().fieldOf("variations").orElse(new ArrayList<>()).codec();
    private static boolean itemsRegistered;

    private HoneycombRegistry() {
        // Single instanced classes do not need to be able to be extended
    }

    public static boolean areItemsRegistered() {
        return itemsRegistered;
    }

    public static HoneycombRegistry getRegistry() {
        return INSTANCE;
    }

    @Override
    public boolean containsVariation(Identifier name) {
        return VARIATION_DATA.containsKey(name);
    }

    @Override
    public @Nullable OutputVariation getHoneycombVariation(Identifier identifier) {
        return VARIATION_DATA.get(identifier);
    }

    /**
     * Registers the supplied honeycomb and associated data, in the form
     * of a {@link JsonObject}, to the mod.
     *
     * @param name The name of the honeycomb being registered.
     * @param honeycombData The raw json data of the honeycomb being registered
     */
    public void cacheRawHoneycombData(Identifier name, JsonObject honeycombData) {
        RAW_DATA.computeIfAbsent(name, s -> Objects.requireNonNull(honeycombData));
    }

    @Override
    public Set<OutputVariation> getSetOfVariations() {
        return Set.copyOf(VARIATION_DATA.values());
    }

    @Override
    public Stream<OutputVariation> getStreamOfVariations() {
        return getSetOfVariations().stream();
    }

    @Override
    public Set<Identifier> getVariationIDs() {
        return Set.copyOf(VARIATION_DATA.keySet());
    }

    //region Regeneration Methods

    public void regenerateVariationData() {
        VARIATION_DATA.clear();
        RAW_DATA.forEach(HoneycombRegistry::parseVariationData);
        for (Supplier<OutputVariation> supplier : JAVA_VARIATIONS) {
            OutputVariation variation = supplier.get();
            OutputVariation previous = VARIATION_DATA.putIfAbsent(variation.id(), variation);
            if (previous != null) {
                throw new IllegalStateException(
                        "Duplicate honeycomb variation: " + variation.id()
                );
            }
        }
    }

    private static void parseVariationData(Identifier id, JsonObject jsonObject) {
        VARIATION_CODEC.parse(JsonOps.INSTANCE, jsonObject)
                .resultOrPartial(message -> ModConstants.LOGGER.error("Could not create output variation from {} json file: {}", id, message))
                .ifPresent(outputVariations -> outputVariations.forEach(HoneycombRegistry::computeVariation));
    }

    private static void computeVariation(OutputVariation variation) {
        VARIATION_DATA.put(variation.id(), variation);
    }

    //endregion

    //region Honeycomb Registration

    public static void registerHoneycombItems() {
        if (itemsRegistered) return;
        RAW_DATA.forEach(HoneycombRegistry::parseRegistryData);
        itemsRegistered = true;
    }

    public static void registerJavaHoneycombs() {
        for (HoneycombDefinition definition : HoneycombRegistration.drain()) {
            registerHoneycomb(definition);
            registerJavaHoneycombVariations(definition);
        }
    }

    public static void registerJavaHoneycombVariations(HoneycombDefinition definition) {
        JAVA_VARIATIONS.addAll(definition.variations());
    }

    private static void parseRegistryData(Identifier id, JsonObject jsonObject) {
        HoneycombDefinition.codec(id).optionalFieldOf("honeycomb").codec().parse(JsonOps.INSTANCE, jsonObject)
                .resultOrPartial(message -> ModConstants.LOGGER.warn("Could not create honeycomb registry item from {} json file: {}", id, message))
                .flatMap(optional -> optional)
                .ifPresent(HoneycombRegistry::registerHoneycomb);
    }

    @Override
    public Map<Identifier, OutputVariation> getVariationData() {
        return Collections.unmodifiableMap(VARIATION_DATA);
    }

    private static final String HONEYCOMB_BLOCK_SUFFIX = "_honeycomb_block";
    private static final String HONEYCOMB_SUFFIX = "_honeycomb";

    private static void registerHoneycomb(HoneycombDefinition definition) {
        Identifier name = definition.name();

        if (definition.block()) {
            var customHoneycombBlock = ModBlocks.HONEYCOMB_BLOCKS.register(
                    name.withSuffix(HONEYCOMB_BLOCK_SUFFIX),
                    properties -> new HoneycombBlock(
                            definition.color(),
                            properties
                    ),
                    honeycombBlockProperties(
                            name.withSuffix(HONEYCOMB_BLOCK_SUFFIX)
                    )
            );

            var blockItem = ModItems.HONEYCOMB_BLOCK_ITEMS.register(
                    name.withSuffix(HONEYCOMB_BLOCK_SUFFIX),
                    properties -> new BlockItem(
                            customHoneycombBlock.get(),
                            properties
                    ),
                    props(definition, ItemType.BLOCK)
            );

            ModItems.HONEYCOMB_ITEMS.register(
                    name.withSuffix(HONEYCOMB_SUFFIX),
                    properties -> new CustomHoneycombItem(
                            definition.color(),
                            blockItem::get,
                            definition.tradeData(),
                            properties
                    ),
                    props(definition, ItemType.COMB)
            );
        } else {
            ModItems.HONEYCOMB_ITEMS.register(
                    name.withSuffix(HONEYCOMB_SUFFIX),
                    properties -> new CustomHoneycombItem(
                            definition.color(),
                            null,
                            definition.tradeData(),
                            properties
                    ),
                    props(definition, ItemType.COMB)
            );
        }
    }


    private static Supplier<Item.Properties> props(HoneycombDefinition definition, ItemType type) {
        return () -> {
            var properties = new Item.Properties();

            properties.component(ModDataComponents.FALLBACK_ITEM_MODEL.get(), type.getDefaultModel());

            if (definition.enchanted()) {
                properties.component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
            }

            if (type == ItemType.COMB && definition.edible() && HoneycombConfig.honeycombsEdible) {
                properties.food(new FoodProperties.Builder()
                        .nutrition(HoneycombConfig.honeycombHunger)
                        .saturationModifier(HoneycombConfig.honeycombSaturation)
                        .alwaysEdible()
                        .build()
                );
            }

            return properties;
        };
    }

    private static Supplier<BlockBehaviour.Properties> honeycombBlockProperties(Identifier registryID) {
        return () -> BlockBehaviour.Properties.ofFullCopy(Blocks.HONEYCOMB_BLOCK).setId(ResourceKey.create(Registries.BLOCK, registryID));
    }

    //endregion

    private static final List<ApiaryOutputType> DEFAULT_APIARY_OUTPUT_TYPES = List.of(ApiaryConfig.tierOneApiaryOutput, ApiaryConfig.tierTwoApiaryOutput, ApiaryConfig.tierThreeApiaryOutput, ApiaryConfig.tierFourApiaryOutput);
    private static final boolean DEFAULT_OUTPUT_TYPE_INCLUDES_COMB = DEFAULT_APIARY_OUTPUT_TYPES.contains(ApiaryOutputType.COMB);
    private static final boolean DEFAULT_OUTPUT_TYPE_INCLUDES_BLOCK = DEFAULT_APIARY_OUTPUT_TYPES.contains(ApiaryOutputType.BLOCK);

    @Override
    public void validateDefaults(Identifier id, Optional<ItemStackTemplate> defaultComb, Optional<ItemStackTemplate> defaultCombBlock) {
        if (DEFAULT_OUTPUT_TYPE_INCLUDES_COMB && defaultComb.isEmpty()) {
            throw new IllegalArgumentException(id + " : Default comb must be present when list is empty and config contains combs!!!");
        } else if (DEFAULT_OUTPUT_TYPE_INCLUDES_BLOCK && defaultCombBlock.isEmpty()) {
            throw new IllegalArgumentException(id + " : Default block must be present when list is empty and config contains blocks!!!");
        }
    }

    private enum ItemType {
        COMB,
        BLOCK,
        ;

        public Identifier getDefaultModel() {
            return ModIdentifier.of(this == COMB ? "honeycomb" : "honeycomb_block");
        }
    }
}
