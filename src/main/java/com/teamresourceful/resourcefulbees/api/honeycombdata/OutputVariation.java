package com.teamresourceful.resourcefulbees.api.honeycombdata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryOutputType;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeehiveTier;
import com.teamresourceful.resourcefullib.common.codecs.recipes.ItemStackCodec;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.Consumer;

public record OutputVariation(String id,
                              Map<BeehiveTier, ItemStack> hiveCombs,
                              Map<ApiaryTier, ItemStack> apiaryCombs,
                              Optional<ItemStack> defaultComb,
                              Optional<ItemStack> defaultCombBlock
) {

    private static final List<ApiaryOutputType> DEFAULT_APIARY_OUTPUT_TYPES = List.of(CommonConfig.T1_APIARY_OUTPUT.get(), CommonConfig.T2_APIARY_OUTPUT.get(), CommonConfig.T3_APIARY_OUTPUT.get(), CommonConfig.T4_APIARY_OUTPUT.get());
    private static final boolean DEFAULT_OUTPUT_TYPE_INCLUDES_COMB = DEFAULT_APIARY_OUTPUT_TYPES.contains(ApiaryOutputType.COMB);
    private static final boolean DEFAULT_OUTPUT_TYPE_INCLUDES_BLOCK = DEFAULT_APIARY_OUTPUT_TYPES.contains(ApiaryOutputType.BLOCK);

    public static final Codec<OutputVariation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("identifier").orElseGet((Consumer<String>) s -> ResourcefulBees.LOGGER.error("Identifier is REQUIRED!"), null).forGetter(OutputVariation::id),
            CodecUtils.BEEHIVE_VARIATIONS.fieldOf("hiveCombs").orElseGet(HashMap::new).forGetter(OutputVariation::hiveCombs),
            CodecUtils.APIARY_VARIATIONS.fieldOf("apiaryCombs").orElseGet(HashMap::new).forGetter(OutputVariation::apiaryCombs),
            ItemStackCodec.CODEC.optionalFieldOf("defaultComb").forGetter(OutputVariation::defaultComb),
            ItemStackCodec.CODEC.optionalFieldOf("defaultCombBlock").forGetter(OutputVariation::defaultCombBlock)
    ).apply(instance, OutputVariation::of));

    private static OutputVariation of(String identifier, Map<BeehiveTier, ItemStack> hiveCombs, Map<ApiaryTier, ItemStack> apiaryCombs, Optional<ItemStack> defaultComb, Optional<ItemStack> defaultCombBlock) {
        defaultComb.ifPresent(comb -> comb.setCount(1));
        defaultCombBlock.ifPresent(block -> block.setCount(1));
        hiveCombs = fixHiveCombs(new EnumMap<>(hiveCombs), defaultComb);
        apiaryCombs = fixApiaryCombs(identifier, new EnumMap<>(apiaryCombs), defaultComb, defaultCombBlock);
        return new OutputVariation(identifier, hiveCombs, apiaryCombs, defaultComb, defaultCombBlock);
    }

    public ItemStack getHiveOutput(BeehiveTier tier) {
        return hiveCombs().get(tier).copy();
    }

    public ItemStack getApiaryOutput(ApiaryTier tier) {
        return apiaryCombs().get(tier).copy();
    }

    /**
     * When the {@link #hiveCombs} list is less than 5 the list will be expanded to a size of 5 using the last entry in the list.
     * When the {@link #hiveCombs} list is empty, then a list will be created using the default comb. The {@link #defaultComb} <b>MUST</b>
     * be provided for the list to be created!
     * */
    private static Map<BeehiveTier, ItemStack> fixHiveCombs(Map<BeehiveTier, ItemStack> hiveCombs, Optional<ItemStack> defaultComb) {
        ItemStack lastStack = defaultComb.map(ItemStack::copy).orElse(null);
        if (lastStack == null && hiveCombs.isEmpty()) throw new IllegalArgumentException("HiveCombs list can't be empty without a default comb supplied!!");
        for (BeehiveTier tier : BeehiveTier.values()) {
            ItemStack comb = hiveCombs.get(tier);
            if (comb != null) { lastStack = comb; continue; }
            hiveCombs.put(tier, lastStack);
        }
        return Collections.unmodifiableMap(hiveCombs);
    }

    /**
     * When the {@link #apiaryCombs} list is less than 4 the list will be expanded to a size of 4 using the last entry in the list.
     * When the {@link #apiaryCombs} list is empty then a new list will be created based on the values specified in the config.
     * <b>BOTH</b> a {@link #defaultComb} and a {@link #defaultCombBlock} <b>MUST</b> be supplied for the list when they are specified in the config!
     * */
    private static Map<ApiaryTier, ItemStack> fixApiaryCombs(String id, Map<ApiaryTier, ItemStack> apiaryCombs, Optional<ItemStack> defaultComb, Optional<ItemStack> defaultCombBlock) {
        ItemStack lastStack = null;
        boolean wasEmpty = apiaryCombs.isEmpty();
        if (wasEmpty) checkDefaultsAreOK(id, defaultComb, defaultCombBlock);
        for (ApiaryTier tier : ApiaryTier.values()) {
            ItemStack comb = apiaryCombs.get(tier);
            if (comb != null) {
                lastStack = comb;
                continue;
            }
            if (lastStack == null || wasEmpty) {
                //We check if its was empty to make sure it with create the proper list based off the config value instead
                //of copying the first config value to all slots.
                //noinspection OptionalGetWithoutIsPresent
                lastStack = tier.getOutputType().isComb() ? defaultComb.get().copy() : defaultCombBlock.get().copy();
                lastStack.setCount(tier.getOutputAmount());
            }
            apiaryCombs.put(tier, lastStack);
        }
        return Collections.unmodifiableMap(apiaryCombs);
    }

    private static void checkDefaultsAreOK(String id, Optional<ItemStack> defaultComb, Optional<ItemStack> defaultCombBlock) {
        if (DEFAULT_OUTPUT_TYPE_INCLUDES_COMB && defaultComb.isEmpty()) {
            throw new IllegalArgumentException(id + " : Default comb must be present when list is empty and config contains combs!!!");
        } else if (DEFAULT_OUTPUT_TYPE_INCLUDES_BLOCK && defaultCombBlock.isEmpty()) {
            throw new IllegalArgumentException(id + " : Default block must be present when list is empty and config contains blocks!!!");
        }
    }
}
