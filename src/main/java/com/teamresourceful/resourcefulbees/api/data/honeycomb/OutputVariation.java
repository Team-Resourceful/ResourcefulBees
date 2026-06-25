package com.teamresourceful.resourcefulbees.api.data.honeycomb;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.registry.HoneycombRegistry;
import com.teamresourceful.resourcefulbees.api.tiers.ApiaryTier;
import com.teamresourceful.resourcefulbees.api.tiers.BeehiveTier;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefullib.common.codecs.recipes.ItemStackCodec;
import com.teamresourceful.resourcefullib.common.lib.Constants;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public record OutputVariation(String id,
                              Map<BeehiveTier, ItemStackTemplate> hiveCombs,
                              Map<ApiaryTier, ItemStackTemplate> apiaryCombs,
                              Optional<ItemStackTemplate> defaultComb,
                              Optional<ItemStackTemplate> defaultCombBlock
) {

    public static final Codec<OutputVariation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("identifier").orElseGet((Consumer<String>) s -> Constants.LOGGER.error("Identifier is REQUIRED!"), null).forGetter(OutputVariation::id),
            Codec.unboundedMap(BeehiveTier.CODEC, ItemStackTemplate.CODEC).fieldOf("hiveCombs").orElseGet(HashMap::new).forGetter(OutputVariation::hiveCombs),
            Codec.unboundedMap(ApiaryTier.CODEC, ItemStackTemplate.CODEC).fieldOf("apiaryCombs").orElseGet(HashMap::new).forGetter(OutputVariation::apiaryCombs),
            ItemStackTemplate.CODEC.optionalFieldOf("defaultComb").forGetter(OutputVariation::defaultComb),
            ItemStackTemplate.CODEC.optionalFieldOf("defaultCombBlock").forGetter(OutputVariation::defaultCombBlock)
    ).apply(instance, OutputVariation::of));

    private static OutputVariation of(String identifier, Map<BeehiveTier, ItemStackTemplate> hiveCombs, Map<ApiaryTier, ItemStackTemplate> apiaryCombs, Optional<ItemStackTemplate> defaultComb, Optional<ItemStackTemplate> defaultCombBlock) {
        ModItems.HONEYCOMB_ITEMS.getEntries().forEach(itemRegistryEntry -> System.out.println(itemRegistryEntry.get()));
        defaultComb.ifPresent(comb -> comb.withCount(1));
        defaultCombBlock.ifPresent(block -> block.withCount(1));
        hiveCombs = fixHiveCombs(hiveCombs, defaultComb);
        apiaryCombs = fixApiaryCombs(identifier, apiaryCombs, defaultComb, defaultCombBlock);
        return new OutputVariation(identifier, hiveCombs, apiaryCombs, defaultComb, defaultCombBlock);
    }

    public ItemStack getHiveOutput(BeehiveTier tier) {
        return hiveCombs().get(tier).create();
    }

    public ItemStack getApiaryOutput(ApiaryTier tier) {
        return apiaryCombs().get(tier).create();
    }

    /**
     * When the {@link #hiveCombs} list is less than 5 the list will be expanded to a size of 5 using the last entry in the list.
     * When the {@link #hiveCombs} list is empty, then a list will be created using the default comb. The {@link #defaultComb} <b>MUST</b>
     * be provided for the list to be created!
     * */
    private static Map<BeehiveTier, ItemStackTemplate> fixHiveCombs(Map<BeehiveTier, ItemStackTemplate> hiveCombs, Optional<ItemStackTemplate> defaultComb) {
        ItemStackTemplate lastStack = defaultComb.orElse(null);
        if (lastStack == null && hiveCombs.isEmpty()) throw new IllegalArgumentException("HiveCombs list can't be empty without a default comb supplied!!");
        for (BeehiveTier tier : BeehiveTier.values()) {
            ItemStackTemplate comb = hiveCombs.get(tier);
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
    private static Map<ApiaryTier, ItemStackTemplate> fixApiaryCombs(String id, Map<ApiaryTier, ItemStackTemplate> apiaryCombs, Optional<ItemStackTemplate> defaultComb, Optional<ItemStackTemplate> defaultCombBlock) {
        ItemStackTemplate lastStack = null;
        boolean wasEmpty = apiaryCombs.isEmpty();
        if (wasEmpty) HoneycombRegistry.get().validateDefaults(id, defaultComb, defaultCombBlock);
        for (ApiaryTier tier : ApiaryTier.values()) {
            ItemStackTemplate comb = apiaryCombs.get(tier);
            if (comb != null) {
                lastStack = comb;
                continue;
            }
            if (lastStack == null || wasEmpty) {
                //We check if it was empty to make sure it with create the proper list based off the config value instead
                //of copying the first config value to all slots.
                //noinspection OptionalGetWithoutIsPresent
                lastStack = tier.outputType().get().isComb() ? defaultComb.get() : defaultCombBlock.get();
                lastStack.withCount(tier.outputAmount().getAsInt());
            }
            apiaryCombs.put(tier, lastStack);
        }
        return Collections.unmodifiableMap(apiaryCombs);
    }

    public Component getDisplayName() {
        return Component.translatable("comb_type.resourcefulbees." + id());
    }
}
