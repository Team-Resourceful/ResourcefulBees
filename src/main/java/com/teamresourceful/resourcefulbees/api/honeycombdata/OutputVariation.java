package com.teamresourceful.resourcefulbees.api.honeycombdata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryOutputType;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeehiveTier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Consumer;

@Unmodifiable
public class OutputVariation {

    private static final List<ApiaryOutputType> DEFAULT_APIARY_OUTPUT_TYPES = Arrays.asList(CommonConfig.T1_APIARY_OUTPUT.get(), CommonConfig.T2_APIARY_OUTPUT.get(), CommonConfig.T3_APIARY_OUTPUT.get(), CommonConfig.T4_APIARY_OUTPUT.get());
    private static final String MISSING_ID = "Identifier is REQUIRED!";
    private static final String MISSING_APIARY_COMB = " : Default comb must be present when list is empty and config contains combs!!!";
    private static final String MISSING_APIARY_BLOCK = " : Default block must be present when list is empty and config contains blocks!!!";
    private static final boolean DEFAULT_OUTPUT_TYPE_INCLUDES_COMB = DEFAULT_APIARY_OUTPUT_TYPES.contains(ApiaryOutputType.COMB);
    private static final boolean DEFAULT_OUTPUT_TYPE_INCLUDES_BLOCK = DEFAULT_APIARY_OUTPUT_TYPES.contains(ApiaryOutputType.BLOCK);

    public static final Codec<OutputVariation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("identifier").orElseGet((Consumer<String>) s -> ResourcefulBees.LOGGER.error(MISSING_ID), null).forGetter(OutputVariation::getIdentifier),
            CodecUtils.BEEHIVE_VARIATIONS.fieldOf("hiveCombs").orElseGet(HashMap::new).forGetter(OutputVariation::getHiveCombs),
            CodecUtils.APIARY_VARIATIONS.fieldOf("apiaryCombs").orElseGet(HashMap::new).forGetter(OutputVariation::getApiaryCombs),
            CodecUtils.ITEM_STACK_CODEC.optionalFieldOf("defaultComb").forGetter(OutputVariation::getDefaultComb),
            CodecUtils.ITEM_STACK_CODEC.optionalFieldOf("defaultCombBlock").forGetter(OutputVariation::getDefaultCombBlock)
    ).apply(instance, OutputVariation::new));

    private final String identifier;
    private final EnumMap<BeehiveTier, ItemStack> hiveCombs;
    private final EnumMap<ApiaryTier, ItemStack> apiaryCombs;
    private final Optional<ItemStack> defaultComb;
    private final Optional<ItemStack> defaultCombBlock;

    private OutputVariation(String identifier, Map<BeehiveTier, ItemStack> hiveCombs, Map<ApiaryTier, ItemStack> apiaryCombs, Optional<ItemStack> defaultComb, Optional<ItemStack> defaultCombBlock) {
        this.identifier = identifier;
        this.hiveCombs = new EnumMap<>(BeehiveTier.class);
        this.hiveCombs.putAll(hiveCombs);
        this.apiaryCombs = new EnumMap<>(ApiaryTier.class);
        this.apiaryCombs.putAll(apiaryCombs);
        this.defaultComb = defaultComb;
        this.defaultComb.ifPresent(comb -> comb.setCount(1));
        this.defaultCombBlock = defaultCombBlock;
        this.defaultCombBlock.ifPresent(block -> block.setCount(1));
        fixHiveCombs();
        fixApiaryCombs();
    }

    public String getIdentifier() {
        return identifier;
    }

    private EnumMap<BeehiveTier, ItemStack> getHiveCombs() {
        return hiveCombs;
    }

    private EnumMap<ApiaryTier, ItemStack> getApiaryCombs() {
        return apiaryCombs;
    }

    private Optional<ItemStack> getDefaultComb() {
        return defaultComb;
    }

    private Optional<ItemStack> getDefaultCombBlock() {
        return defaultCombBlock;
    }

    public ItemStack getHiveOutput(BeehiveTier tier) {
        return getHiveCombs().get(tier).copy();
    }

    public ItemStack getApiaryOutput(ApiaryTier tier) {
        return getApiaryCombs().get(tier).copy();
    }

    /**
     * When the {@link #hiveCombs} list is less than 5 the list will be expanded to a size of 5 using the last entry in the list.
     * When the {@link #hiveCombs} list is empty, then a list will be created using the default comb. The {@link #defaultComb} <b>MUST</b>
     * be provided for the list to be created!
     * */
    private void fixHiveCombs() {
        ItemStack lastStack = defaultComb.orElse(null);
        if (lastStack == null && hiveCombs.isEmpty()) throw new IllegalArgumentException("HiveCombs list can't be empty without a default comb supplied!!");
        for (BeehiveTier tier : BeehiveTier.values()) {
            ItemStack comb = hiveCombs.get(tier);
            if (comb != null) { lastStack = comb; continue; }
            hiveCombs.put(tier, lastStack);
        }
    }

    /**
     * When the {@link #apiaryCombs} list is less than 4 the list will be expanded to a size of 4 using the last entry in the list.
     * When the {@link #apiaryCombs} list is empty then a new list will be created based on the values specified in the config.
     * <b>BOTH</b> a {@link #defaultComb} and a {@link #defaultCombBlock} <b>MUST</b> be supplied for the list when they are specified in the config!
     * */
    private void fixApiaryCombs() {
        ItemStack lastStack = null;
        if (apiaryCombs.isEmpty()) checkDefaultsAreOK();
        for (ApiaryTier tier : ApiaryTier.values()) {
            ItemStack comb = apiaryCombs.get(tier);
            if (comb != null) { lastStack = comb; continue; }
            if (lastStack == null) {
                lastStack = tier.getOutputType().isComb() ? defaultComb.get() : defaultCombBlock.get();
                lastStack.setCount(tier.getOutputAmount());
            }
            apiaryCombs.put(tier, lastStack);
        }
    }

    private void checkDefaultsAreOK() {
        if (DEFAULT_OUTPUT_TYPE_INCLUDES_COMB && defaultComb.isEmpty()) {
            throw new IllegalArgumentException(identifier + MISSING_APIARY_COMB);
        } else if (DEFAULT_OUTPUT_TYPE_INCLUDES_BLOCK && defaultCombBlock.isEmpty()) {
            throw new IllegalArgumentException(identifier + MISSING_APIARY_BLOCK);
        }
    }
}
